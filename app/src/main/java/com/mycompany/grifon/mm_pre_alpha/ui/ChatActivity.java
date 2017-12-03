package com.mycompany.grifon.mm_pre_alpha.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.Chat;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.NewMessageEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.chat.WholeChatEvent;
import com.mycompany.grifon.mm_pre_alpha.data.events.profile.MyProfileEvent;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.data.Message;
import com.mycompany.grifon.mm_pre_alpha.data.PlainChat;
import com.mycompany.grifon.mm_pre_alpha.ui.chat.ChatRecyclerViewAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE = 1;
    //private FirebaseListAdapter<Message> adapter;
    private ChatRecyclerViewAdapter adapter;
    RelativeLayout activity_chat;
    Button button;
    DatabaseReference reference;
    EditText input;
    private final Map<String,Message> messages=new HashMap<>();
    private Chat chat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final PlainChat chat = (PlainChat) getIntent().getSerializableExtra("chat");
        reference= FirebasePathHelper.getInstance().getChatMessagesReference(chat.getUuid());
        //reference.orderByChild("timeMessage");
        input = (EditText) findViewById(R.id.editText);

        activity_chat = (RelativeLayout) findViewById(R.id.activity_chat);
        button = (Button)findViewById(R.id.button2);
        //присваиваем кнопке обработчик нажатия
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //поле ввода

                //считываем тект из поля ввода и отправляем новый экземпляр сообщения в БД firebase
                FirebasePathHelper.getInstance().addMessageToChat(chat.getUuid(), new Message(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });
        //прежде чем отправить сообщение пользователь нужно сделать проверку того -
        //подписался ли ты на данного пользователя и не занёс ли он тебя в ЧС и открыта ли у него личка
        //
        displayChat();
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessages(NewMessageEvent evt) {
        messages = evt.getArg();
    }*/

    //WholeChatEvent
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatUpdate(WholeChatEvent evt) {
        chat = evt.getArg();
    }

    private void displayChat() {

        RecyclerView listMessages = (RecyclerView) findViewById(R.id.listView);
        LinearLayoutManager mgr = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mgr.setStackFromEnd(true);
        listMessages.setLayoutManager(mgr);
        adapter = new ChatRecyclerViewAdapter(this,reference);
        //listMessages.get
       /* Collections.sort(listMessages, new Comparator<Message>() {
            @Override
            public int compare(Message message1, Message message2) {
                return message1.compareTo(message2);
            }
        });*/
        listMessages.setAdapter(adapter);
    }
   /* @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }*/
}
