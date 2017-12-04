package com.mycompany.mm_pre_alpha.ui.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.mm_pre_alpha.data.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Vlad on 03.12.2017.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder {


    private final TextView tvUser;
    private final TextView tvTime;
    private final TextView tvMessage;


    public ChatViewHolder(View itemView) {
        super(itemView);
        tvUser= (TextView) itemView.findViewById(R.id.tvUser);
        tvTime= (TextView) itemView.findViewById(R.id.tvTime);
        tvMessage= (TextView) itemView.findViewById(R.id.tvMessage);

    }
    private static final SimpleDateFormat format = createDateFormat();

    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy (HH:mm:ss)");
        TimeZone tz = TimeZone.getDefault();
       // tz.setRawOffset(1000*60*60*4);
        format.setTimeZone(tz);
        return format;
    }


    public void bind(Message msg){
        tvUser.setText(msg.getAutor());
        tvTime.setText(format.format( new Date(msg.getTimeMessage())));
        tvMessage.setText(msg.getTextMessage());

    }
}
