package com.mycompany.grifon.mm_pre_alpha.engine.eventbus;

import android.support.v7.app.AppCompatActivity;
import org.greenrobot.eventbus.EventBus;

public class EBActivity extends AppCompatActivity {
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
