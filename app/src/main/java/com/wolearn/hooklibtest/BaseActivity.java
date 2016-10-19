package com.wolearn.hooklibtest;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wolearn.hooklistener.HookCore;
import com.wolearn.hooklistener.HookListenerContract;
import com.wolearn.hooklistener.ListenerManager;

/**
 * Created by wulei
 * Data: 2016/10/18.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ListenerManager.Builer builer = new ListenerManager.Builer();
        builer.buildOnClickListener(new HookListenerContract.OnClickListener() {
            @Override
            public void doInListener(View v) {
                Toast.makeText(BaseActivity.this, "单击时我执行", Toast.LENGTH_SHORT).show();
            }
        }).buildOnLongClickListener(new HookListenerContract.OnLongClickListener() {
            @Override
            public void doInListener(View v) {
                Toast.makeText(BaseActivity.this, "长按时我执行", Toast.LENGTH_SHORT).show();
            }
        }).buildOnFocusChangeListener(new HookListenerContract.OnFocusChangeListener() {
            @Override
            public void doInListener(View v, boolean hasFocus) {
                Toast.makeText(BaseActivity.this, "焦点变化时我执行", Toast.LENGTH_SHORT).show();
            }
        });
        HookCore.getInstance().startHook(this, ListenerManager.create(builer));
    }
}
