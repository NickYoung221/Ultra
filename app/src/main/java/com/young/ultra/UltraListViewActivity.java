package com.young.ultra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.young.customview.CustomUltraRefreshHeader;
import com.young.customview.UltraRefreshListView;
import com.young.customview.UltraRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * 使用自定义的UltraRefreshListView
 */
public class UltraListViewActivity extends AppCompatActivity implements UltraRefreshListener {

    private PtrClassicFrameLayout mPtrFrame;

    private List<HashMap<String,String>> datas = new ArrayList<>();

    //private SimpleBaseAdapter mAdapter;
    private SimpleAdapter mAdapter;

//    private UltraRefreshListView mLv;
    private UltraRefreshListView mLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultra_listview);

        //查找控件
        mPtrFrame = ((PtrClassicFrameLayout) findViewById(R.id.ultra_ptr));

        mLv = ((UltraRefreshListView) findViewById(R.id.ultra_lv));

        //创建我们的自定义头部视图
        CustomUltraRefreshHeader header = new CustomUltraRefreshHeader(this);

        //设置头部视图
        mPtrFrame.setHeaderView(header);

        //设置视图修改的回调，因为我们的CustomUltraRefreshHeader实现了PtrUIHandler
        mPtrFrame.addPtrUIHandler(header);

        //设置数据刷新的会回调，因为UltraRefreshListView实现了PtrHandler
        mPtrFrame.setPtrHandler(mLv);

        mAdapter = new SimpleAdapter(this,datas,R.layout.item,new String[]{"data"},new int[]{R.id.tv});

        mLv.setAdapter(mAdapter);

        //设置数据刷新回调接口
        mLv.setUltraRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                datas.clear();
                for(int i = 0;i<20;i++){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("data","添加了数据~~"+i);
                    datas.add(map);
                }
                //刷新完成
                mLv.refreshComplete();
                mAdapter.notifyDataSetChanged();
            }
        },1000);

    }

    @Override
    public void addMore() {
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {

                int count = mAdapter.getCount();
                for(int i = count; i< count +10; i++){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("data","添加了数据~~"+i);
                    datas.add(map);
                }
                mAdapter.notifyDataSetChanged();
                //刷新完成
                mLv.refreshComplete();
            }
        },1000);

    }



}
