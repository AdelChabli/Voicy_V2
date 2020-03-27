package com.example.voicy_v2.interfaces;

import org.json.JSONArray;

public interface CallbackServer
{
    void executeAfterResponseServer(JSONArray response);
    void exercuceAfterErrorServer(String error);
}
