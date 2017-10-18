package com.music.android.base;

/**
 * Created by liuyun on 17/4/17.
 */

/**
 * Presenter 是Model和View的中间衔接层，一个标准的Presenter中应该至少包含一个Model和一个View。<br/>
 * 在MVP模式中，View是不允许与Model交互的，View只是负责展示数据，是彻底的哑View。当View需要更新数据时，
 * 需要通过Presenter来操作Model，Model获取到数据后再通过Presenter来更新View。<br/>
 * 所以数据流通是这样的:
 *      ->           ->           Remote data source
 * View    Presenter    Model ->
 *      <-            <-          Local data source
 * <br/>
 * 需要注意：Presenter不直接操作数据（更新数据状态），需要调用Model操作数据（更新数据状态）<br/>
 *
 */

public interface BasePresenter {

    void start();
}
