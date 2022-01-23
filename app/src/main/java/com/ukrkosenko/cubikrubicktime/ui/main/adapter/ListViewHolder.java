package com.ukrkosenko.cubikrubicktime.ui.main.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ukrkosenko.cubikrubicktime.R;

public class ListViewHolder extends RecyclerView.ViewHolder{
    public TextView itemsTextView;
    public ListViewHolder(@NonNull View itemView) {
        super(itemView);
        itemsTextView=itemView.findViewById(R.id.items_text_view);
    }
}
