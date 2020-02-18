package com.example.voicy_v2.model;

import android.content.Context;

import com.example.voicy_v2.interfaces.CallbackServer;

import java.util.HashMap;
import java.util.List;

public class RequestPhoneme extends ServerRequest
{
    private static final String URL_REQUEST = "xxxxxx";
    private static final int ID_SERVER = 0;

    public RequestPhoneme(Context context, CallbackServer callbackServer)
    {
        super(context, callbackServer);
    }

    @Override
    public void sendHttpsRequest(HashMap<String, String> parametres)
    {
        // Librairie de communication : https://developer.android.com/training/volley
        // A implémenter avec doc officiel (le top est de regarder des exemples de gens qui l'utilise ;) )

        // Le corps du truc en gros :
        // utilisation de StringRequest pour faire la requête

        // onResponse
        String response = "reponseDuServer";
        callback.executeAfterResponseServer(response, ID_SERVER);

        // onErrorResponse
        // Traitement

        //getParams
        // Envoie des paramètres de la hashmap

        // Définition du comportement de la requête
        //stringRequest.setRetryPolicy(timeout, numRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        // timeout = temps où le client va attendre exprimé en milisecondes avant de considérer que la requête a échoué si aucune réponse
        // numRetries = Nombre de fois où la requête va être réenvoyer en cas de timeout (mieux vaux le laisser à 0 sinon ça fait des conflits par moment)

        // Envoie de la requête
    }
}
