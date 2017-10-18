package com.music.android.base;

/**
 * Created by liuyun on 17/4/17.
 */

/**
 * MVP 中的View，View必须是哑View，即只能是显示数据，响应操作，除此之外没有任何权限了，
 * 不能够直接操作Model（当然也不能直接操作Model传递过来的Bean对象，一定要做到View只显示数据和响应操作）。
 * 并且View中也要尽量去除所有逻辑操作，比如Activity的各个生命周期方法，以及Activity的
 * onActivityResult(int requestCode, int resultCode, Intent data)方法都需要放到Presenter中托管和处理。
 */

public interface BaseView<T> {
    /**
     * 让View持有Presenter，这样可以让View在合适的时机（View初始化完成）来调用Presenter中的方法加载数据
     */
    void setPresenter(T presenter);
}
