package com.example.voicy_v2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.voicy_v2.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AffichageExerciceActivity extends AppCompatActivity
{
    private String exerciceFolderName;
    private TextView txtAffichage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_exercice);

        txtAffichage = findViewById(R.id.resultAffichage);

        // Permet de récuperer le paramètre envoyer par l'activité précédente
        Bundle param = getIntent().getExtras();
        exerciceFolderName = param.getString("folder");

        txtAffichage.setText(exerciceFolderName);
    }

    private String getJsonStringOfResult(String fileName)
    {
        String result = "";

        try {
            File testDoc = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(testDoc));
            String line = "";
            while((line =reader.readLine()) != null) {
                result = line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
