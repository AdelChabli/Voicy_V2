package com.example.voicy_v2.model;

import android.util.Log;

import java.io.Serializable;

public class ResultFile implements Serializable
{
    private String pathResult = DirectoryManager.OUTPUT_RESULTAT;
    private String nameFile;
    private String date;
    private String hour;

    public ResultFile(String name)
    {
        this.nameFile = name;

        formatDataFromNameFolder();
    }

    public ResultFile(String name, String d, String h)
    {
        this.nameFile = name;
        this.date = d;
        this.hour = h;
    }

    private void formatDataFromNameFolder()
    {
        String laDate = "" , lheure = "";

        String chaine = this.nameFile;

        laDate = chaine.substring(chaine.indexOf("_") + 1, chaine.indexOf("_") + 3) + "/" +
                chaine.substring(chaine.indexOf("_") + 3, chaine.indexOf("_") + 5) + "/" +
                chaine.substring(chaine.indexOf("_") + 5, chaine.indexOf("_") + 7);

        chaine = chaine.substring(chaine.indexOf("_") + 1);

        lheure = chaine.substring(chaine.indexOf("_") + 1, chaine.indexOf("_") + 3) + ":" +
                chaine.substring(chaine.indexOf("_") + 3, chaine.indexOf("_") + 5) + ":" +
                chaine.substring(chaine.indexOf("_") + 5, chaine.indexOf("_") + 7);

        //Log.d("listResult", laDate);
        //Log.d("listResult", lheure);

        this.date = laDate;
        this.hour = lheure;

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
}
