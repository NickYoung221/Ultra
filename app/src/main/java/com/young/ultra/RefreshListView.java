package com.young.ultra;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 自定义的ListView，实现下拉刷新，上拉加载
 * Created by yang on 2016/10/13 0013.
 *  1、给ListView添加头部
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener{
    OnRefreshLoadChangeListener onRefreshLoadChangeListener; //自定义的接口

    private int headHeight; //headView的高度
    View headView; //头部布局
    View footView; //底部布局
    int headState; //头部控件的状态
    int firstVisibleItem; //第一个显示的Item
    boolean flag = false; //表示是否在下拉（默认没有下拉）

    private static final int INIT = 0; //初始化状态：头部不显示
    private static final int PrepareRefreshing = 1; //准备刷新：头部会全部显示
    private static final int Refreshing = 2;   //正在刷新
    private float downY; //按下去的时候，Y轴的坐标
    private float moveY; //移动的时候，Y轴的坐标

    private RotateAnimation upAnimation;     //向上的动画
    private RotateAnimation downAnimation;   //向下的动画
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView tvRefreshState;
    private TextView tvRefreshTime;
    private ProgressBar footPb;
    TextView footTv;
    boolean isLoading = false; //是否处于加载状态

    public RefreshListView(Context context) {
        this(context,null);
    }
    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initHead(context);
        initAnimation();
        initFoot(context);
        this.setOnScrollListener(this);//这里如果不写的话，是不会监听的，即onScrollStateChanged和onScroll不会执行
    }

    //初始化头部
    void initHead(Context context){
        headView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_head,null);
        //初始化头部控件
