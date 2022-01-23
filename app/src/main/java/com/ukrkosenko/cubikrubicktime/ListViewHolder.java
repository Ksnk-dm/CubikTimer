package com.ukrkosenko.cubikrubicktime;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder{
    public TextView itemsTextView;
    public ListViewHolder(@NonNull View itemView) {
        super(itemView);
        itemsTextView=itemView.findViewById(R.id.items_text_view);
    }
}
