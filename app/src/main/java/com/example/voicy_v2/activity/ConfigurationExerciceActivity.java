package com.example.voicy_v2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.voicy_v2.R;
import com.example.voicy_v2.model.LogVoicy;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfigurationExerciceActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    private String typeExercice;
    private Spinner spinner;
    private EditText editIteration;
    private Button buttonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        // Permet de récuperer le paramètre envoyer par l'activité précédente
        Bundle param = getIntent().getExtras();
        typeExercice = param.getString("type");

        // Permet de configurer la toolbar pour cette activité
        configOfToolbar(typeExercice);

        spinner = findViewById(R.id.spinnerGenre);
        editIteration = findViewById(R.id.editTextIteration);
        buttonValider = findViewById(R.id.btnValiderConfig);

        editIteration.addTextChangedListener(new PatternedTextWatcher("##"));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genre , android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(String.valueOf(editIteration.getText()).length() > 2 || String.valueOf(editIteration.getText()).length() < 1)
                {
                    mauvaiseConfig("Veuillez remplir le nombre d'exercice à effectuer");
                }
                else
                {
                    String.valueOf(editIteration.getText());
                    int iteration = Integer.parseInt(String.valueOf(editIteration.getText()));

                    if(iteration == 0)
                    {
                        mauvaiseConfig("Impossible de lancer 0 exercice.");
                    }
                    else if(iteration > 12)
                    {
                        mauvaiseConfig("Impossible de lancer plus de 12 exercices");
                    }
                    else
                    {
                        String genre = spinner.getSelectedItem().toString();

                        Intent intent = new Intent(ConfigurationExerciceActivity.this, ExerciceActivity.class);
                        intent.putExtra("type", typeExercice);
                        intent.putExtra("genre", genre);
                        intent.putExtra("iteration", iteration);
                        startActivityForResult(intent, 0);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            }
        });
    }

    public void mauvaiseConfig(String error)
    {
        SweetAlertDialog sDialog = new SweetAlertDialog(ConfigurationExerciceActivity.this, SweetAlertDialog.ERROR_TYPE);
        sDialog.setTitleText("Oups ...");
        sDialog.setContentText(error);
        sDialog.setConfirmText("Ok");
        sDialog.setCancelable(false);
        sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        });
        sDialog.show();
    }

    // ----------------------- SECTION TOOLBAR ET ACTION LORS DES BACK / CLIQUE ITEM MENU -----------------------------

    private void configOfToolbar(String type)
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Configuration");
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
            Intent intent = new Intent(ConfigurationExerciceActivity.this, MainActivity.class);
            setResult(0, intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == 0) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(false);
        Intent intent = new Intent(ConfigurationExerciceActivity.this, MainActivity.class);
        setResult(0, intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ConfigurationExerciceActivity.this, MainActivity.class);
        setResult(0, intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return true;
    }
}
