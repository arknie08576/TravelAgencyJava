package pl.travelagency.dto;

import java.time.LocalDate;

public class TripDto {
    private Integer id;
    private String hotelName;
    private String hotelDescription;
    private String country;
    private String city;
    private Integer pricePerAdult;
    private Integer pricePerKid;
    private LocalDate startDate;
    private LocalDate stopDate;
    private String departure;
    private String food;
    private String requiredDocuments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelDescription() {
        return hotelDescription;
    }

    public void setHotelDescription(String hotelDescription) {
        this.hotelDescription = hotelDescription;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPricePerAdult() {
        return pricePerAdult;
    }

    public void setPricePerAdult(Integer pricePerAdult) {
        this.pricePerAdult = pricePerAdult;
    }

    public Integer getPricePerKid() {
        return pricePerKid;
    }

    public void setPricePerKid(Integer pricePerKid) {
        this.pricePerKid = pricePerKid;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStopDate() {
        return stopDate;
    }

    public void setStopDate(LocalDate stopDate) {
        this.stopDate = stopDate;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(String requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }
}