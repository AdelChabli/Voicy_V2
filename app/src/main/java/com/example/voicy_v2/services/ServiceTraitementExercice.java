package com.example.voicy_v2.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.voicy_v2.R;
import com.example.voicy_v2.activity.MainActivity;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.LogVoicy;
import com.example.voicy_v2.model.RequestServer;
import com.example.voicy_v2.model.ResultFile;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static com.example.voicy_v2.services.App.CHANNEL_ID;

public class ServiceTraitementExercice extends Service implements CallbackServer
{
    private int nombreTraitement = 0, nombreReussi = 0, nombreEchouer = 0;
    private CallbackServer callbackServer = ServiceTraitementExercice.this;
    private String[] listeExo;
    private int position = 0;
    private ResultFile exercice;
    public static boolean WORKING_ON = false;
    private NotificationManager notificationManager;
    private int progressIteration;
    private int currentProgress = 0;
    private NotificationCompat.Builder notification;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        final int progressMax = 100;

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Voicy Service")
                .setContentText("Traitement exercice en cours ...")
                .setProgress(progressMax,0,false)
                .setSmallIcon(R.drawable.ic_record_voice)
                .setOngoing(true)
                .setOnlyAlertOnce(true);

        startForeground(1, notification.build());

        new Thread(new Runnable() {
            public void run()
            {
                TraiterLesExercices();
            }
        }).start();

        return START_REDELIVER_INTENT;
    }

    private void TraiterLesExercices()
    {
        WORKING_ON = true;

        Intent dialogIntent = new Intent(this, MainActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);

        File traitementFolder = new File(DirectoryManager.OUTPUT_ATTENTE);

        listeExo = traitementFolder.list();
        nombreTraitement = listeExo.length;

        progressIteration = 100 / nombreTraitement;

        traiterExercice(listeExo[position]);

    }

    private void traiterExercice(String exoName)
    {
        exercice = new ResultFile(exoName);

        HashMap<String, String> params = getParamsOfExercice(exercice.getNameFile());

        RequestServer requestLogatome = new RequestServer(this, callbackServer);

        if(exercice.getNameFile().toLowerCase().contains("p"))
        {
            requestLogatome.sendHttpsRequestService(params, "phrase");
        }
        else
        {
            requestLogatome.sendHttpsRequestService(params, "logatome");
        }
    }

    private HashMap<String, String> getParamsOfExercice(String nameFile)
    {
        String sourceDirectory = DirectoryManager.OUTPUT_ATTENTE + "/" + nameFile;

        String paramString = "";

        try {
            File file = new File(sourceDirectory + "/temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            while((line =reader.readLine()) != null)
            {
                paramString += line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogVoicy.getInstance().createLogInfo(paramString);

        paramString = paramString.substring(1);
        paramString = paramString.substring(0, paramString.length() - 1);
        paramString = paramString.replaceAll(", ", ",");

        LogVoicy.getInstance().createLogInfo(paramString);

        HashMap<String, String> params = new HashMap<>();

        String[] pairs = paramString.split(",");


        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            LogVoicy.getInstance().createLogInfo(pair);
            String[] keyValue = pair.split("=");
            params.put(keyValue[0], keyValue[1]);
        }

        LogVoicy.getInstance().createLogInfo(params.get("gender"));
        LogVoicy.getInstance().createLogInfo(params.get("type"));

        return params;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void executeAfterResponseServer(final JSONArray response)
    {
        nombreReussi++;
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run()
            {
                String sourceDirectory = DirectoryManager.OUTPUT_ATTENTE + "/" + exercice.getNameFile();

                DirectoryManager.getInstance().createFileOnDirectory(sourceDirectory, "resultat.txt", response.toString());

                DirectoryManager.getInstance().cutAndPastFolderToAnother(sourceDirectory, DirectoryManager.OUTPUT_RESULTAT);

                exerciceSuivant();
            }
        });
    }

    @Override
    public void exercuceAfterErrorServer(String error)
    {
        nombreEchouer++;
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run()
            {
                exerciceSuivant();
            }
        });
    }

    private void exerciceSuivant()
    {
        currentProgress += progressIteration;
        notification.setProgress(100, currentProgress, false);
        notificationManager.notify(1, notification.build());

        if((position + 1) > (nombreTraitement - 1))
        {
            WORKING_ON = false;

            stopSelf();

            LogVoicy.getInstance().createLogInfo("Terminer traitement");

            notifierUtilisateur();
        }
        else
        {
            LogVoicy.getInstance().createLogInfo("Traitement suivant");
            position++;
            traiterExercice(listeExo[position]);
        }
    }

    private void notifierUtilisateur()
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Voicy Service")
                .setContentText(nombreTraitement + " traitement effectués dont " + nombreEchouer + " échecs.")
                .setSmallIcon(R.drawable.ic_record_voice)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(1, notification);
    }
}
