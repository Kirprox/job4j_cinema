package ru.job4j.cinema.dto;

import java.time.LocalDateTime;

public class FilmSessionDto {
    private int id;
    private String film;
    private String hall;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int price;

    public FilmSessionDto(int id, String film, String hall, LocalDateTime startTime,
                          LocalDateTime endDime, int price) {
        this.id = id;
        this.film = film;
        this.hall = hall;
        this.startTime = startTime;
        this.endTime = endDime;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilm() {
        return film;
    }

    public void setFilm(String film) {
        this.film = film;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndDime() {
        return endTime;
    }

    public void setEndDime(LocalDateTime endDime) {
        this.endTime = endDime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
