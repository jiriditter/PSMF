package com.developer.psmf;

public class _config {
    private static final String URL_hosting = "https://api.psmf.hrajfrisbee.cz/api/v1/";
    private static final String URL_hledejtym = "teams-by-name?token=&name=";
    private static final String URL_teamdetails = "team-details?token=&year=";
    private static final String dotaz_hriste = "fields?token=";
    private static final String dotaz_TeamStandings = "table?token=&year=";

    private static String URL_hanspaulka = "teams?token=&competition=1&season=";
    private static String URL_veterani = "teams?token=&competition=2&season=";
    private static String URL_superveterani = "teams?token=&competition=3&season=";
    private static String URL_ultraveterani = "teams?token=&competition=4&season=";

    public static String getURL_hosting() {
        return URL_hosting;
    }

    public static String getURL_hledejtym() {
        return URL_hledejtym;
    }

    public static String getURL_hanspaulka() {
        return URL_hanspaulka;
    }

    public static String getURL_veterani() {
        return URL_veterani;
    }

    public static String getURL_superveterani() {
        return URL_superveterani;
    }

    public static String getURL_ultraveterani() {
        return URL_ultraveterani;
    }

    public static String getURL_teamdetails() {
        return URL_teamdetails;
    }

    public static String getDotaz_hriste() {
        return dotaz_hriste;
    }

    public static String getDotaz_TeamStandings() {
        return dotaz_TeamStandings;
    }
}
