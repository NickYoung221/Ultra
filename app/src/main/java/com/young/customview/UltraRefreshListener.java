package com.young.customview;

/**
 * 将下拉刷新和上拉加载整合到了一起，定义一个共同的接口以便进行相应操作的回调
 * Created by yang on 2016/10/16 0016.
 */
public interface UltraRefreshListener {
    //下拉刷新
    void onRefresh();

    //上拉加载
    void addMore();
}
