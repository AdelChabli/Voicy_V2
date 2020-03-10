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
import com.example.voicy_v2.model.ServerRequest;

public class MainActivity extends AppCompatActivity implements CallbackServer
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

        btn_phoneme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExerciceActivity.class);
                intent.putExtra("type", "logatome");
                startActivityForResult(intent, 0);
            }
        });

        btn_sentence.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhonemeActivity.class);
                intent.putExtra("type", "phrase");
                startActivityForResult(intent, 1);
            }
        });

        btn_rslt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
    public void executeAfterResponseServer(String response, int idServer)
    {
        // serveur phoneme
        if(idServer == 0)
            traitementPhoneme(response);
        else
            traitementPhrase(response);
    }

    public void traitementPhoneme(String response) {}

    public void traitementPhrase(String response) {}

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);

        new cn.pedant.SweetAlert.SweetAlertDialog(this, cn.pedant.SweetAlert.SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Êtes-vous sûr ?")
                .setContentText("Vous aller quitter l'application.")
                .setConfirmText("Quitter")
                .setConfirmClickListener(new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog)
                    {
                        finishAffinity();
                    }
                })
                .setCancelButton("Annuler", new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog) {
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
