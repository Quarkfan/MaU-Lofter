package oracle.mau.main.message.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.mau.R;
import oracle.mau.application.MaUApplication;
import oracle.mau.entity.LabelRecommendEntity;
import oracle.mau.entity.SpecialEntity;
import oracle.mau.http.bean.BeanData;
import oracle.mau.http.common.Callback;
import oracle.mau.http.common.HttpServer;
import oracle.mau.http.constants.URLConstants;
import oracle.mau.http.data.SpecialData;
import oracle.mau.http.data.SpecialListData;
import oracle.mau.http.data.UserData;
import oracle.mau.http.parser.SpecialListParser;
import oracle.mau.http.parser.UserParser;
import oracle.mau.main.MainActivity;
import oracle.mau.main.account.activity.AboutUs;
import oracle.mau.main.label.adapter.LabelMainReommendLabelLVAdapter;
import oracle.mau.main.loginAndregister.LoginActivity;
import oracle.mau.main.message.activity.SpecialDetailActivity;
import oracle.mau.main.message.adapter.NewsListAdapter;
import oracle.mau.view.ListViewForScrollView;

/**
 * Created by shadow on 2017/3/2.
 */

public class TalkFragment extends Fragment  implements PullToRefreshBase.OnRefreshListener<ScrollView> ,AdapterView.OnItemClickListener{
//    private ListViewForScrollView listView;

    private ListViewForScrollView lv_label_main_label_recommend;
    private ArrayList<LabelRecommendEntity> lrList;
    /**
     * 下拉刷新
     */

    private final int LABEL_MAIN_PULL_UPDATE = 100001;
    private PullToRefreshScrollView mPullRefreshScrollView;

    private ArrayList<SpecialEntity> listSpecial=new ArrayList<>();
    /**
     * 判断是否是第一次加载
     */
    private boolean isFirst = true;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_msg_talk,null);
//        listView=(ListViewForScrollView) view.findViewById(R.id.msg_talk_listview);
        lv_label_main_label_recommend = (ListViewForScrollView) view.findViewById(R.id.lv_label_main_label_recommend);

//        listView.setOnItemClickListener(this);
        /**
         * 初始化下拉刷新、设置监听(去获取数据)
         */
        mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_label_main_scrollview);
        mPullRefreshScrollView.smoothScrollTo(0, 0);//将ScrollView滚动至最顶端（自定义的listview影响下的效果）
        mPullRefreshScrollView.setOnRefreshListener(this);
        if (isFirst) {
            //向服务器请求专题数据
            sendMessages();
            isFirst = false;
        }


        return  view;
    }

    public void sendMessages(){

        HttpServer.sendPostRequest(HttpServer.HTTPSERVER_GET, null, new SpecialListParser(), URLConstants.BASE_URL + URLConstants.gET_SPECIAL_LIST, new Callback() {


            @Override
            public void success(BeanData beanData) {
                SpecialListData uData = (SpecialListData) beanData;
                listSpecial=uData.getSpecialEntityList();
                //初始化标签推荐listview数据
                initLabelRecommendLVData();
                /**
                 * 下拉刷新返回头部
                 */
                mPullRefreshScrollView.onRefreshComplete();
            }


            @Override
            public void failure(String error) {
                Toast.makeText(getActivity(),error,Toast.LENGTH_LONG).show();
            }
        });


    }


    /**
     * 下拉刷新的监听方法
     * 用于去获取数据
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        sendMessages();
    }

    /**
     * 初始化标签推荐listview数据
     */
    private void initLabelRecommendLVData() {
        lrList = new ArrayList<>();
        LabelRecommendEntity lr1 = new LabelRecommendEntity();
        lr1.setLrTitle(listSpecial.get(0).getSpecialTitle());
        lr1.setLrParticipationNum(12875);
        lrList.add(lr1);

        LabelRecommendEntity lr2 = new LabelRecommendEntity();
        lr2.setLrTitle(listSpecial.get(1).getSpecialTitle());
        lr2.setLrParticipationNum(34875);
        lrList.add(lr2);

        LabelRecommendEntity lr3 = new LabelRecommendEntity();
        lr3.setLrTitle(listSpecial.get(2).getSpecialTitle());
        lr3.setLrParticipationNum(66875);
        lrList.add(lr3);

        LabelRecommendEntity lr4 = new LabelRecommendEntity();
        lr4.setLrTitle(listSpecial.get(3).getSpecialTitle());
        lr4.setLrParticipationNum(232875);
        lrList.add(lr4);

        int[] bgs = {R.mipmap.mh9, R.mipmap.mh10, R.mipmap.mh11, R.mipmap.mh12};
       /* int[] lrImgs1 = {R.mipmap.renxiang1, R.mipmap.renxiang2, R.mipmap.renxiang3};
        int[] lrImgs2 = {R.mipmap.caise1, R.mipmap.caise2, R.mipmap.caise3};
        int[] lrImgs3 = {R.mipmap.chuntian1, R.mipmap.chuntian2, R.mipmap.chuntian3};
        int[] lrImgs4 = {R.mipmap.miaoxingren1, R.mipmap.miaoxingren2, R.mipmap.miaoxingren3};*/
        ArrayList<String> lrImgs1=listSpecial.get(0).getPiclist();
        ArrayList<String> lrImgs2=listSpecial.get(1).getPiclist();
        ArrayList<String> lrImgs3=listSpecial.get(2).getPiclist();
        ArrayList<String> lrImgs4=listSpecial.get(3).getPiclist();
        ArrayList<ArrayList<String>> imgsList = new ArrayList<>();
        imgsList.add(lrImgs1);
        imgsList.add(lrImgs2);
        imgsList.add(lrImgs3);
        imgsList.add(lrImgs4);
        LabelMainReommendLabelLVAdapter adapter = new LabelMainReommendLabelLVAdapter(getContext(), lrList, bgs, imgsList);
        lv_label_main_label_recommend.setAdapter(adapter);
        lv_label_main_label_recommend.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            /*Intent intentSpecial=new Intent(getActivity(), SpecialDetailActivity.class);

            intentSpecial.putExtra("special_id",listSpecial.get(i).getSpecialId());
            startActivity(intentSpecial);*/
            SpecialDetailActivity.actionGetid(getActivity(),listSpecial.get(i).getSpecialId()+"");

    }
}
