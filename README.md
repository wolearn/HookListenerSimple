# Android Hook Listener
![](http://upload-images.jianshu.io/upload_images/1931006-e0dc7387c8fd761d.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
# 背景
需求如下，在不侵入业务代码的情况下监听所有的点击事件，并记录所有的点击数，用于统计热点页面和其他一些分析工作。仔细分析了下，主要涉及到2个问题：
* 如何获取所有的带点击事件的View对象
* 不改动原来的业务代码，但是在原来点击事件的业务逻辑中加入新的逻辑，如点击数上传等

最大的难点是怎么在不改变onclickListener->onclick(){}内部的代码块的同时，又能插入一段新的逻辑。毕竟不能把老的监听器一个个找出来去加，不仅可能会漏掉而且会。。。疯掉。。。

# 先说解决方案
面对这个问题，我想肯定会有童鞋跟我遇到类似的问题。对于类似的问题，可以抽象出来统一解决，所以我撸了个库。当然本文的精髓不是怎么使用这个库，而是后面原理分析的部分，也就是解决这个问题的过程。源码的github地址如下
> https://github.com/wolearn/HookListenerSimple/tree/master

本来想放到jcenter上浪一下，可惜目前还没上传成功。

![](http://upload-images.jianshu.io/upload_images/1931006-af3a31dabcb9dd90.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

但是我还是准备了arr包，包含一下也很方便。如果要修改源码就用module的方式引入吧。库不大就20K多一点，引入随意。

# 用法
具体看你想在单个Activity中使用，建议在如下函数中添加
```
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
```
* 可以实现点击，长按，焦点三种事件，按照自己的需求build。
* 理论上是可以实现所有的监听器的，各位可以自己扩展。
* doInListener中的代码会在监听器触发的时候执行，如果你想在服务端记录这次行为，可以在这里执行。
* 如果想实现所有Activity，可以把上面的代码写在BaseActivity中。也可以参考源码中的例子。

#原理分析
这里面主要涉及三个方面的知识点
* 静态代理
* 反射
*  **怎么Hook**

###1. 静态代理
我理解的代理就是，代理具备被代理对象的所有能力，并且可以在该能力执行的时候添油加醋。
>用程序的语言来说就是，代理对象持有了被代理对象的引用，当被代理方法执行的时候，在执行的前后添加一些逻辑或是修改返回的结果。

举个简单的例子如下。先抽象个简单的狗。
```
public interface IDog {
    void eat();
    void drink();
}
```
创建一个具体的黑色狗狗
```
public class BlankDog implements IDog{
    @Override
    public void eat() {
        Log.i("BlankDog", "----  eat  -----");
    }

    @Override
    public void drink() {
        Log.i("BlankDog", "----  drink  -----");
    }
}
```
最重要的代理黑狗
```
public class DogProxy implements IDog{
    private BlankDog mBlankDog;

    public DogProxy(BlankDog blankDog){
        this.mBlankDog = blankDog;
    }

    @Override
    public void eat() {
        Log.i("","----  在吃之前先撒个欢  -----");
        mBlankDog.eat();
    }

    @Override
    public void drink() {
        mBlankDog.drink();
        Log.i("","----  在喝之后撒个欢  -----");
    }
}
```
发现所有黑狗会的技能，代理狗都会，而且还做的一样好，甚至在吃喝之前还撒个欢，有了自己的个性。更多设计模式相关的知识，可以参考我的[白话设计模式六大原则](http://www.jianshu.com/p/a489dd5ad1fe)。

###2. 反射
这里面涉及到几个的反射方法
>Class.forName()

通过类名获取Class对象。
>setAccessible()

改变访问对象的可见性，常常用来访问private属性的对象。
>invoke(Object receiver, Object... args)

通过对象和参数列表执行方法。
>get(Object object)

获取Feild的值。

###3. 怎么Hook
先理清Hook是什么？翻译过来是钩子。
>我的理解是一些已有的API不能满足新的需求，可以通过Hook来改变其功能，或在原API前后插入新的代码，或改变其返回值，或干脆覆盖掉原来的代码，来实现新的需求。

结合文章前面的需求，我们要做的就是在Android源码调用监听器的逻辑中加入一段自己的代码，来统一监听监听器的执行。通过阅读View的源码发现一个很有用的方法
```
    ListenerInfo getListenerInfo() {
        if (mListenerInfo != null) {
            return mListenerInfo;
        }
        mListenerInfo = new ListenerInfo();
        return mListenerInfo;
    }
```
所有监听器的对象都是保存在ListenerInfo类型的对象中
```
static class ListenerInfo {
        protected OnFocusChangeListener mOnFocusChangeListener;
        private ArrayList<OnLayoutChangeListener> mOnLayoutChangeListeners;
        protected OnScrollChangeListener mOnScrollChangeListener;
        private CopyOnWriteArrayList<OnAttachStateChangeListener> mOnAttachStateChangeListeners;
        public OnClickListener mOnClickListener;
        protected OnLongClickListener mOnLongClickListener;
        protected OnContextClickListener mOnContextClickListener;
        protected OnCreateContextMenuListener mOnCreateContextMenuListener;
        private OnKeyListener mOnKeyListener;
        private OnTouchListener mOnTouchListener;
        private OnHoverListener mOnHoverListener;
        private OnGenericMotionListener mOnGenericMotionListener;
        private OnDragListener mOnDragListener;
        private OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;
        OnApplyWindowInsetsListener mOnApplyWindowInsetsListener;
    }
```
接下来的事情就简单了，执行getListenerInfo()方法获取mListenerInfo对象，然后用静态代理对象替换掉mListenerInfo的各种监听器的成员变量，在我们自己的代理对象中我们就可以为所欲为了。比如OnclickListener的代理对象
```
public class OnClickListenerProxy implements View.OnClickListener{
    private View.OnClickListener object;
    private HookListenerContract.OnClickListener mlistener;

    public OnClickListenerProxy(View.OnClickListener object, HookListenerContract.OnClickListener listener){
        this.object = object;
        this.mlistener = listener;
    }

    @Override
    public void onClick(View v) {
        if(mlistener != null) mlistener.doInListener(v);
        if(object != null) object.onClick(v);
    }
}
```
mlistener是我们要插入逻辑的承载对象。其他监听器的代理实现类似。接下来只要把原来的监听器对象换成代理对象即可。替换过程如下。
```
 mClassView = Class.forName("android.view.View");
 Method method = mClassView.getDeclaredMethod("getListenerInfo");
 method.setAccessible(true);
 Object listenerInfoObject = method.invoke(view);

 Class mClassListenerInfo = Class.forName("android.view.View$ListenerInfo");

 Field feildOnClickListener = mClassListenerInfo.getDeclaredField("mOnClickListener");
 feildOnClickListener.setAccessible(true);
 View.OnClickListener mOnClickListenerObject = (View.OnClickListener) feildOnClickListener.get(listenerInfoObject);

View.OnClickListener onClickListenerProxy = new OnClickListenerProxy(mOnClickListenerObject, mListenerManager.mOnClickListener);

feildOnClickListener.set(listenerInfoObject, onClickListenerProxy);
```
hook监听器的过程就完成了，HookListenerContract.OnClickListener()->doInListener(View v)中的方法或随着监听器的触发执行。其他的监听器类似。

#后记
效果大家可以下载代码试试。欢迎大家评论，点赞。。。。

#关注我(微信扫一扫，订阅号：程序小船)
![](http://upload-images.jianshu.io/upload_images/1931006-a94af6d0df05701b.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
