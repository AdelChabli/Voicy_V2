package com.example.voicy_v2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.voicy_v2.R;
import com.example.voicy_v2.interfaces.CallbackServer;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.RecyclerAttenteAdapter;
import com.example.voicy_v2.model.RecyclerResultAdapter;
import com.example.voicy_v2.model.ResultFile;
import com.example.voicy_v2.model.SortFileByCreationDate;
import com.example.voicy_v2.model.SwipeHelper;
import com.example.voicy_v2.model.SwipeHelperAttente;
import com.example.voicy_v2.services.ServiceTraitementExercice;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AttenteExerciceActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    public static RecyclerAttenteAdapter rAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attente_exercice);
        configOfToolbar();

        List<ResultFile> listeResult = getAllAttenteFromAppFolder();

        recyclerView = findViewById(R.id.rv_attente);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rAdapter = new RecyclerAttenteAdapter(this, listeResult);
        recyclerView.setAdapter(rAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new SwipeHelperAttente(this, rAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    // Fonction permettant de récupérer tous dossier résultat et en les transformants en objet ResultFile
    public List<ResultFile> getAllAttenteFromAppFolder()
    {
        List<ResultFile> listResult = new ArrayList<>();

        File[] dirs = SortFileByCreationDate.getInstance().getListSorted(DirectoryManager.OUTPUT_ATTENTE);

        for(int i = (dirs.length - 1); i >= 0; i--)
        {
            listResult.add(new ResultFile(dirs[i].getName()));
        }

        return listResult;
    }

    public void startTraitementService(View v)
    {
        if(DirectoryManager.getInstance().getAvailableMo() > 100)
        {
            Intent serviceIntent = new Intent(this, ServiceTraitementExercice.class);
            startService(serviceIntent);
            finish();
        }
        else
        {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Plus assez de place disponible")
                    .setContentText("Il faut 100 Mo minimum disponible pour lancer un traitement. Veuillez en libérer pour pouvoir lancer un traitement.")
                    .show();
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
        super.onBackPressed();
        finish();
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
        getSupportActionBar().setTitle("Exercice en attente");
    }
}
