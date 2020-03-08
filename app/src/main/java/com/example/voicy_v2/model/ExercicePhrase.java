package com.example.voicy_v2.model;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

// TODO A implementer
public class ExercicePhrase extends Exercice
{
    public ExercicePhrase(int nb, Context c)
    {
        super(c);
        totalIteration = nb;
    }

    @Override
    protected void recupereElementExercice(String f) {

    }

    @Override
    protected String getExerciceDirectory()
    {
        String direcName = "Phrase_";

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        direcName += currentDateandTime;

        //Log.d("logATOM", "Directory : " + direcName);

        return direcName;
    }
}
