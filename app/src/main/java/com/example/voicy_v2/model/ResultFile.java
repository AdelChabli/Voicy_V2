package com.example.voicy_v2.model;

import android.util.Log;

import java.io.Serializable;

public class ResultFile implements Serializable
{
    private String pathResult = DirectoryManager.OUTPUT_RESULTAT;
    private String nameFile;
    private String date;
    private String hour;
    private String genre;

    public ResultFile(String name)
    {
        this.nameFile = name;

        formatDataFromNameFolder();
    }

    public ResultFile(String name, String d, String h, String g)
    {
        this.nameFile = name;
        this.date = d;
        this.hour = h;
        this.genre = g;
    }

    private void formatDataFromNameFolder()
    {
        String laDate = "" , lheure = "", leGenre = "";

        String chaine = this.nameFile;

        laDate = chaine.substring(chaine.indexOf("_") + 1, chaine.indexOf("_") + 3) + "/" +
                chaine.substring(chaine.indexOf("_") + 3, chaine.indexOf("_") + 5) + "/" +
                chaine.substring(chaine.indexOf("_") + 5, chaine.indexOf("_") + 7);

        chaine = chaine.substring(chaine.indexOf("_") + 1);

        lheure = chaine.substring(chaine.indexOf("_") + 1, chaine.indexOf("_") + 3) + ":" +
                chaine.substring(chaine.indexOf("_") + 3, chaine.indexOf("_") + 5) + ":" +
                chaine.substring(chaine.indexOf("_") + 5, chaine.indexOf("_") + 7);

        this.date = laDate;
        this.hour = lheure;
        this.genre = chaine.substring(chaine.lastIndexOf("_") + 1);

    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
