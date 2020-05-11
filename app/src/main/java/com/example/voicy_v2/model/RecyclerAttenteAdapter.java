package com.example.voicy_v2.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voicy_v2.R;
import com.example.voicy_v2.activity.AffichageExerciceActivity;
import com.example.voicy_v2.activity.AttenteExerciceActivity;
import com.example.voicy_v2.activity.ExerciceActivity;
import com.example.voicy_v2.activity.MainActivity;
import com.example.voicy_v2.activity.ResultatActivity;
import com.example.voicy_v2.interfaces.CallbackServer;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecyclerAttenteAdapter extends RecyclerView.Adapter<RecyclerAttenteAdapter.ResultViewHolder> implements CallbackServer
{
    private Context context;
    private List<ResultFile> listeResultat;
    private ResultFile exerciceSelected;
    private CallbackServer callbackServer = RecyclerAttenteAdapter.this;
    private int currentPositionExercice;

    public RecyclerAttenteAdapter(Context ccontext, List<ResultFile> listResult)
    {
        this.context = ccontext;
        this.listeResultat = listResult;
    }

    public void deleteItem(int position)
    {
        ResultFile resultFile = listeResultat.get(position);

        DirectoryManager.getInstance().rmdirFolder(DirectoryManager.OUTPUT_ATTENTE + "/" + resultFile.getNameFile());

        listeResultat.remove(position);

        notifyItemRemoved(position);

        if(listeResultat.isEmpty())
        {
            ((Activity) context).finish();
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder
    {
        public TextView laDate, lheure, leGenre;
        public Button logoResult;

        public ResultViewHolder(@NonNull View itemView)
        {
            super(itemView);

            laDate = itemView.findViewById(R.id.txtDate);
            lheure = itemView.findViewById(R.id.txtHour);
            leGenre = itemView.findViewById(R.id.txtGenre);
            logoResult = itemView.findViewById(R.id.buttonType);
        }
    }

    @NonNull
    @Override
    public RecyclerAttenteAdapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_resultat, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAttenteAdapter.ResultViewHolder holder, final int position)
    {
        ResultFile resultFile = listeResultat.get(position);

        if(String.valueOf(resultFile.getNameFile().charAt(0)).toLowerCase().equals("l"))
        {
            holder.logoResult.setText("L");
            holder.logoResult.setBackgroundResource(R.drawable.shape_logatome);
        }
        else
        {
            holder.logoResult.setText("P");
            holder.logoResult.setBackgroundResource(R.drawable.shape_phrase);
        }

        holder.laDate.setText(resultFile.getDate());
        holder.lheure.setText(resultFile.getHour());
        holder.leGenre.setText(resultFile.getGenre());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if(DirectoryManager.getInstance().getAvailableMo() > 100)
                {
                    exerciceSelected = listeResultat.get(position);
                    currentPositionExercice = position;

                    new cn.pedant.SweetAlert.SweetAlertDialog(((Activity) context), cn.pedant.SweetAlert.SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Êtes-vous sûr ?")
                            .setContentText("Voulez-vous traiter cette exercice ?")
                            .setConfirmText("Oui")
                            .setConfirmClickListener(new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog)
                                {
                                    sDialog.dismissWithAnimation();

                                    final ProgressDialog dialog = ProgressDialog.show(context, null, "Préparation des données à envoyer ...");

                                    new Thread(new Runnable() {
                                        public void run()
                                        {
                                            // Récupère la hashmap de donnée
                                            final HashMap<String,String> params = traitementOnItemClick();

                                            // Dismiss sur l'UI le progressDialog
                                            ((Activity)context).runOnUiThread(new Runnable() {
                                                public void run() {
                                                    dialog.dismiss();
                                                }
                                            });

                                            ((Activity)context).runOnUiThread(new Runnable()
                                            {
                                                public void run()
                                                {
                                                    RequestServer requestLogatome = new RequestServer(context, callbackServer);

                                                    if(exerciceSelected.getNameFile().toLowerCase().contains("p"))
                                                    {
                                                        requestLogatome.sendHttpsRequest(params, "phrase");
                                                    }
                                                    else
                                                    {
                                                        requestLogatome.sendHttpsRequest(params, "logatome");
                                                    }
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            })
                            .setCancelButton("Non", new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
                else
                {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Plus assez de place disponible")
                            .setContentText("Il faut 100 Mo minimum disponible pour lancer un traitement. Veuillez en libérer pour pouvoir lancer un traitement.")
                            .show();
                }

            }
        });
    }

    private HashMap<String, String> traitementOnItemClick()
    {
        String sourceDirectory = DirectoryManager.OUTPUT_ATTENTE + "/" + exerciceSelected.getNameFile();

        String paramString = "";

        try {
            File file = new File(sourceDirectory + "/temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            while((line =reader.readLine()) != null)
            {
                paramString += line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        LogVoicy.getInstance().createLogInfo(paramString);

        paramString = paramString.substring(1);
        paramString = paramString.substring(0, paramString.length() - 1);
        paramString = paramString.replaceAll(", ", ",");

        LogVoicy.getInstance().createLogInfo(paramString);

        HashMap<String, String> params = new HashMap<>();

        String[] pairs = paramString.split(",");


        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            LogVoicy.getInstance().createLogInfo(pair);
            String[] keyValue = pair.split("=");
            params.put(keyValue[0], keyValue[1]);
        }

        LogVoicy.getInstance().createLogInfo(params.get("gender"));
        LogVoicy.getInstance().createLogInfo(params.get("type"));

        return params;
    }

    @Override
    public int getItemCount() {
        return listeResultat.size();
    }

    @Override
    public void executeAfterResponseServer(final JSONArray response)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run()
            {
                String sourceDirectory = DirectoryManager.OUTPUT_ATTENTE + "/" + exerciceSelected.getNameFile();

                DirectoryManager.getInstance().createFileOnDirectory(sourceDirectory, "resultat.txt", response.toString());

                DirectoryManager.getInstance().cutAndPastFolderToAnother(sourceDirectory, DirectoryManager.OUTPUT_RESULTAT);

                deleteItem(currentPositionExercice);

                Intent intent = new Intent(context, ResultatActivity.class);
                ((Activity) context).startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public void exercuceAfterErrorServer(final String error)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run()
            {
                SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
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
        });
    }
}
