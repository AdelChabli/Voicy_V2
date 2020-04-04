package com.example.voicy_v2.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.voicy_v2.R;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.Exercice;
import com.example.voicy_v2.model.ExerciceLogatome;
import com.example.voicy_v2.model.ExercicePhrase;
import com.example.voicy_v2.model.LogVoicy;
import com.example.voicy_v2.model.Mot;
import com.example.voicy_v2.model.Recorder;
import com.example.voicy_v2.model.RequestServer;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ExerciceActivity extends AppCompatActivity implements CallbackServer
{
    private Toolbar toolbar;
    private Button btnAnnuler;
    private ImageButton btnNext, btnEcouter, btnRecord;
    private TextView lePrompteur, iterationEnCours;
    private Exercice exercice;
    private String typeExercice, genre;
    private int maxIteration;
    private Mot motActuel;
    private boolean isRecording = false, isListening = false;
    private Recorder record;
    MediaPlayer mp;
    private JSONObject jsonObject = new JSONObject();
    private JSONArray jsonParams = new JSONArray();
    private HashMap<String,String> params = new HashMap<>();
    private String wavLocation = "";
    private int index = 1;



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercice);

        // Initialise le prompteur
        lePrompteur = findViewById(R.id.prompteur);
        iterationEnCours = findViewById(R.id.txtNumElement);

        // Permet de récuperer le paramètre envoyer par l'activité précédente
        Bundle param = getIntent().getExtras();
        typeExercice = param.getString("type");
        maxIteration = param.getInt("iteration");
        genre = param.getString("genre"); // Homme ou Femme

        // Log
        LogVoicy.getInstance().createLogInfo("Exercice " + typeExercice);
        LogVoicy.getInstance().createLogInfo("Genre : " + genre);
        LogVoicy.getInstance().createLogInfo("MaxIteration : " + maxIteration);

        // Permet de configurer la request avec le type de l'exercice
        params.put("gender",genre);
        params.put("type",typeExercice);
        params.put("size",String.valueOf(maxIteration));

        // Permet de configurer la toolbar pour cette activité
        configOfToolbar(typeExercice);

        // Initialise les boutons et les configures
        initAllButton();

        // Lance l'exercice
        lancerExercice();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void lancerExercice()
    {
        if(typeExercice.equals("logatome"))
        {
            lePrompteur.setTextSize(46);

            exercice = new ExerciceLogatome(maxIteration, genre,this);

            record = new Recorder(this, exercice.getDirectoryPath());

            lireExercice();
        }
        else
        {
            lePrompteur.setTextSize(38);

            exercice = new ExercicePhrase(maxIteration, genre,this);

            record = new Recorder(this, exercice.getDirectoryPath());

            lireExercice();
        }
    }

    public void lireExercice()
    {
        if(!exercice.isExerciceFinish())
        {
            // Recupère le mot/phrase actuel
            motActuel = exercice.getActuelMot();

            lePrompteur.setText(motActuel.getMot());
            iterationEnCours.setText(exercice.getIterationSurMax());
        }
        else
        {
            // Exercice terminer

            // Affichage du jsonArray en log
            try {
                for(int i = 0; i < jsonParams.length(); i++)
                {
                    JSONObject leJsonObject = (JSONObject) jsonParams.get(i);
                    LogVoicy.getInstance().createLogInfo("jsonArray["+i+"] -> { 'element', '" + leJsonObject.get("element").toString() + "' },");
                    LogVoicy.getInstance().createLogInfo("jsonArray["+i+"] -> { 'wav', " + leJsonObject.get("wav").toString() + " },");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            RequestServer requestLogatome = new RequestServer(this, ExerciceActivity.this);
            requestLogatome.sendHttpsRequest(params, this.typeExercice);

        }
    }

    @Override
    public void executeAfterResponseServer(final JSONArray response)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run()
            {

                DirectoryManager.getInstance().createFileOnDirectory(exercice.getDirectoryPath(), "resultat.txt", response.toString());

                Intent intent = new Intent(ExerciceActivity.this, ResultatActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

    }

    @Override
    public void exercuceAfterErrorServer(String error)
    {
        DirectoryManager.getInstance().cutAndPastFolderToAnother(exercice.getDirectoryPath(), DirectoryManager.OUTPUT_ATTENTE);

        String pathDestination = DirectoryManager.OUTPUT_ATTENTE + "/" + exercice.getDirectoryName();

        DirectoryManager.getInstance().createFileOnDirectory(pathDestination, "temp.txt", params.toString());

        SweetAlertDialog sDialog = new SweetAlertDialog(ExerciceActivity.this, SweetAlertDialog.ERROR_TYPE);
        sDialog.setTitleText("Oups ...");
        sDialog.setContentText(error);
        sDialog.setConfirmText("Ok");
        sDialog.setCancelable(false);
        sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                finish();
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        sDialog.show();
    }

    public void initAllButton()
    {
        btnAnnuler = findViewById(R.id.buttonAnnuler);
        btnEcouter = findViewById(R.id.buttonEcouter);
        btnNext = findViewById(R.id.buttonNext);
        btnRecord = findViewById(R.id.buttonRecord);

        setVisibiliteBouton(false);

        btnAnnuler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                quitterPopup();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                if(typeExercice.equals("logatome")) {
                    params.put(exercice.getActuelMot().getMot(), getBase64FromWav(wavLocation));
                } else {
                    params.put("textScript"+index, exercice.getActuelMot().getMot());
                    params.put("input"+index, getBase64FromWav(wavLocation));
                }

                index++;
                exercice.nextIteration();
                setVisibiliteBouton(false);
                lireExercice();
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isRecording)
                {
                    btnRecord.setImageResource(R.drawable.stop);
                    isRecording = true;
                    record.startRecording();
                }
                else
                {
                    btnRecord.setImageResource(R.drawable.mic_48dp);
                    isRecording = false;

                    if(typeExercice.equals("logatome"))
                        wavLocation = record.stopRecording(exercice.getActuelMot().getMot()+".wav");
                    else
                        wavLocation = record.stopRecording("phrase"+ (exercice.getActuelIteration() + 1) +".wav");

                    setVisibiliteBouton(true);
                }
            }
        });

        btnEcouter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mp == null) {
                    try {
                        mp = new MediaPlayer();

                        if(typeExercice.equals("logatome"))
                        {
                            mp.setDataSource(exercice.getDirectoryPath() + "/" + exercice.getActuelMot().getMot() + ".wav");
                        }
                        else
                        {
                            mp.setDataSource(exercice.getDirectoryPath() + "/phrase"+ (exercice.getActuelIteration() + 1) + ".wav");
                        }

                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp)
                        {
                            isListening = false;
                            btnEcouter.setImageResource(R.drawable.ic_play_arrow_white_32dp);
                            stopMediaPlayer();
                        }
                    });
                }

                if (!isListening) {
                    btnEcouter.setImageResource(R.drawable.ic_stop_white_32dp);
                    isListening = true;
                    mp.start();
                } else
                {
                    btnEcouter.setImageResource(R.drawable.ic_play_arrow_white_32dp);
                    isListening = false;
                    stopMediaPlayer();
                }
            }
        });
    }

    private String getBase64FromWav(String wavPath) //TODO deja codé dans le class Encode.java
    {
        File files = new File(wavPath);

        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(files);
            LogVoicy.getInstance().createLogInfo("Conversion " + wavPath + " en base64");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(bytes, 0);
    }

    @Override
    public void onDestroy() {
        stopMediaPlayer();
        super.onDestroy();
    }


    public void stopMediaPlayer() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    public void setVisibiliteBouton(boolean isVisible)
    {
        if(isVisible)
        {
            btnEcouter.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
        else
        {
            btnEcouter.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        }
    }

    // ----------------------- SECTION TOOLBAR ET ACTION LORS DES BACK / CLIQUE ITEM MENU -----------------------------

    public void quitterPopup()
    {
        new cn.pedant.SweetAlert.SweetAlertDialog(this, cn.pedant.SweetAlert.SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Êtes-vous sûr ?")
                .setContentText("Voulez-vous vraiment quitter l'exercice en cours ?")
                .setConfirmText("Oui")
                .setConfirmClickListener(new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        DirectoryManager.getInstance().rmdirFolder(exercice.getDirectoryPath());
                        //Intent intent = new Intent(ExerciceActivity.this, MainActivity.class);
                        //startActivity(intent);
                        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                })
                .setCancelButton("Non", new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void configOfToolbar(String type)
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Exercice " + type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_home)
        {
            quitterPopup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(false);
        quitterPopup();
    }

    @Override
    public boolean onSupportNavigateUp() {
        quitterPopup();
        return true;
    }
}
