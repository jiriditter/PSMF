package com.developer.psmf;

public class _hriste {
    private String zkratka, jmeno, adresa, popis;
    private String LAT, LONG;
    private int active;

    public _hriste() {
    }

    public String getZkratka() {
        return zkratka;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getPopis() {
        return popis;
    }

    public String getLAT() {
        return LAT;
    }

    public String getLONG() {
        return LONG;
    }

    public int getActive() {
        return active;
    }

    public void setZkratka(String zkratka) {
        this.zkratka = zkratka;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public void setLONG(String LONG) {
        this.LONG = LONG;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
