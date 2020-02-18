package com.example.voicy_v2.model;

import android.content.Context;

import com.example.voicy_v2.interfaces.CallbackServer;

import java.util.HashMap;

public abstract class ServerRequest
{
    protected Context context;
    protected CallbackServer callback;

    public ServerRequest(Context context, CallbackServer callback)
    {
        this.context = context;
        this.callback = callback;
    }

    public abstract void sendHttpsRequest(HashMap<String, String> parametres);
}
