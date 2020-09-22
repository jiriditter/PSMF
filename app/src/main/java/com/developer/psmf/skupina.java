package com.developer.psmf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class skupina{
    private String www;
    private char skupina;
    private int liga, color;
    private int team_id;
    private String datum; //datum zapasu

    private int competition, season, year;

    public void setCompetition(int competition) {
        this.competition = competition;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCompetition() {
        return competition;
    }

    public int getSeason() {
        return season;
    }

    public int getYear() {
        return year;
    }

    public void setWww(String www) {
        this.www = www;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getWww() {
        return www;
    }

    public int getColor() {
        return color;
    }

    private String nazev;
    private int teamscount;

    public void setSkupina(char skupina) {
        this.skupina = skupina;
    }

    public void setLiga(int liga) {
        this.liga = liga;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public void setTeamscount(int teamscount) {
        this.teamscount = teamscount;
    }

    public char getSkupina() {
        return skupina;
    }

    public int getLiga() {
        return liga;
    }

    public int getTeam_id() {
        return team_id;
    }

    public String getNazev() {
        return nazev;
    }

    public int getTeamscount() {
        return teamscount;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }
}
