package com.wolearn.hooklibtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class Main2Activity extends BaseActivity {

    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.button)
    Button button;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.activity_main2)
    RelativeLayout activityMain2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv, R.id.button, R.id.button1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv:
                break;
            case R.id.button:
                break;
            case R.id.button1:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }

    @OnLongClick({R.id.tv, R.id.button, R.id.button1})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.tv:
                break;
            case R.id.button:
                break;
            case R.id.button1:
                break;
        }
        return true;
    }
}
