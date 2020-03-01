package com.example.voicy_v2.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voicy_v2.R;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.Encode;
import com.example.voicy_v2.model.RequestPhoneme;
import com.example.voicy_v2.model.ServerRequest;

import java.util.HashMap;

public class PhonemeActivity extends AppCompatActivity implements CallbackServer {

    private Button btn_test; //TODO A virer !
    private ServerRequest requestPhoneme;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneme);

        btn_test = findViewById(R.id.btn_test); //TODO A virer !

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhoneme = new RequestPhoneme(context, PhonemeActivity.this);

                HashMap<String, String> listeParametre = new HashMap<>();
                listeParametre.put("wav", Encode.getEncode(DirectoryManager.getInstance().getFileTest("#babrin.wav")));
                listeParametre.put("phoneme", Encode.getEncode("babrin"));

                // Envoie au serveur une requête sur les phonemes
                requestPhoneme.sendHttpsRequest(listeParametre);

            }
        });
    }

    @Override
    public void executeAfterResponseServer(String response, int idServer) {

    }
}
