package com.wolearn.hooklibtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.edt)
    EditText edt;
    @Bind(R.id.button)
    Button button;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.activity_main)
    RelativeLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button, R.id.button1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv:
                break;
            case R.id.button:
                break;
            case R.id.button1:
                startActivity(new Intent(this, Main2Activity.class));
                finish();
                break;
        }
    }

    @OnLongClick({R.id.edt, R.id.button, R.id.button1})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.tv:
                break;
            case R.id.button:
                break;
            case R.id.button1:
                break;
        }
        return false;
    }

}
