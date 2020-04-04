package com.example.voicy_v2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.voicy_v2.R;
import com.example.voicy_v2.model.DirectoryManager;
import com.example.voicy_v2.model.LogVoicy;
import com.example.voicy_v2.model.RecyclerResultAdapter;
import com.example.voicy_v2.model.ResultFile;
import com.example.voicy_v2.model.SortFileByCreationDate;
import com.example.voicy_v2.model.SwipeHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultatActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    public static RecyclerResultAdapter rAdapter;
    private List<ResultFile> listeFile;
    private ResultFile resultFile;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);

        configOfToolbar();

        listeFile = getAllResultFromAppFolder();

        recyclerView = findViewById(R.id.rvResultat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rAdapter = new RecyclerResultAdapter(this, listeFile);
        recyclerView.setAdapter(rAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new SwipeHelper(this, rAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

    }

    // Fonction permettant de récupérer tous dossier résultat et en les transformants en objet ResultFile
    public List<ResultFile> getAllResultFromAppFolder()
    {
        List<ResultFile> listResult = new ArrayList<>();

        File[] dirs = SortFileByCreationDate.getInstance().getListSorted(DirectoryManager.OUTPUT_RESULTAT);

        for(int i = (dirs.length - 1); i >= 0; i--)
        {
            listResult.add(new ResultFile(dirs[i].getName()));
        }

        return listResult;
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
        getSupportActionBar().setTitle("Resultat");
    }
}
