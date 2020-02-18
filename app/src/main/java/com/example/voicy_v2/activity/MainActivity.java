package com.example.voicy_v2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.voicy_v2.R;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.RequestPhoneme;
import com.example.voicy_v2.model.RequestPhrase;
import com.example.voicy_v2.model.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CallbackServer
{
    private ServerRequest requestPhoneme, requestPhrase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Déclaration
        requestPhoneme = new RequestPhoneme(this, MainActivity.this);
        requestPhrase = new RequestPhrase(this, MainActivity.this);

        // Remplissage des paramètres a envoyé
        HashMap<String, String> listeParametre = new HashMap<>();
        listeParametre.put("wav", "base64wavString");
        listeParametre.put("phoneme", "base64TxtFile ou directement le phoneme");

        // Envoie au serveur une requête sur les phonemes
        requestPhoneme.sendHttpsRequest(listeParametre);
    }

    @Override
    public void executeAfterResponseServer(String response, int idServer)
    {
        // serveur phoneme
        if(idServer == 0)
            traitementPhoneme(response);
        else
            traitementPhrase(response);
    }

    public void traitementPhoneme(String response) {}

    public void traitementPhrase(String response) {}
}
