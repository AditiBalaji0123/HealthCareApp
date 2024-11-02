package com.aditi.miniproject;

public class Assessment {
    private String height;
    private String weight;
    private String notes;
    private String date;

    public Assessment() {
    }

    public Assessment(String height, String weight, String notes, String date) {
        this.height = height;
        this.weight = weight;
        this.notes = notes;
        this.date = date;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
