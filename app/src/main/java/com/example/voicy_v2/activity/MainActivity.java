package com.example.voicy_v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voicy_v2.R;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.ServerRequest;

public class MainActivity extends AppCompatActivity implements CallbackServer
{
    private ServerRequest requestPhoneme, requestPhrase;

    private Button btn_phoneme;
    private Button btn_sentence;
    private Button btn_rslt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DirectoryManager.getInstance().initProject();

        btn_phoneme = findViewById(R.id.btn_phoneme);
        btn_sentence = findViewById(R.id.btn_sentence);
        btn_rslt = findViewById(R.id.btn_rslt);

        btn_phoneme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhonemeActivity.class);
                startActivity(intent);
            }
        });
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
}
