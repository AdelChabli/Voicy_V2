package com.example.voicy_v2.model;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO A implementer
public class ExercicePhrase extends Exercice
{
    public ExercicePhrase(int nb, String leGenre, Context c)
    {
        super(c);

        totalIteration = nb;
        genre = leGenre;

        // Va récupérer les phrases
        recupereElementExercice("chevre.txt");

        directoryName = getExerciceDirectory();

        DirectoryManager.getInstance().createFolder(DirectoryManager.OUTPUT_RESULTAT + "/" + directoryName);
    }

    @Override
    protected void recupereElementExercice(String fileName)
    {
        listeElement.clear();

        String[] res = null;

        int i = 0;

        // buffer sur la liste présentes dans assets
        try(BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName))))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                LogVoicy.getInstance().createLogInfo(line);

                // split de la ligne par rapport à une tabulation
                res = line.split("\t");

                // Ajout du mot dans l'arrayList avec en param 1 = mot et param 2 = phoneme
                listeElement.add(new Mot(res[0], ""));      //TODO attention j'ai viré res[1]

                i++;

                if(i == totalIteration)
                    break;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected String getExerciceDirectory()
    {
        String direcName = "Phrase_";

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        direcName += currentDateandTime;

        direcName += "_" + genre;

        return direcName;
    }
}
