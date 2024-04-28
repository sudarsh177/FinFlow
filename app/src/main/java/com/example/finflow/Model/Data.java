package com.example.finflow.Model;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private int amount;
    private String categories;
    private String note;
    private String id;
    private String date;

    private String monthYear;

    private int day;
    private int month;
    private int year;


    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public Data(int amount, String categories, String note, String id, String date, int day, int month, int year, String monthYear) {
        this.amount = amount;
        this.categories = categories;
        this.note = note;
        this.id = id;
        this.date=date;
        this.month = month;
        this.year = year;
        this.monthYear = monthYear;

    }

    @Override
    public String toString() {
        return "Data{" +
                "amount=" + amount +
                ", categories='" + categories + '\'' +
                ", note='" + note + '\'' +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAmount() {
        return (int) amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategories() {
        return categories;
    }

    public void setType(String categories) {
        this.categories = categories;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Data() {
    }
}
