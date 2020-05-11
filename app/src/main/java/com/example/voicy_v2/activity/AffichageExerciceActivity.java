package com.example.voicy_v2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AffichageExerciceActivity extends AppCompatActivity
{
    private ResultFile resultFile;
    private String fileTXT = "";
    private ConstraintLayout rLayout;
    private List<JSONObject> listeJSONObject = new ArrayList<>();
    private List<Logatome> listeLogatome = new ArrayList<>();
    private ListView listView;
    private Toolbar toolbar;
    private RelativeLayout mRelativeLayout;
    private PopupWindow popUp;
    private TableLayout tableLayout;
    private TextView titrePopUp, textClose;
    private ImageButton btnEcouter;
    private MediaPlayer mediaPlayer;
    private boolean isListening = false;
    private String logatomeChoisi = "";
    private LayoutInflater inflater;
    private View customView;
    private int phraseIterator = 1;
    private String typeExercice;
    private boolean popupOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_exercice);

        configOfToolbar();

        listView = findViewById(R.id.listeElement);
        rLayout = findViewById(R.id.const_layout);
        mediaPlayer = new MediaPlayer();
        // Initialisation
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.popup_resultat,null);

        // Permet de récuperer le paramètre envoyer par l'activité précédente
        resultFile = (ResultFile) getIntent().getSerializableExtra("resultat");
        Bundle param = getIntent().getExtras();
        typeExercice = param.getString("type");

        LogVoicy.getInstance().createLogInfo("Type resultat = " + typeExercice);

        fileTXT = DirectoryManager.OUTPUT_RESULTAT + "/" + resultFile.getNameFile() + "/resultat.txt";

        // Permet de remplir la liste de JSON object
        getAllJSONObject(fileTXT);

        // Permet de remplir la liste de logatome
        getAllElement();

        // Permet d'ajouter un score globale qui fait un recap de tous les autres phonemes
        if(typeExercice.toLowerCase().equals("l"))
        {
            double avgScoreContraint = getAvgScoreContraint(listeLogatome);
            double avgScoreNonContraint = getAvgScoreNonContraint(listeLogatome);
            Logatome globale = new Logatome("Résultats globaux", String.valueOf(avgScoreNonContraint), String.valueOf(avgScoreContraint));

            for(Logatome logatome : listeLogatome)
            {
                Phoneme phoneme = getGlobalFormatedPhoneme(logatome);
                globale.addPhoneme(phoneme.getPhoneme(), phoneme.getDebut(), phoneme.getFin(), phoneme.getScoreContraint(), phoneme.getScoreNonContraint());
            }

            listeLogatome.add(globale);

        }

        List<String> element = new ArrayList<>();

        for(Logatome logatome : listeLogatome)
        {
            element.add(logatome.getLogatomeName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, element);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(!popupOpen)
                {
                    showResultat(listeLogatome.get(i), i);

                    popupOpen = true;

                    chargerWav(adapterView, i);
                }

                btnEcouter = customView.findViewById(R.id.btnEcouterResultat);

                if(typeExercice.toLowerCase().equals("l"))
                {
                    if(i == (listeLogatome.size() - 1))
                    {
                        btnEcouter.setVisibility(View.INVISIBLE);
                    }
                }


                btnEcouter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if(!mediaPlayer.isPlaying())
                        {
                            mediaPlayer.start();
                        }

                    }
                });
            }
        });

    }

    private Phoneme getGlobalFormatedPhoneme(Logatome logatome)
    {
        String name = logatome.getLogatomeName();

        int nbPhoneme = logatome.getListePhoneme().size();

        String debut = logatome.getListePhoneme().get(0).getDebut();
        String fin = logatome.getListePhoneme().get(nbPhoneme - 1).getFin();

        String scoreContraint = logatome.getScoreContraint();
        String scoreNonContraint = logatome.getScoreNonContraint();

        return new Phoneme(name, debut, fin, scoreContraint, scoreNonContraint);
    }

    private double getAvgScoreContraint(List<Logatome> listeLogatome)
    {
        int nbLog = listeLogatome.size();
        double additionScore = 0.00;
        for(Logatome l : listeLogatome)
        {
            additionScore += Double.parseDouble(l.getScoreContraint());
        }

        double avgScore = additionScore / nbLog;
        avgScore = Math.round(avgScore * 100.0) / 100.0;

        return avgScore;
    }

    private double getAvgScoreNonContraint(List<Logatome> listeLogatome)
    {
        int nbLog = listeLogatome.size();
        double additionScore = 0.00;
        for(Logatome l : listeLogatome)
        {
            additionScore += Double.parseDouble(l.getScoreNonContraint());
        }

        double avgScore = additionScore / nbLog;
        avgScore = Math.round(avgScore * 100.0) / 100.0;

        return avgScore;
    }

    private void chargerWav(AdapterView<?> parent, int i)
    {
        mediaPlayer.reset();

        String path = "";

        if(typeExercice.toLowerCase().equals("p"))
        {
            path = DirectoryManager.OUTPUT_RESULTAT + "/" + resultFile.getNameFile() + "/phrase" + (i+1) + ".wav";
        }
        else
        {
            path = DirectoryManager.OUTPUT_RESULTAT + "/" + resultFile.getNameFile() + "/" + listeLogatome.get(i).getLogatomeName() + ".wav";
        }

        try
        {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        }
        catch (Exception e )
        {
            e.printStackTrace();
        }
    }

    private void showResultat(Logatome logatome, int i)
    {
        textClose = customView.findViewById(R.id.txtClose);


        //Initialisation de la popup
        popUp = new PopupWindow(customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popUp.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popUp.setElevation(5.0f);
        }

        // Configuration du titre
        titrePopUp = popUp.getContentView().findViewById(R.id.titrePopup);

        if(typeExercice.toLowerCase().equals("p"))
        {
            titrePopUp.setText("Resultat");
        }
        else
        {
            titrePopUp.setText(logatome.getLogatomeName());
        }


        // Configuration des lignes du tableau
        addRowAndColumn(logatome.getListePhoneme(), logatome.getScoreContraint());

        // Faire apparaitre la popup
        popUp.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popUp.showAtLocation(rLayout, Gravity.CENTER,0,0);
        popUp.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp.dismiss();
                mediaPlayer.stop();
                popupOpen = false;
            }
        });

        popUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mediaPlayer.stop();
                popupOpen = false;
            }
        });
    }

    private void addRowAndColumn(List<Phoneme> listePhoneme, String score)
    {

        // Configuration du tableLayout
        TableLayout tab = popUp.getContentView().findViewById(R.id.tabResultat);
        tab.setMinimumWidth(700);
        tab.removeAllViews();

        // Création et initialisation d'une ligne
        TableRow ligneTitre = new TableRow(this);
        ligneTitre.setBackgroundResource(R.drawable.row_border);
        ligneTitre.setBackgroundColor(Color.BLACK);
        ligneTitre.setPadding(0, 0, 0, 2); //Border between rows

        // boucle pour les titres des colonnes
        for (int i = 0; i < 3; i++)
        {
            TextView col = new TextView(this);
            col.setGravity(Gravity.CENTER_HORIZONTAL);
            col.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            col.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            col.setTextColor(Color.WHITE);

            switch(i)
            {
                case 0 :
                    if (true)
                        col.setText(" Phoneme ");
                    else
                        col.setText(" Semi-Contraint ");
                    break;

                case 1 :
                    col.setText(" Durée (frames) ");
                    break;

                case 2 :
                    col.setText(" Score ");
                    break;
            }
            // Ajout de la colonne sur la ligne
            ligneTitre.addView(col);
        }

        // Ajout de la ligne dans le tableau
        tab.addView(ligneTitre);

        TextView scoreNorm = popUp.getContentView().findViewById(R.id.scoreNorm);
        scoreNorm.setText("Score normalisé : " + score);
        Phoneme phoneme;

        // Création de ligne dynamiquement par phoneme
        for (int i = 0; i < listePhoneme.size(); i++)
        {
            phoneme = listePhoneme.get(i);

            // Initialisation d'une ligne
            TableRow tabLigne = new TableRow(this);
            tabLigne.setBackgroundColor(Color.GRAY);

            // Initialisation des paramètres d'une colonne


            // On a trois colonne donc pour chaque colonne on va ajouter une information
            for(int j = 0; j < 3; j++)
            {
                TextView dataCol = new TextView(this);
                dataCol.setBackgroundResource(R.drawable.row_border);
                dataCol.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                dataCol.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                dataCol.setGravity(Gravity.CENTER_HORIZONTAL);
                dataCol.setPadding(0, 5, 0, 5);
                dataCol.setTextColor(Color.WHITE);

                switch (j)
                {
                    case 0:
                        dataCol.setText(phoneme.getPhoneme());
                        break;

                    case 1:
                        dataCol.setText(phoneme.getDebut() + "-" + phoneme.getFin());
                        break;

                    case 2:
                        dataCol.setText(phoneme.getScoreContraint());
                        break;
                }
                tabLigne.addView(dataCol);
            }
            tab.addView(tabLigne);
        }
    }

    private void getAllElement()
    {
        Logatome logatome = null;

        for(JSONObject jsonObject : listeJSONObject)
        {
            LogVoicy.getInstance().createLogInfo(jsonObject.toString());

            try {

                String name = "";
                if(typeExercice.toLowerCase().equals("p"))
                {
                    name = "résultat des phrases";
                }
                else
                {
                    name = jsonObject.getString("name");
                }

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

                    double realEnd = Double.parseDouble(start) + Double.parseDouble(end);
                    realEnd = Math.round(realEnd * 100.0) / 100.0;


                    logatome.addPhoneme(phonemeName, start, String.valueOf(realEnd),String.valueOf(phoneAC),  String.valueOf(phoneNC));

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
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(1);
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
