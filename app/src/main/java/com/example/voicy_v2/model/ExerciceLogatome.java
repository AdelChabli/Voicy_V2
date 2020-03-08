package com.example.voicy_v2.model;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class ExerciceLogatome extends Exercice
{

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ExerciceLogatome(int nb, Context c)
    {
        super(c);
        totalIteration = nb;

        // Randomise un chiffre entre 1 et 4 (pour selectionner la liste des logatomes à prendre dans assets)
        Random random = new Random();
        int num = random.nextInt((4 - 1) + 1) + 1;

        // Va récuperer les nonmots de la liste
        recupereElementExercice("Liste" + num);

        directoryName = getExerciceDirectory();

        DirectoryManager.getInstance().createFolder(DirectoryManager.OUTPUT_RESULTAT + "/" + directoryName);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void recupereElementExercice(String fileName)
    {
        // Vide la liste (au cas où)
        listeElement.clear();

        String[] res = null;

        // buffer sur la liste présentes dans assets
        try(BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName))))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                // split de la ligne par rapport à une tabulation
                res = line.split("\t");

                // Ajout du mot dans l'arrayList avec en param 1 = mot et param 2 = phoneme
                listeElement.add(new Mot(res[0], res[1]));
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        // Un mélange de la liste finale
        Collections.shuffle(listeElement);
    }

    @Override
    protected String getExerciceDirectory()
    {
        String direcName = "Logatome_";

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        direcName += currentDateandTime;

        //Log.d("logATOM", "Directory : " + direcName);

        return direcName;
    }
}
