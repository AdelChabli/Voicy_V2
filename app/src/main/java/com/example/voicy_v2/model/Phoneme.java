package com.example.voicy_v2.model;

public class Phoneme
{
    private String phoneme;
    private String debut;
    private String fin;
    private String scoreContraint;
    private String scoreNonContraint;

    public Phoneme(String lePhoneme, String leDebut, String laFin, String contraint, String nonContraint)
    {
        this.phoneme = lePhoneme;
        this.debut = leDebut;
        this.fin = laFin;
        this.scoreContraint = contraint;
        this.scoreNonContraint = nonContraint;
    }

    public String getPhoneme() {
        return phoneme;
    }

    public void setPhoneme(String phoneme) {
        this.phoneme = phoneme;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getScoreContraint() {
        return scoreContraint;
    }

    public void setScoreContraint(String scoreContraint) {
        this.scoreContraint = scoreContraint;
    }

    public String getScoreNonContraint() {
        return scoreNonContraint;
    }

    public void setScoreNonContraint(String scoreNonContraint) {
        this.scoreNonContraint = scoreNonContraint;
    }

    public String getPhonemeToString()
    {
        return this.phoneme + " -> " + this.debut + "/" + this.fin + " | AC: " + this.scoreContraint + " | NC: " + this.scoreNonContraint;
    }
}
