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

import com.example.voicy_v2.R;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.LogVoicy;
import com.example.voicy_v2.model.ServerRequest;

public class MainActivity extends AppCompatActivity
{
    private static final String TOOLBAR_TITLE = "Voicy";

    private ServerRequest requestPhoneme, requestPhrase;

    private Button btn_phoneme;
    private Button btn_sentence;
    private Button btn_rslt;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configOfToolbar();

        DirectoryManager.getInstance().initProject();

        btn_phoneme = findViewById(R.id.btn_phoneme);
        btn_sentence = findViewById(R.id.btn_sentence);
        btn_rslt = findViewById(R.id.btn_rslt);

        LogVoicy.getInstance().createLogInfo("Arriver sur l'activity MainActivity");

        btn_phoneme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogVoicy.getInstance().createLogInfo("Clique sur le bouton exercice phonème détecté");
                LogVoicy.getInstance().createLogInfo("Changement de page vers ExerciceActivity avec envoie du paramètre [type: logatome]");
                Intent intent = new Intent(getApplicationContext(), ExerciceActivity.class);
                intent.putExtra("type", "logatome");
                startActivityForResult(intent, 0);
            }
        });

        btn_sentence.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogVoicy.getInstance().createLogInfo("Clique sur le bouton exercice phrase détecté");
                LogVoicy.getInstance().createLogInfo("Changement de page vers PhonemeActivity avec envoie du paramètre [type: phrase]");
                Intent intent = new Intent(getApplicationContext(), ExerciceActivity.class);
                intent.putExtra("type", "phrase");
                startActivityForResult(intent, 1);
            }
        });

        btn_rslt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogVoicy.getInstance().createLogInfo("Clique sur le bouton resultat détecté");
                LogVoicy.getInstance().createLogInfo("Changement de page vers ResultatActivity");
                Intent intent = new Intent(getApplicationContext(), ResultatActivity.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            if (resultCode == 0) {

            }
        }

        if(requestCode == 1) {
            if (resultCode == 0) {

            }
        }

        if(requestCode == 2) {
            if (resultCode == 0) {

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
}
