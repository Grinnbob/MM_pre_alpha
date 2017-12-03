package com.mycompany.grifon.mm_pre_alpha.ui.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.Message;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.MyFirebaseArray;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Vlad on 03.12.2017.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    private final LayoutInflater inflater;
    MyFirebaseArray firebaseArray;

    private final SortedMap<Long,Message> chat;
    long[] keysArray;

    public ChatRecyclerViewAdapter(Context context, DatabaseReference ref){
        inflater=LayoutInflater.from(context);
        this.chat= new TreeMap<>();
        //keysArray=new long[chat.size()];
        /*int pos=0;
        for(Long l:this.chat.keySet()){
            keysArray[pos++]=l;
        }*/
        firebaseArray=new MyFirebaseArray(ref);
        firebaseArray.setOnChangedListener(new MyFirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                switch (type){
                    case ADDED:
                    case CHANGED:{
                        Message msg = firebaseArray.getItem(index).getValue(Message.class);
                        chat.put(msg.getTimeMessage(),msg);
                        updateIndecesArray();
                        break;
                    }
                    case REMOVED:{
                        Message msg = (Message) firebaseArray.getItem(index).getValue();
                        chat.remove(msg.getTimeMessage());
                        updateIndecesArray();
                        break;
                    }
                    case MOVED:// ignore
                        break;
                }



                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                clear();
            }
        });
    }

    public void clear(){
        keysArray=new long[0];
        chat.clear();
    }


    private void updateIndecesArray(){
        keysArray=new long[chat.size()];
        int pos=0;
        for(Long l:this.chat.keySet()){
            keysArray[pos++]=l;
        }
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_item,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.bind(getItem(position));
    }



    public Message getItem(int position){
        return chat.get(keysArray[position]);
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }
}
