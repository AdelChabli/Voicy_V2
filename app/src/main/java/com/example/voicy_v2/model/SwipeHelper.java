package com.example.voicy_v2.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voicy_v2.R;

public class SwipeHelper extends ItemTouchHelper.Callback
{
    // Attribute
    private static final int BACKGROUND_COLOR_SWIPE = Color.parseColor("#fc0f31");
    private ColorDrawable mBackground;
    private Drawable icon_delete;
    private Context context;
    private int intrinsicWidth, intrinsicHeight;
    private Paint mClearPaint;
    private RecyclerResultAdapter adapter;

    public SwipeHelper(Context theContext, RecyclerResultAdapter theAdapter)
    {
        context = theContext;
        adapter = theAdapter;
        mBackground = new ColorDrawable();
        icon_delete = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp);
        intrinsicWidth = icon_delete.getIntrinsicWidth();
        intrinsicHeight = icon_delete.getIntrinsicHeight();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
    {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction)
    {
        new cn.pedant.SweetAlert.SweetAlertDialog(context, cn.pedant.SweetAlert.SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Êtes-vous sûr ?")
                .setContentText("Voulez-vous vraiment supprimer cet exercice ?")
                .setConfirmText("Oui")
                .setConfirmClickListener(new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        final int position = viewHolder.getAdapterPosition();

                        adapter.deleteItem(position);

                    }
                })
                .setCancelButton("Non", new cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(cn.pedant.SweetAlert.SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                })
                .show();



    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
    {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if(dX >0)
        {
            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getHeight();

            boolean isCancelled = dX == 0 && !isCurrentlyActive;

            if (isCancelled) {
                clearCanvas(c, itemView.getLeft() + dX, (float) itemView.getTop(), (float) itemView.getLeft(), (float) itemView.getBottom());
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }

            mBackground.setColor(BACKGROUND_COLOR_SWIPE);
            mBackground.setBounds(itemView.getLeft() + (int) dX, itemView.getTop(), itemView.getLeft(), itemView.getBottom());
            mBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight + intrinsicHeight) / 2;
            int deleteIconLeft = itemView.getLeft() + deleteIconMargin - intrinsicWidth;
            int deleteIconRight = itemView.getLeft() + deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;

            icon_delete.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            icon_delete.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        else if(dX < 0)
        {
            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getHeight();

            boolean isCancelled = dX == 0 && !isCurrentlyActive;

            if (isCancelled) {
                clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }

            mBackground.setColor(BACKGROUND_COLOR_SWIPE);
            mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            mBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;

            icon_delete.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            icon_delete.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }



    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom)
    {
        c.drawRect(left, top, right, bottom, mClearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }
}
