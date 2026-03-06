package pl.travelagency.dto;

public class PutReservationByIdRequest {
    private Integer reservationId;
    private Integer tripId;
    private Integer userId;
    private Integer adultsNumber;
    private Integer kidsNumber;
    private Integer pricePaid;

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAdultsNumber() {
        return adultsNumber;
    }

    public void setAdultsNumber(Integer adultsNumber) {
        this.adultsNumber = adultsNumber;
    }

    public Integer getKidsNumber() {
        return kidsNumber;
    }

    public void setKidsNumber(Integer kidsNumber) {
        this.kidsNumber = kidsNumber;
    }

    public Integer getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(Integer pricePaid) {
        this.pricePaid = pricePaid;
    }
}