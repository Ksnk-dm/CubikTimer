package com.ukrkosenko.cubikrubicktime.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ukrkosenko.cubikrubicktime.R;
import com.ukrkosenko.cubikrubicktime.empty.Records;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder>{
    private List<Records> items;
    private View.OnClickListener onClickListener;

    public ListAdapter(View.OnClickListener onClickListener){
        this.onClickListener=onClickListener;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ListViewHolder(
                layoutInflater.inflate(
                        R.layout.list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Records item = getPosition(position);
        holder.itemsTextView.setText(item.getTime());
      //  holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private Records getPosition(int position) {
        return items.get(position);
    }

    public void setItems(List<Records> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItems(Records records){
        items.add(records);
        notifyDataSetChanged();

    }
}
