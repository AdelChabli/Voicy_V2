package com.example.voicy_v2.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.voicy_v2.R;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.ServerRequest;

import org.json.JSONArray;

public class PhonemeActivity extends AppCompatActivity
{

    private static final String TOOLBAR_TITLE = "UNDEFINED";

    // TODO A commenter

    private Button btn_test; //TODO A virer !
    private ServerRequest requestPhoneme;

    private Context context = this;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneme);

        /*
        btn_test = findViewById(R.id.btn_test); //TODO A virer !

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhoneme = new RequestPhoneme(context, PhonemeActivity.this);

                HashMap<String, String> listeParametre = new HashMap<>();

                int size = 2;

                listeParametre.put("size",String.valueOf(size));


                listeParametre.put(DirectoryManager.getInstance().getFileTest("babrin.wav").getName(), Encode.getEncode(DirectoryManager.getInstance().getFileTest("babrin.wav")));

                listeParametre.put(DirectoryManager.getInstance().getFileTest("babu.wav").getName(), Encode.getEncode(DirectoryManager.getInstance().getFileTest("babu.wav")));

                // Envoie au serveur une requête sur les phonemes
                requestPhoneme.sendHttpsRequest(listeParametre);

            }
        });

         */
    }

    private void configOfToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
    }
}
