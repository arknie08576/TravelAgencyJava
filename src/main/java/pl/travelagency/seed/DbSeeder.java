package pl.travelagency.seed;

import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.travelagency.db.*;
import pl.travelagency.repo.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Component
public class DbSeeder implements ApplicationRunner {

    private final UserRepository userRepo;
    private final TripRepository tripRepo;
    private final ReservationRepository reservationRepo;
    private final OpinionRepository opinionRepo;

    public DbSeeder(
            UserRepository userRepo,
            TripRepository tripRepo,
            ReservationRepository reservationRepo,
            OpinionRepository opinionRepo
    ) {
        this.userRepo = userRepo;
        this.tripRepo = tripRepo;
        this.reservationRepo = reservationRepo;
        this.opinionRepo = opinionRepo;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        System.out.println("DB SEEDER STARTED");
        // jak w C#: jeśli coś jest -> nie seedujemy
        boolean hasAny = userRepo.count() > 0
                || tripRepo.count() > 0
                || reservationRepo.count() > 0
                || opinionRepo.count() > 0;
        if (hasAny) return;
        System.out.println("DATABASE IS EMPTY, SEEDING...");
        seed(60, 90, 220, 0.55);
    }

    private void seed(int usersCount, int tripsCount, int reservationsCount, double opinionShare) {

        // deterministyczny random jak w C# Randomizer.Seed = new Random(12345)
        Random rnd = new Random(12345);
        Faker faker = new Faker(new Locale("pl", "PL"), rnd);

        record Dest(String country, String city) {}
        List<Dest> destinations = List.of(
                new Dest("Hiszpania","Barcelona"), new Dest("Hiszpania","Majorka"), new Dest("Hiszpania","Teneryfa"),
                new Dest("Grecja","Ateny"), new Dest("Grecja","Santorini"), new Dest("Grecja","Kreta"),
                new Dest("Włochy","Rzym"), new Dest("Włochy","Neapol"), new Dest("Włochy","Sycylia"),
                new Dest("Portugalia","Lizbona"), new Dest("Portugalia","Porto"),
                new Dest("Francja","Paryż"), new Dest("Cypr","Larnaka"), new Dest("Turcja","Antalya"),
                new Dest("Egipt","Hurghada"), new Dest("Egipt","Marsa Alam")
        );

        String[] departures = {"Warszawa (WAW)", "Kraków (KRK)", "Gdańsk (GDN)", "Wrocław (WRO)", "Poznań (POZ)", "Katowice (KTW)"};
        String[] foods = {"All Inclusive", "HB (śniadanie+obiadokolacja)", "BB (tylko śniadanie)", "SC (bez wyżywienia)"};
        String[] hotels = {"Grand Plaza", "Sea Breeze Resort", "City Center Inn", "Riviera Palace",
                "Boutique Corner", "Skyline Hotel", "Old Town Suites", "Sunset Bay", "Blue Lagoon", "Royal Gardens"};

        // hasło jak w C#: jednorazowy salt z SecureRandom, PBKDF2 HMACSHA1 10000 iteracji 256-bit
        String commonPassword = saltedPbkdf2Password("12345");

        // --- Users ---
        List<UserEntity> users = new ArrayList<>();
        for (int i = 0; i < usersCount; i++) {
            var u = new UserEntity();
            u.setName(faker.name().firstName());
            u.setSurname(faker.name().lastName());

            String baseLogin = toSlug(u.getName()) + "." + toSlug(u.getSurname());
            u.setLogin(baseLogin + (10 + rnd.nextInt(990)));

            u.setPassword(commonPassword);
            u.setEmail(toSlug(u.getName()) + "." + toSlug(u.getSurname()) + (10 + rnd.nextInt(990)) + "@example.com");

            // 10% admin, reszta user
            u.setRole(rnd.nextDouble() < 0.10 ? UserRole.admin : UserRole.user);

            users.add(u);
        }

        // unikalność email/login jak w C# (dosztukujemy sufiksy jeśli konflikt)
        users = ensureUnique(users);

        userRepo.saveAll(users);

        // --- Trips ---
        LocalDate today = LocalDate.now();
        List<TripEntity> trips = new ArrayList<>();

        for (int i = 0; i < tripsCount; i++) {
            var t = new TripEntity();
            t.setHotelName(hotels[rnd.nextInt(hotels.length)]);
            t.setHotelDescription(faker.lorem().paragraph(1));

            var dest = destinations.get(rnd.nextInt(destinations.size()));
            t.setCountry(dest.country());
            // dopasuj miasto do kraju
            var cities = destinations.stream().filter(d -> d.country().equals(t.getCountry())).map(Dest::city).toList();
            t.setCity(cities.get(rnd.nextInt(cities.size())));

            t.setDeparture(departures[rnd.nextInt(departures.length)]);
            t.setFood(foods[rnd.nextInt(foods.length)]);
            t.setRequiredDocuments(docsFor(t.getCountry()));

            int priceAdult = 1600 + rnd.nextInt(5200 - 1600 + 1);
            t.setPricePerAdult(priceAdult);
            double kidFactor = 0.45 + rnd.nextDouble() * (0.70 - 0.45);
            t.setPricePerKid((int)Math.round(priceAdult * kidFactor));

            boolean biasFuture = rnd.nextDouble() < 0.65;
            int startShift = biasFuture ? (7 + rnd.nextInt(150 - 7 + 1)) : -(15 + rnd.nextInt(120 - 15 + 1));
            LocalDate start = today.plusDays(startShift);
            t.setStartDate(start);

            int length = 4 + rnd.nextInt(10 - 4 + 1);
            t.setStopDate(start.plusDays(length));

            trips.add(t);
        }

        tripRepo.saveAll(trips);

        // --- Reservations ---
        List<ReservationEntity> reservations = new ArrayList<>();
        for (int i = 0; i < reservationsCount; i++) {
            var r = new ReservationEntity();

            TripEntity trip = trips.get(rnd.nextInt(trips.size()));
            UserEntity user = users.get(rnd.nextInt(users.size()));
            r.setTrip(trip);
            r.setUser(user);

            int adults = 1 + rnd.nextInt(2);
            int kids = rnd.nextInt(3); // 0..2
            r.setAdultsNumber(adults);
            r.setKidsNumber(kids);

            int basePrice = adults * trip.getPricePerAdult() + kids * trip.getPricePerKid();
            double discount = rnd.nextDouble() * 0.12; // 0..12%
            r.setPricePaid((int)Math.round(basePrice * (1.0 - discount)));

            reservations.add(r);
        }

        reservationRepo.saveAll(reservations);

        // --- Opinions (tylko dla zakończonych) ---
        List<ReservationEntity> ended = new ArrayList<>(
                reservations.stream()
                        .filter(r -> !r.getTrip().getStopDate().isAfter(today))
                        .toList()
        );

        int opinionsCount = (int)Math.round(ended.size() * opinionShare);
        Collections.shuffle(ended, rnd);
        List<ReservationEntity> reviewed = ended.stream().limit(opinionsCount).toList();

        String[] good = {
                "Świetna organizacja, hotel zgodny z opisem.",
                "Wszystko na plus: jedzenie, obsługa, przeloty.",
                "Bardzo dobry stosunek jakości do ceny.",
                "Czysty hotel, rewelacyjna lokalizacja."
        };
        String[] mixed = {
                "Ok, ale transfer mógłby być lepiej zorganizowany.",
                "Pokój mniejszy niż na zdjęciach, ale czysty.",
                "Fajne wycieczki fakultatywne, jedzenie przeciętne."
        };
        String[] bad = {
                "Hałas w nocy, słaba klimatyzacja.",
                "Opóźniony lot i przeciętne jedzenie w hotelu."
        };

        List<OpinionEntity> opinions = new ArrayList<>();
        for (var res : reviewed) {
            var o = new OpinionEntity();

            int rating = weightedPick(rnd, new int[]{3,4,5}, new double[]{1,3,5});
            o.setRating(rating);

            String desc;
            if (rating >= 5) desc = good[rnd.nextInt(good.length)];
            else if (rating == 4) desc = pickFrom(rnd, concat(good, mixed));
            else if (rating == 3) desc = pickFrom(rnd, concat(mixed, bad));
            else desc = bad[rnd.nextInt(bad.length)];

            o.setDescription(desc);

            LocalDate min = res.getTrip().getStopDate().plusDays(1);
            LocalDate max = res.getTrip().getStopDate().plusDays(30);
            long range = Math.max(0, max.toEpochDay() - min.toEpochDay());
            o.setDate(min.plusDays(range == 0 ? 0 : rnd.nextInt((int)range + 1)));

            o.setReservation(res);
            opinions.add(o);
        }

        opinionRepo.saveAll(opinions);
    }

