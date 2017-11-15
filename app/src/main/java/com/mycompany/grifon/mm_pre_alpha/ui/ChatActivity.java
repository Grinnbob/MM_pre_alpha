package com.mycompany.grifon.mm_pre_alpha.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.data.Message;
import com.mycompany.grifon.mm_pre_alpha.data.PlainChat;

public class ChatActivity extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<Message> adapter;
    RelativeLayout activity_chat;
    Button button;
    DatabaseReference reference;
    EditText input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final PlainChat chat = (PlainChat) getIntent().getSerializableExtra("chat");
        reference= FirebasePathHelper.getChatMessagesReference(chat.getUuid());
        input = (EditText) findViewById(R.id.editText);

        activity_chat = (RelativeLayout) findViewById(R.id.activity_chat);
        button = (Button)findViewById(R.id.button2);
        //присваиваем кнопке обработчик нажатия
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //поле ввода

                //считываем тект из поля ввода и отправляем новый экземпляр сообщения в БД firebase
                FirebasePathHelper.addMessageToChat(chat.getUuid(), new Message(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });
        //прежде чем отправить сообщение пользователь нужно сделать проверку того -
        //подписался ли ты на данного пользователя и не занёс ли он тебя в ЧС и открыта ли у него личка
        //
        displayChat();
    }

    private void displayChat() {

        ListView listMessages = (ListView) findViewById(R.id.listView);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message_item, reference) {
            @Override
            protected void populateView(View v, Message model, int position) {

                TextView textMessage, autor, timeMessage;
                textMessage = (TextView) v.findViewById(R.id.tvMessage);
                autor = (TextView) v.findViewById(R.id.tvUser);
                timeMessage = (TextView) v.findViewById(R.id.tvTime);

                textMessage.setText(model.getTextMessage());
                autor.setText(model.getAutor());
                timeMessage.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimeMessage()));
            }
        };
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
