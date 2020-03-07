package com.example.voicy_v2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.voicy_v2.R;

public class ExerciceActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private Button btnAnnuler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercice);

        Bundle param = getIntent().getExtras();

        configOfToolbar(param.getString("type"));

        btnAnnuler = findViewById(R.id.buttonAnnuler);

        btnAnnuler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                quitterPopup();
            }
        });
    }

    // ----------------------- SECTION TOOLBAR ET ACTION LORS DES BACK / CLIQUE ITEM MENU -----------------------------

    public void quitterPopup()
    {
        new cn.pedant.SweetAlert.SweetAlertDialog(this, cn.pedant.SweetAlert.SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Êtes-vous sûr ?")
                .setContentText("Vous aller annuler l'exercice en cours")
                .setConfirmText("Continuer")
                .setConfirmClickListener(new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog)
                    {
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    private void configOfToolbar(String type)
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void onBackPressed() {
        super.onBackPressed();
        quitterPopup();
    }

    @Override
    public boolean onSupportNavigateUp() {
        quitterPopup();
        return true;
    }
}
