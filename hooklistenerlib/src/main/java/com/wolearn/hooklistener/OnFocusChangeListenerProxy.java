package com.wolearn.hooklistener;

import android.util.Log;
import android.view.View;

/**
 * Created by wulei
 * Data: 2016/10/17.
 */

public class OnFocusChangeListenerProxy implements View.OnFocusChangeListener{
    private View.OnFocusChangeListener object;
    private HookListenerContract.OnFocusChangeListener mlistener;

    public OnFocusChangeListenerProxy(View.OnFocusChangeListener object, HookListenerContract.OnFocusChangeListener listener){
        this.object = object;
        this.mlistener = listener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.e("OnFocusChangeProxy", "---------------OnFocusChangeListenerProxy-------------");
        if(mlistener != null) mlistener.doInListener(v, hasFocus);
        if(object != null) object.onFocusChange(v, hasFocus);
    }
}
