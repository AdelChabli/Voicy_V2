package com.example.voicy_v2.model;

import android.content.Context;

import java.util.ArrayList;

public abstract class Exercice
{
    protected int totalIteration;
    protected int actuelIteration;
    protected String genre;
    protected ArrayList<Mot> listeElement;
    protected Context context;
    protected String directoryName;

    public Exercice(Context c)
    {
        actuelIteration = 0;
        listeElement = new ArrayList<>();
        context = c;
    }

    // Permet de déterminer si l'exercice est terminé
    public boolean isExerciceFinish()
    {
        if(actuelIteration == totalIteration)
            return true;

        return false;
    }

    // Incremente l'iteration de l'exercice
    public void nextIteration()
    {
        actuelIteration++;
    }

    // Decrémente l'iteration de l'exercice
    public void previousIteration()
    {
        if(actuelIteration > 0)
        {
            actuelIteration--;
        }
    }

    // Renvoie un objet mot correspondant à celui qui est en cours d'affichage
    public Mot getActuelMot()
    {
        return listeElement.get(actuelIteration);
    }

    // Renvoie sous format text : 1/3 ou bien 2/3 donc iteration / maxIteration
    public String getIterationSurMax() {  return (actuelIteration + 1) + "/" + totalIteration; }

    protected abstract void recupereElementExercice(String f);
    protected abstract String getExerciceDirectory();

    public int getTotalIteration() { return totalIteration;}
    public int getActuelIteration() { return actuelIteration;}
    public String getDirectoryName() { return directoryName; }

    // Renvoie le chemin complet vers le dossier de l'exercice se trouvant dans resultat
    public String getDirectoryPath() { return DirectoryManager.OUTPUT_RESULTAT + "/" + directoryName;}

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
