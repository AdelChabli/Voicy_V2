package com.example.voicy_v2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.voicy_v2.R;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.LogVoicy;
import com.example.voicy_v2.model.Logatome;
import com.example.voicy_v2.model.Phoneme;
import com.example.voicy_v2.model.ResultFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AffichageExerciceActivity extends AppCompatActivity
{
    private ResultFile resultFile;
    private String fileTXT = "";
    private List<JSONObject> listeJSONObject = new ArrayList<>();
    private List<Logatome> listeLogatome = new ArrayList<>();
    private ListView listView;
    private Toolbar toolbar;
    private PopupWindow popUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_exercice);

        configOfToolbar();

        listView = findViewById(R.id.listeElement);

        // Permet de récuperer le paramètre envoyer par l'activité précédente
        resultFile = (ResultFile) getIntent().getSerializableExtra("resultat");

        fileTXT = DirectoryManager.OUTPUT_RESULTAT + "/" + resultFile.getNameFile() + "/resultat.txt";

        // Permet de remplir la liste de JSON object
        getAllJSONObject(fileTXT);

        // Permet de remplir la liste de logatome
        getAllElement();

        List<String> element = new ArrayList<>();

        for(Logatome logatome : listeLogatome)
        {
            element.add(logatome.getLogatomeName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, element);

        listView.setAdapter(arrayAdapter);

    }

    private void getAllElement()
    {
        Logatome logatome = null;

        for(JSONObject jsonObject : listeJSONObject)
        {
            LogVoicy.getInstance().createLogInfo(jsonObject.toString());

            try {
                String name = jsonObject.getString("name");
                name = name.substring(name.indexOf("/") + 1);
                LogVoicy.getInstance().createLogInfo("NamePhoneme: " + name);

                Double scoreContraint = Math.round(Double.parseDouble(jsonObject.getJSONObject("global").getString("scoreContraint")) * 100.0) / 100.0;
                Double scoreNonContraint = Math.round(Double.parseDouble(jsonObject.getJSONObject("global").getString("scoreNonContraint")) * 100.0) / 100.0;
                LogVoicy.getInstance().createLogInfo("SC: " + scoreContraint);
                LogVoicy.getInstance().createLogInfo("NC: " + scoreNonContraint);

                logatome = new Logatome(name, String.valueOf(scoreNonContraint), String.valueOf(scoreContraint));

                JSONArray jsonArray = jsonObject.getJSONArray("phoneAll");

                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject ligneJSONObject = jsonArray.getJSONObject(i);

                    String phonemeName = ligneJSONObject.getString("phone");
                    double phoneAC = Math.round(Double.parseDouble(ligneJSONObject.getString("AC")) * 100.0) / 100.0;
                    double phoneNC = Math.round(Double.parseDouble(ligneJSONObject.getString("NC")) * 100.0) / 100.0;
                    String start = ligneJSONObject.getString("start");
                    String end = ligneJSONObject.getString("end");

                    logatome.addPhoneme(phonemeName, start, end,String.valueOf(phoneAC),  String.valueOf(phoneNC));

                    LogVoicy.getInstance().createLogInfo(logatome.getListePhoneme().get(i).getPhonemeToString());
                }

                listeLogatome.add(logatome);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllJSONObject(String fileName)
    {
        try {
            File testDoc = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(testDoc));
            String line = "";
            while((line =reader.readLine()) != null)
            {
                JSONArray jsonArray = new JSONArray(line);

                for(int i = 0; i < jsonArray.length(); i++)
                {
                    listeJSONObject.add(jsonArray.getJSONObject(i));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    // ----------------------- SECTION TOOLBAR ET ACTION LORS DES BACK / CLIQUE ITEM MENU -----------------------------

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
            Intent i = new Intent(AffichageExerciceActivity.this,MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return true;
    }

    private void configOfToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exercice");
    }
}
