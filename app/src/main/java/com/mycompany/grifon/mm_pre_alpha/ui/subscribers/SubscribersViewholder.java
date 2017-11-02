package com.mycompany.grifon.mm_pre_alpha.ui.subscribers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.grifon.mm_pre_alpha.ProfileActivity;
import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;

/**
 * Created by Vlad on 29.10.2017.
 */

public class SubscribersViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView tv_subscriber_name;
    private PlainUser plainUser;

    public SubscribersViewholder(View itemView) {
        super(itemView);
        tv_subscriber_name= (TextView) itemView.findViewById(R.id.tv_subscriber_name);
        itemView.setOnClickListener(this);
    }


    void applySubscriber(PlainUser plainUser){
        tv_subscriber_name.setText(plainUser.getName());
        this.plainUser = plainUser;
    }


    @Override
    public void onClick(View v) {
        Context c = v.getContext();
        Toast.makeText(c,"I am pressed!",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(c, ProfileActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("user", plainUser);
        c.startActivity(i);
    }
}
