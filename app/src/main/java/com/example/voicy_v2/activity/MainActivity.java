package com.example.voicy_v2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.voicy_v2.R;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.LogVoicy;
import com.example.voicy_v2.model.ServerRequest;
import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.voicy_v2.services.ServiceTraitementExercice.WORKING_ON;


public class MainActivity extends AppCompatActivity
{
    private static final String TOOLBAR_TITLE = "Voicy";

    private ServerRequest requestPhoneme, requestPhrase;

    private Button btn_phoneme, btn_rslt, btn_sentence, btn_attente;
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bug tablette le backgroudn color devient noir alors je le repasse blanc
        constraintLayout = findViewById(R.id.mainLayout);
        constraintLayout.setBackgroundColor(Color.WHITE);

        LogVoicy.getInstance().createLogInfo("Arriver sur l'activity MainActivity");

        configOfToolbar();

        // Créer l'architecture dossier de l'application (si cela n'est pas déjà fait)
        DirectoryManager.getInstance().initProject();

        // Initialise tous les boutons de la page
        initialisationOfAllButton();

        // Initialisation des listener on click des boutons de la page
        initialisationOfAllButtonListener();
    }

    private void initialisationOfAllButtonListener()
    {
        btn_phoneme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(DirectoryManager.getInstance().getAvailableMo() > 100)
                {
                    LogVoicy.getInstance().createLogInfo("Clique sur le bouton exercice phonème détecté");
                    LogVoicy.getInstance().createLogInfo("Changement de page vers ExerciceActivity avec envoie du paramètre [type: logatome]");
                    Intent intent = new Intent(getApplicationContext(), ConfigurationExerciceActivity.class);
                    intent.putExtra("type", "logatome");
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else
                {
                    popupNoSpace();
                }
            }
        });

        btn_sentence.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(DirectoryManager.getInstance().getAvailableMo() > 100)
                {
                    LogVoicy.getInstance().createLogInfo("Clique sur le bouton exercice phrase détecté");
                    LogVoicy.getInstance().createLogInfo("Changement de page vers PhonemeActivity avec envoie du paramètre [type: phrase]");
                    Intent intent = new Intent(getApplicationContext(), ConfigurationExerciceActivity.class);
                    intent.putExtra("type", "phrase");
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else
                {
                    popupNoSpace();
                }
            }
        });

        btn_rslt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogVoicy.getInstance().createLogInfo("Clique sur le bouton resultat détecté");
                LogVoicy.getInstance().createLogInfo("Changement de page vers ResultatActivity");
                Intent intent = new Intent(getApplicationContext(), ResultatActivity.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        btn_attente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogVoicy.getInstance().createLogInfo("Clique sur le bouton attente détecté");
                LogVoicy.getInstance().createLogInfo("Changement de page vers AttenteExerciceActivy");
                Intent intent = new Intent(getApplicationContext(), AttenteExerciceActivity.class);
                startActivityForResult(intent, 3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        testAttenteResultatFichier();
    }

    private void testAttenteResultatFichier()
    {
        File file = new File(DirectoryManager.OUTPUT_ATTENTE);
        File[] list = file.listFiles();

        if(list.length == 0 || WORKING_ON)
        {
            btn_attente.setVisibility(View.INVISIBLE);
        }
        else
        {
            btn_attente.setVisibility(View.VISIBLE);
        }

        File file2 = new File(DirectoryManager.OUTPUT_RESULTAT);
        File[] list2 = file2.listFiles();

        if(list2.length == 0)
        {
            btn_rslt.setVisibility(View.INVISIBLE);
        }
        else
        {
            btn_rslt.setVisibility(View.VISIBLE);
        }
    }

    private void initialisationOfAllButton()
    {
        btn_phoneme = findViewById(R.id.btn_phoneme);
        btn_sentence = findViewById(R.id.btn_sentence);
        btn_rslt = findViewById(R.id.btn_rslt);
        btn_attente = findViewById(R.id.btn_attente);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            if (resultCode == 0) {
                testAttenteResultatFichier();
            }
        }

        if(requestCode == 1) {
            if (resultCode == 0) {
                testAttenteResultatFichier();
            }
        }

        if(requestCode == 2) {
            if (resultCode == 0) {
                testAttenteResultatFichier();
            }
        }

        if(requestCode == 3) {
            if (resultCode == 0) {
                testAttenteResultatFichier();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);

        LogVoicy.getInstance().createLogInfo("Detection touche Back press utiliser");
        LogVoicy.getInstance().createLogInfo("Affichage d'une alert avec choix type yes/no");

        new cn.pedant.SweetAlert.SweetAlertDialog(this, cn.pedant.SweetAlert.SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Êtes-vous sûr ?")
                .setContentText("Vous aller quitter l'application.")
                .setConfirmText("Quitter")
                .setConfirmClickListener(new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog)
                    {
                        LogVoicy.getInstance().createLogInfo("Clique sur Yes l'application va se fermer");
                        finishAffinity();
                    }
                })
                .setCancelButton("Annuler", new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog) {
                        LogVoicy.getInstance().createLogInfo("Clique sur No fermeture de la popup");
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        Drawable drawable = menu.findItem(R.id.action_home).getIcon();
        drawable.setColorFilter(Color.parseColor("#f7ee68"), PorterDuff.Mode.SRC_ATOP);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_home)
        {

        }

        return super.onOptionsItemSelected(item);
    }

    private void configOfToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
    }

    private void popupNoSpace()
    {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Plus assez de place disponible")
                .setContentText("Il faut 100 Mo minimum disponible pour lancer un traitement. Veuillez en libérer pour pouvoir lancer un traitement.")
                .show();
    }
}
