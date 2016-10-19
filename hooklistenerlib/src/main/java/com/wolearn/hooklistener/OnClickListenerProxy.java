package com.wolearn.hooklistener;

import android.util.Log;
import android.view.View;

/**
 * Created by wulei
 * Data: 2016/10/17.
 */

public class OnClickListenerProxy implements View.OnClickListener{
    private View.OnClickListener object;
    private HookListenerContract.OnClickListener mlistener;

    public OnClickListenerProxy(View.OnClickListener object, HookListenerContract.OnClickListener listener){
        this.object = object;
        this.mlistener = listener;
    }

    @Override
    public void onClick(View v) {
        Log.e("OnClickListenerProxy", "---------------OnClickListenerProxy-------------");
        if(mlistener != null) mlistener.doInListener(v);
        if(object != null) object.onClick(v);
    }
}
