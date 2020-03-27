package com.example.voicy_v2.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.voicy_v2.interfaces.CallbackServer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerRequest
{
    protected Context context;
    protected CallbackServer callback;
    public static final String URL_SERVER_LOGATOME = "http://pedago.univ-avignon.fr:3211";
    public static final String URL_SERVER_PHRASE = "http://pedago.univ-avignon.fr:3211";

    public ServerRequest(Context context, CallbackServer callback)
    {
        this.context = context;
        this.callback = callback;
    }

    public void sendHttpsRequest(JSONArray jsonArrayParam, String URL, int timeout)
    {
        final ProgressDialog dialog = ProgressDialog.show(context, null, "Traitement de la production de parole en cours ...");

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, jsonArrayParam,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();

                        callback.executeAfterResponseServer(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        dialog.dismiss();

                        // TODO Ecrire le code pour onError
                    }
                });

        // Paramètre la requête
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Lance la requête
        queue.add(jsonArrayRequest);
    }
}
