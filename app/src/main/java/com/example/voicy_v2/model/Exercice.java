package com.example.voicy_v2.model;

import android.content.Context;

import java.util.ArrayList;

public abstract class Exercice
{
    protected int totalIteration;
    protected int actuelIteration;
    protected ArrayList<Mot> listeElement;
    protected Context context;
    protected String directoryName;

    public Exercice(Context c)
    {
        actuelIteration = 0;
        listeElement = new ArrayList<>();
        context = c;
    }

    public boolean isExerciceFinish()
    {
        if(actuelIteration == totalIteration)
            return true;

        return false;
    }

    public void nextIteration()
    {
        actuelIteration++;
    }

    public void previousIteration()
    {
        if(actuelIteration > 0)
        {
            actuelIteration--;
        }
    }

    public Mot getActuelMot()
    {
        return listeElement.get(actuelIteration);
    }

    public String getIterationSurMax() {  return (actuelIteration + 1) + "/" + totalIteration; }

    protected abstract void recupereElementExercice(String f);
    protected abstract String getExerciceDirectory();

    public int getTotalIteration() { return totalIteration;}
    public int getActuelIteration() { return actuelIteration;}
    public String getDirectoryName() { return directoryName; }
    public String getDirectoryPath() { return DirectoryManager.OUTPUT_RESULTAT + "/" + directoryName;}
}
