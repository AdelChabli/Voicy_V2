package com.example.voicy_v2.model;

import java.util.ArrayList;
import java.util.List;

public class Logatome
{
    private String logatomeName;
    private List<Phoneme> listePhoneme = new ArrayList<>();
    private String scoreNonContraint;
    private String scoreContraint;

    public List<Phoneme> getListePhoneme() {
        return listePhoneme;
    }

    public Logatome(String name, String scoreNC, String SC)
    {
        this.logatomeName = name;
        this.scoreNonContraint = scoreNC;
        this.scoreContraint = SC;
    }

    public void addPhoneme(String name, String debut, String fin, String contraint, String nonContraint)
    {
        this.listePhoneme.add(new Phoneme(name,debut,fin, contraint, nonContraint));
    }


    public String getLogatomeName() {
        return logatomeName;
    }

    public void setLogatomeName(String logatomeName) {
        this.logatomeName = logatomeName;
    }

    public String getScoreNonContraint() {
        return scoreNonContraint;
    }

    public void setScoreNonContraint(String scoreNonContraint) {
        this.scoreNonContraint = scoreNonContraint;
    }


    public String getScoreContraint() {
        return scoreContraint;
    }
}
