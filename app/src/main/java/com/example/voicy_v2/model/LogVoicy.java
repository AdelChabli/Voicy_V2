package com.example.voicy_v2.model;

import android.util.Log;

public class LogVoicy
{
    public static String LOG_INFO = "infoLog";
    public static String LOG_ERROR = "errorLog";
    public static boolean LOG_INFO_IS_ACTIVATED = true;
    public static boolean LOG_ERROR_IS_ACTIVATED  = true;

    // ----------------------- SINGLETON -----------------
    private static LogVoicy INSTANCE = null;

    private LogVoicy() {}

    public static synchronized LogVoicy getInstance()
    {
        if (INSTANCE == null)
        {   INSTANCE = new LogVoicy();
        }
        return INSTANCE;
    }
    // ----------------------------------------------------

    public void createLogInfo(String info)
    {
        if(LOG_INFO_IS_ACTIVATED)
        {
            Log.d(LOG_INFO, info);
        }
    }

    public void createLogError(String error)
    {
        if(LOG_ERROR_IS_ACTIVATED)
        {
            Log.e(LOG_ERROR, error);
        }
    }
}
