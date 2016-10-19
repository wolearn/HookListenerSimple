package com.wolearn.hooklistener;

import android.util.Log;
import android.view.View;

/**
 * Created by wulei
 * Data: 2016/10/17.
 */

public class OnLongClickListenerProxy implements View.OnLongClickListener{
    private View.OnLongClickListener object;
    private HookListenerContract.OnLongClickListener mlistener;

    public OnLongClickListenerProxy(View.OnLongClickListener object, HookListenerContract.OnLongClickListener listener){
        this.object = object;
        this.mlistener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        Log.e("OnLongClickProxy", "-------------OnLongClickListenerProxy-----------");
        if(mlistener != null) mlistener.doInListener(v);
        if(object != null) return object.onLongClick(v);
        return false;
    }
}