    private static String saltedPbkdf2Password(String raw) {
        try {
            byte[] salt = new byte[16]; // 128-bit
            new SecureRandom().nextBytes(salt);

            var spec = new PBEKeySpec(raw.toCharArray(), salt, 10_000, 256);
            var skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] key = skf.generateSecret(spec).getEncoded();

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(key);
            return saltB64 + ";" + hashB64;
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 error", e);
        }
    }

    private static String docsFor(String country) {
        if (country.equals("Egipt") || country.equals("Turcja")) {
            return "Paszport (min. 6 mies. ważności), wiza (jeśli wymagana)";
        }
        return "Dowód osobisty lub paszport (UE/Schengen)";
    }

    private static List<UserEntity> ensureUnique(List<UserEntity> users) {
        // email unique
        Map<String, Integer> emailCount = new HashMap<>();
        for (var u : users) {
            int idx = emailCount.merge(u.getEmail(), 1, Integer::sum) - 1;
            if (idx > 0) u.setEmail(u.getEmail().replace("@", "+" + idx + "@"));
        }
        // login unique
        Map<String, Integer> loginCount = new HashMap<>();
        for (var u : users) {
            int idx = loginCount.merge(u.getLogin(), 1, Integer::sum) - 1;
            if (idx > 0) u.setLogin(u.getLogin() + idx);
        }
        return users;
    }

    private static String toSlug(String s) {
        String x = s.trim().toLowerCase(Locale.ROOT);
        x = x.replace('ą','a').replace('ć','c').replace('ę','e').replace('ł','l')
                .replace('ń','n').replace('ó','o').replace('ś','s').replace('ż','z').replace('ź','z');
        StringBuilder sb = new StringBuilder();
        for (char ch : x.toCharArray()) {
            if (Character.isLetterOrDigit(ch) || ch == '.') sb.append(ch);
        }
        return sb.toString();
    }

    private static int weightedPick(Random rnd, int[] values, double[] weights) {
        double sum = 0;
        for (double w : weights) sum += w;
        double r = rnd.nextDouble() * sum;
        double acc = 0;
        for (int i = 0; i < values.length; i++) {
            acc += weights[i];
            if (r <= acc) return values[i];
        }
        return values[values.length - 1];
    }

    private static String[] concat(String[] a, String[] b) {
        String[] out = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    private static String pickFrom(Random rnd, String[] arr) {
        return arr[rnd.nextInt(arr.length)];
    }
}
