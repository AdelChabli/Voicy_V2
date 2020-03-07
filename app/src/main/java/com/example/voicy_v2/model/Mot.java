package com.example.voicy_v2.model;

public class Mot
{
    private String mot;
    private String phonemes;

    public Mot(String m, String p)
    {
        mot = m;
        phonemes = p;
    }

    public String getMot() {
        return mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public String getPhonemes() {
        return phonemes;
    }

    public void setPhonemes(String phonemes) {
        this.phonemes = phonemes;
    }
}
