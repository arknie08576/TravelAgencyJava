package pl.travelagency.db;

import jakarta.persistence.*;

@Entity
@Table(name = "reservations")
public class ReservationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer adultsNumber;

    @Column(nullable = false)
    private Integer kidsNumber;

    @Column(nullable = false)
    private Integer pricePaid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id")
    private TripEntity trip;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getAdultsNumber() { return adultsNumber; }
    public void setAdultsNumber(Integer adultsNumber) { this.adultsNumber = adultsNumber; }
    public Integer getKidsNumber() { return kidsNumber; }
    public void setKidsNumber(Integer kidsNumber) { this.kidsNumber = kidsNumber; }
    public Integer getPricePaid() { return pricePaid; }
    public void setPricePaid(Integer pricePaid) { this.pricePaid = pricePaid; }
    public TripEntity getTrip() { return trip; }
    public void setTrip(TripEntity trip) { this.trip = trip; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
}