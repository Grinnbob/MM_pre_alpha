package com.mycompany.grifon.mm_pre_alpha.ui.subscribers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;

import java.util.ArrayList;
import java.util.List;

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersViewholder> {

    private final LayoutInflater inflater;
    private final Context context;
    private final List<PlainUser> currentPlainUsers = new ArrayList<>();

    public SubscribersAdapter(Context c) {
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = c;
    }

    public void replaceData(List<PlainUser> newData) {
        currentPlainUsers.clear();
        currentPlainUsers.addAll(newData);
        notifyDataSetChanged();
    }

    ;

    @Override
    public SubscribersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscribersViewholder(inflater.inflate(R.layout.recycler_subscribers_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SubscribersViewholder holder, int position) {
        holder.applySubscriber(currentPlainUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return currentPlainUsers.size();
    }
}
