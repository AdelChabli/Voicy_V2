package com.example.voicy_v2.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voicy_v2.R;
import com.example.voicy_v2.activity.AffichageExerciceActivity;
import com.example.voicy_v2.activity.MainActivity;

import java.util.List;

public class RecyclerResultAdapter extends RecyclerView.Adapter<RecyclerResultAdapter.ResultViewHolder>
{
    private Context context;
    private List<ResultFile> listeResultat;

    public RecyclerResultAdapter(Context ccontext, List<ResultFile> listResult)
    {
        this.context = ccontext;
        this.listeResultat = listResult;
    }

    public void deleteItem(int position)
    {
        ResultFile resultFile = listeResultat.get(position);

        DirectoryManager.getInstance().rmdirFolder(DirectoryManager.OUTPUT_RESULTAT + "/" + resultFile.getNameFile());

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
    public RecyclerResultAdapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_resultat, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerResultAdapter.ResultViewHolder holder, final int position)
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
            public void onClick(View view) {
                Intent intent = new Intent(context, AffichageExerciceActivity.class);
                intent.putExtra("resultat", listeResultat.get(position));
                intent.putExtra("type", holder.logoResult.getText());
                ((Activity)context).startActivityForResult(intent, 0);
                ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listeResultat.size();
    }
}
