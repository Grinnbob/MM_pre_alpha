package com.mycompany.grifon.mm_pre_alpha.ui.subscribers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.ui.ProfileActivity;

import java.io.Serializable;

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
    public void onClick(View view) {
        Context c = view.getContext();
        Toast.makeText(c, plainUser.getName(), Toast.LENGTH_SHORT).show();
        /*Intent i = new Intent(c, ProfileActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("user", (Serializable) plainUser);
        i.putExtra("BUNDLE", b);
        c.startActivity(i);*/

        /*Intent intent = new Intent(Current.class, Transfer.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)object);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);*/


        Intent i = new Intent(c, ProfileActivity.class);
        i.putExtra("user", plainUser);
        c.startActivity(i);
        /*Intent intent = new Intent(SourceActivity.this, TargetActivity.class);
        intent.putExtra("QuestionListExtra", ArrayList<Question>mQuestionList);*/
    }
}
