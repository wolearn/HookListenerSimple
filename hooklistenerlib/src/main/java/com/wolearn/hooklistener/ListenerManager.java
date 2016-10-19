package com.wolearn.hooklistener;

/**
 * Created by wulei
 * Data: 2016/10/18.
 */

public class ListenerManager {
    public HookListenerContract.OnFocusChangeListener mOnFocusChangeListener;
    public HookListenerContract.OnClickListener mOnClickListener;
    public HookListenerContract.OnLongClickListener mOnLongClickListener;

    private ListenerManager(){};

    public static ListenerManager create(Builer builer){
        if (builer == null){
            return null;
        }
        return builer.build();
    }

    public static class Builer{
        private ListenerManager listenerManager = new ListenerManager();

        public Builer buildOnFocusChangeListener(HookListenerContract.OnFocusChangeListener onFocusChangeListener){
            listenerManager.mOnFocusChangeListener = onFocusChangeListener;
            return this;
        }

        public Builer buildOnClickListener(HookListenerContract.OnClickListener onClickListener){
            listenerManager.mOnClickListener = onClickListener;
            return this;
        }

        public Builer buildOnLongClickListener(HookListenerContract.OnLongClickListener onLongClickListener){
            listenerManager.mOnLongClickListener = onLongClickListener;
            return this;
        }

        public ListenerManager build(){
            return listenerManager;
        }
    }
}
