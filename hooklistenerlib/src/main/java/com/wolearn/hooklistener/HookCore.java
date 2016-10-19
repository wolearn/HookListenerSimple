package com.wolearn.hooklistener;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulei
 * Data: 2016/10/18.
 */

public class HookCore {
    private ListenerManager mListenerManager;

    private HookCore(){}

    private static class SingleHolder{
        public static final HookCore INSTANCE = new HookCore();
    }

    public static HookCore getInstance(){
        return SingleHolder.INSTANCE;
    }


    /**
     * 入口函数
     * @param activity
     */
    public void startHook(Activity activity, ListenerManager listenerManager){
        mListenerManager = listenerManager;

        List<View> views = getAllChildViews(activity);
        for(View v: views){
            hookSingleView(v);
        }
    }

    /**
     * hook 单个view
     * @param view
     */
    private void hookSingleView(View view){
        Class mClassView = null;
        try {
            mClassView = Class.forName("android.view.View");
            Method method = mClassView.getDeclaredMethod("getListenerInfo");
            method.setAccessible(true);
            Object listenerInfoObject = method.invoke(view);

            Class mClassListenerInfo = Class.forName("android.view.View$ListenerInfo");

            Field feildOnClickListener = mClassListenerInfo.getDeclaredField("mOnClickListener");
            feildOnClickListener.setAccessible(true);
            View.OnClickListener mOnClickListenerObject = (View.OnClickListener) feildOnClickListener.get(listenerInfoObject);

            Field feildOnLongClickListener = mClassListenerInfo.getDeclaredField("mOnLongClickListener");
            feildOnLongClickListener.setAccessible(true);
            View.OnLongClickListener mOnLongClickListenerObject = (View.OnLongClickListener) feildOnLongClickListener.get(listenerInfoObject);

            Field feildOnFocusChangeListener = mClassListenerInfo.getDeclaredField("mOnFocusChangeListener");
            feildOnFocusChangeListener.setAccessible(true);
            View.OnFocusChangeListener mOnFocusChangeListenerObject = (View.OnFocusChangeListener) feildOnFocusChangeListener.get(listenerInfoObject);

            View.OnClickListener onClickListenerProxy = new OnClickListenerProxy(mOnClickListenerObject, mListenerManager.mOnClickListener);
            View.OnLongClickListener onLongClickListenerProxy = new OnLongClickListenerProxy(mOnLongClickListenerObject, mListenerManager.mOnLongClickListener);
            View.OnFocusChangeListener onFocusChangeListenerProxy = new OnFocusChangeListenerProxy(mOnFocusChangeListenerObject, mListenerManager.mOnFocusChangeListener);

            feildOnClickListener.set(listenerInfoObject, onClickListenerProxy);
            feildOnLongClickListener.set(listenerInfoObject, onLongClickListenerProxy);
            feildOnFocusChangeListener.set(listenerInfoObject, onFocusChangeListenerProxy);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历需要监听Listenerd的Activity
     * @param activity
     * @return
     */
    public List<View> getAllChildViews(Activity activity) {
        View view = activity.getWindow().getDecorView();
        return getAllChildViews(view);
    }

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }
}
