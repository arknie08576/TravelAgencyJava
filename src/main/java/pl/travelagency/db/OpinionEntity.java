package pl.travelagency.db;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "opinions")
public class OpinionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id", unique = true)
    private ReservationEntity reservation;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public ReservationEntity getReservation() { return reservation; }
    public void setReservation(ReservationEntity reservation) { this.reservation = reservation; }
}