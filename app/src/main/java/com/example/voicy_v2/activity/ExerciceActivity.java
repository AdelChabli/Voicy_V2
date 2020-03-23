package com.example.voicy_v2.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.voicy_v2.R;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.Exercice;
import com.example.voicy_v2.model.ExerciceLogatome;
import com.example.voicy_v2.model.ExercicePhrase;
import com.example.voicy_v2.model.LogVoicy;
import com.example.voicy_v2.model.Mot;
import com.example.voicy_v2.model.Recorder;
import com.example.voicy_v2.model.ServerRequest;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ExerciceActivity extends AppCompatActivity implements CallbackServer
{
    private Toolbar toolbar;
    private Button btnAnnuler;
    private ImageButton btnNext, btnEcouter, btnRecord;
    private TextView lePrompteur, iterationEnCours;
    private Exercice exercice;
    private String typeExercice;
    private int maxIteration;
    private Mot motActuel;
    private boolean isRecording = false, isListening = false;
    private Recorder record;
    MediaPlayer mp;
    private JSONObject jsonObject = new JSONObject();
    private JSONArray jsonParams = new JSONArray();
    private String wavLocation = "";


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

        // Permet de configurer la toolbar pour cette activité
        configOfToolbar(typeExercice);

        // Initialise les boutons et les configures
        initAllButton();

        // TODO Pour l'instant, on peut lancer mini 3 logatomes (à rendre modulable)
        maxIteration = 2;

        // Lance l'exercice
        lancerExercice();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void lancerExercice()
    {
        if(typeExercice.equals("logatome"))
        {
            exercice = new ExerciceLogatome(maxIteration, this);

            record = new Recorder(this, exercice.getDirectoryPath());

            lireExercice();
        }
        else
        {
            exercice = new ExercicePhrase(1, this);

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


            if(typeExercice.equals("logatome"))
            {
                ServerRequest requestLogatome = new ServerRequest(this, ExerciceActivity.this);
                requestLogatome.sendHttpsRequest(jsonParams, ServerRequest.URL_SERVER_LOGATOME, 10000);
            }
            else
            {
                ServerRequest requestPhrase = new ServerRequest(this, ExerciceActivity.this);
                requestPhrase.sendHttpsRequest(jsonParams, ServerRequest.URL_SERVER_PHRASE, 10000);
            }


            // TODO Et ce code là, tu peux le déplacer à la fin d'une response serveur réussi ;)
            Intent intent = new Intent(ExerciceActivity.this, ResultatActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void executeAfterResponseServer(final JSONArray response)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run()
            {
                // TODO Enregistrer le jsonArray en resultat.txt dans le dossier de résultat

                String pathResultat = exercice.getDirectoryPath();

            }
        });

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
                try {
                    jsonObject.put("element", exercice.getActuelMot().getMot());
                    jsonObject.put("wav", getBase64FromWav(wavLocation));
                    jsonParams.put(new JSONObject(jsonObject.toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                        wavLocation = record.stopRecording("phrase"+exercice.getActuelIteration()+".wav");

                    setVisibiliteBouton(true);
                }
            }
        });

        btnEcouter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mp == null) {
                    try {
                        mp = new MediaPlayer();
                        mp.setDataSource(exercice.getDirectoryPath() + "/" + exercice.getActuelMot().getMot() + ".wav");
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

    private String getBase64FromWav(String wavPath)
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
                        Intent intent = new Intent(ExerciceActivity.this, MainActivity.class);
                        setResult(0, intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
