package com.young.ultra;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Ultra-Pull-To-Refresh是一个功能非常强大的类库，通过他，我们可以实现非常丰富的下拉刷新视图，
 * 并且他支持几乎所有的控件的下拉刷新（不仅仅是ListView），但该视图不支持上拉加载，
 * 作者可能在考虑此库设计时的想法与Google官方的SwipeRefreshLayout的理念符合。
 * 即刷新可能是许多控件都需要，而上拉加载只有列表视图需要
 */
public class UltraRefreshActivity extends AppCompatActivity implements RefreshListView.OnRefreshLoadChangeListener {

    private PtrClassicFrameLayout ptrFrame;
    Button btn;
    RefreshListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultra_refresh);

        ptrFrame = (PtrClassicFrameLayout) findViewById(R.id.ultra_ptr_frame);
//        btn = (Button) findViewById(R.id.btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UltraRefreshActivity.this,UltraListViewActivity.class);
//                startActivity(intent);
//            }
//        });

        ptrFrame.setLastUpdateTimeRelateObject(this);

        //下拉刷新的阻力，下拉时，下拉距离和显示头部的距离比例，值越大，则越不容易滑动
        ptrFrame.setRatioOfHeaderHeightToRefresh(1.2f);

        ptrFrame.setDurationToClose(200);//返回到刷新的位置（暂未找到）

        ptrFrame.setDurationToCloseHeader(1000);//关闭头部的时间 // default is false

        ptrFrame.setPullToRefresh(false);//当下拉到一定距离时，自动刷新（true），显示释放以刷新（false）

        ptrFrame.setKeepHeaderWhenRefresh(true);//见名只意

        //数据刷新的接口回调
        ptrFrame.setPtrHandler(new PtrHandler() {
            //是否能够刷新
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header);
            }
            //开始刷新的回调
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                //数据刷新的回调

                ptrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrame.refreshComplete();
                    }
                }, 1500);
            }
        });

        //UI更新接口的回调
        ptrFrame.addPtrUIHandler(new PtrUIHandler() {
            //刷新完成之后，UI消失之后的接口回调
            @Override
            public void onUIReset(PtrFrameLayout frame) {

            }
            //开始下拉之前的接口回调
            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {

            }
            //开始刷新的接口回调
            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {

            }
            //刷新完成的接口回调
            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {

            }
            //下拉滑动的接口回调，多次调用
            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
                /**
                 * isUnderTouch ：手指是否触摸
                 * status：状态值
                 * ptrIndicator：滑动偏移量等值的封装对象。
                 */
            }
        });



        lv = (RefreshListView) findViewById(R.id.lv);
        lv.setOnRefreshUploadChangeListener(this);
        List<HashMap<String,Object>> stus = new ArrayList<HashMap<String,Object>>();
        //每循环一次，将一个学生对象放到map里面
        for(int i=0;i<30;){
            int j = i++;  //   i++和++i效果不一样
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("stuName","stuName"+j);
            map.put("stuId",j);
            map.put("stuAge",20+j);
            stus.add(map);
        }
        // String[] from={"name","sno"};
        SimpleAdapter adapter = new SimpleAdapter(this,stus,R.layout.simple_item,
                new String[]{"stuName","stuId","stuAge"},
                new int[]{R.id.tv_stuName,R.id.tv_stuId,R.id.tv_stuAge});
        //设置适配器
        lv.setAdapter(adapter);

    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lv.completeLoad();
            }
        },1500);
    }


}