//        imageView= (ImageView) headView.findViewById(R.id.iv_refresher);
//        progressBar=(ProgressBar) headView.findViewById(R.id.pb_refresher);
//        tvRefreshState = (TextView) headView.findViewById(R.id.tv_refreshertext);
//        tvRefreshTime = (TextView) headView.findViewById(R.id.tv_refreshtime);
//
//        //获取headView的高度
//        headView.measure(0,0);  //不执行的话测不出headView的高度
//        headHeight = headView.getMeasuredHeight();
//
//        //设置头部不显示
//        headView.setPadding(0,-headHeight,0,0);

        //ListView添加头部
        addHeaderView(headView);
    }

    //初始化底部
    void initFoot(Context context){
        footView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer,null);
        footPb = (ProgressBar) footView.findViewById(R.id.footer_progressbar);
        footTv = (TextView) footView.findViewById(R.id.footer_hint_textview);

        addFooterView(footView);//添加footView
    }

    //初始化动画
    void initAnimation(){
        //0-》180：选择中心点在自身的原点
        upAnimation=new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setFillAfter(true);
        upAnimation.setDuration(1000);
        downAnimation=new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setFillAfter(true);
        downAnimation.setDuration(1000);
    }

    //onTouchEvent有三种状态：action_down（当按下）,action_move（移动）,action_up（当抬起）
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        switch(ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                flag = false; //每次点击之前，让flag变为初始状态
//                //当手按下去的时候，记录初始值
//                downY = ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                //Log.i("RefreshListView", "onTouchEvent1: --"+headState);
//                //如果是正在刷新状态，则停止执行
//                if(headState==Refreshing){
//                    return false;
//                }
//                moveY = ev.getY();  //滑动点Y轴的坐标
//                //判断：当第一条记录可见&&下拉(不是上拉)，跟着头部改变
//                if(firstVisibleItem==0 && moveY-downY>0){
//                    flag = true; //是下拉状态
//                    float headPadding = -headHeight+(moveY-downY);//拉出的时候，头部的Padding（上边距）
//
//                    //下拉过程中状态改变时：头部显示出来==》headState改为：准备刷新
//                    if(headPadding>=0&&headState==INIT){ //这种情况headState为INIT
//                        headState = PrepareRefreshing;//状态变成准备刷新
//                        changeHeadState();//改变界面控件
//                    }
//                    //下拉过程中状态改变时：头部没有完全显示出来==》headState改为：INIT
//                    if(headPadding<0&&headState==PrepareRefreshing){//这种情况headState为PrepareRefreshing(下拉又回收了)
//                        headState = INIT;
//                        changeHeadState();//改变界面控件
//                    }
//                    headView.setPadding(0, (int) headPadding,0,0);
//                    //return true;//bug1:如果是向下拉，return,解决方案：加一个flag来记录是否在下拉，然后在Item点击事件里判断flag
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                //准备状态==》正在刷新
//                if(headState==PrepareRefreshing){
//                    headState = Refreshing;
//                    changeHeadState();
//                    //更新数据源
//                    if(onRefreshLoadChangeListener!=null){
//                        onRefreshLoadChangeListener.onRefresh();
//                    }
//                    //headView的padding变为0
//                    headView.setPadding(0,0,0,0);
//                } else if(headState==INIT){
//                    //若是初始状态，松手后，头部不显示
//                    headView.setPadding(0,-headHeight,0,0);
//                }
//                break;
//        }
//        return super.onTouchEvent(ev);
//    }

    //实现AbsListView.OnScrollListener接口需要重写的两个方法
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //滚动状态改变会执行
        //Log.i("RefreshListView", "onScrollStateChanged: "+scrollState);
        if(getLastVisiblePosition()==getCount()-1&&isLoading==false){//表示最后一个Item可见，且没有在加载状态
            if(scrollState==OnScrollListener.SCROLL_STATE_TOUCH_SCROLL||scrollState==OnScrollListener.SCROLL_STATE_IDLE){
                //界面改变（开始加载）--》完成刷新--》界面改变（完成加载）
                isLoading = true; //改变加载状态
                changeFootState(); //改变底部界面的状态
                if(onRefreshLoadChangeListener!=null){
                    onRefreshLoadChangeListener.onLoad();//加载更多数据
                }
            }
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Log.i("RefreshListView", "onScroll: ");
        this.firstVisibleItem = firstVisibleItem; //记录第一个可见的Item（并不一定是ListView的第一个Item）
    }

    //headview不同状态，改变不同的控件状态
    public void changeHeadState(){
        switch(headState){
            case INIT: //初始化的状态
                progressBar.setVisibility(View.INVISIBLE); //设置进度条不可见
                imageView.setVisibility(View.VISIBLE);
                //给imageView设置动画
                imageView.startAnimation(downAnimation);//设置箭头朝下转
                tvRefreshState.setText("下拉刷新");
                tvRefreshTime.setVisibility(View.INVISIBLE);//设置时间不可见
                break;
            case PrepareRefreshing://准备刷新的状态
                progressBar.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.startAnimation(upAnimation);////设置箭头朝上转
                tvRefreshState.setText("释放刷新");
                tvRefreshTime.setVisibility(View.INVISIBLE);
                break;
            case Refreshing://正在刷新的状态
                progressBar.setVisibility(View.VISIBLE);//设置进度条可见
                imageView.setVisibility(View.INVISIBLE);
                imageView.clearAnimation();//清除ImageView的动画
                tvRefreshState.setText("正在刷新");
                tvRefreshTime.setVisibility(View.VISIBLE);
                tvRefreshTime.setText(getTime());//刷新时间
                break;
        }
    }

    //footView处于不同状态，改变不同的控件状态
    public void changeFootState(){
        if(isLoading){
            //正在加载：ProgressBar显示
            footPb.setVisibility(VISIBLE);
            footTv.setVisibility(GONE);
        }else{
            //没有在加载,进图条隐藏，文本显示
            footPb.setVisibility(GONE);
            footTv.setVisibility(VISIBLE);
        }
    }

    //格式化当前时间
    public String getTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time=	format.format(new Date());
        return time;
    }

    public boolean isFlag() {
        return flag;
    }

    //刷新完成要还原状态
    public void completeRefresh(){
        //padding返回去
        headView.setPadding(0,-headHeight,0,0);
        //状态改变：正在刷新-INIT
        headState=INIT;
        changeHeadState();//控件初始化
    }

    //加载完成
    public void completeLoad(){
        isLoading = false; //加载完成
        changeFootState();
    }

    //定义接口：下拉刷新，上拉加载
    public interface OnRefreshLoadChangeListener{
        void onRefresh(); //下拉刷新
        void onLoad();  //上拉加载
    }

    //供其他类实现该接口
    public void setOnRefreshUploadChangeListener(OnRefreshLoadChangeListener onRefreshLoadChangeListener){
        this.onRefreshLoadChangeListener = onRefreshLoadChangeListener;
    }

    //去掉footView
    public void setFootGONE(){
        removeFooterView(footView);
    }
    public void setHeadGONE(){
        removeHeaderView(headView);
    }

    //重写onMeasure方法,这里不能重写该方法，否则放在PtrClassicFrameLayout里划不动
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//
//        super.onMeasure(widthMeasureSpec,expandSpec);
//        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

}
