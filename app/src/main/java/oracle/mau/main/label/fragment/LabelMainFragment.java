package oracle.mau.main.label.fragment;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import oracle.mau.R;
import oracle.mau.base.BaseFragment;
import oracle.mau.entity.ArticleEntity;
import oracle.mau.entity.LabelTagEntity;
import oracle.mau.entity.LabelTagNoListEntity;
import oracle.mau.entity.UserEntity;
import oracle.mau.http.bean.BeanData;
import oracle.mau.http.common.Callback;
import oracle.mau.http.common.HttpServer;
import oracle.mau.http.constants.URLConstants;
import oracle.mau.http.data.ArticleData;
import oracle.mau.http.data.LabelTagData;
import oracle.mau.http.data.HotUserData;
import oracle.mau.http.parser.ArticlePicParser;
import oracle.mau.http.parser.LabelTagParser;
import oracle.mau.http.parser.HotUserParser;
import oracle.mau.main.account.activity.AccountDetailActivity;
import oracle.mau.main.label.activity.ArticleDetailActivity;
import oracle.mau.main.label.adapter.LabelMainRecommendUserGVAdapter;
import oracle.mau.main.label.adapter.LabelMessageArticleGVAdapter;
import oracle.mau.main.label.adapter.TagGalleryVPAdapter;
import oracle.mau.main.label.view.TouchViewPager;
import oracle.mau.utils.ScreenUtils;
import oracle.mau.view.GridViewForScrollView;

/**
 * Created by 田帅 on 2017/2/28.
 */

public class LabelMainFragment extends BaseFragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {

    /**
     * 用户推荐gridview
     */
    private GridViewForScrollView gv_label_main_user_recommend;
    private List<UserEntity> userList;
    /**
     * 进度条
     */
    private AVLoadingIndicatorView avi;

    /**
     * 标签画廊数据
     */
    private TouchViewPager vp_label_tag;
    private List<LabelTagEntity> tagList;
    /**
     * 下拉刷新
     */
    private PullToRefreshScrollView mPullRefreshScrollView;
    //标记是否是下拉刷新
    private int updateDowmFlag = 0;
    //标记几次上拉加载,默认是第一页
    private int updateUpFlag = 1;
    private boolean isFirst = true;

    /**
     * 文章推荐网格
     */
    private GridViewForScrollView gv_label_main_article;
    private List<ArticleEntity> articleList;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_label_main;
    }

    @Override
    protected void initView() {
        avi = (AVLoadingIndicatorView) rootView.findViewById(R.id.avi);
        vp_label_tag = (TouchViewPager) rootView.findViewById(R.id.vp_label_tag);
        gv_label_main_user_recommend = (GridViewForScrollView) rootView.findViewById(R.id.gv_label_main_user_recommend);
        gv_label_main_user_recommend.setOnItemClickListener(this);
        gv_label_main_article = (GridViewForScrollView) rootView.findViewById(R.id.gv_label_main_article);
        gv_label_main_article.setOnItemClickListener(this);
        /**
         * 初始化下拉刷新、设置监听(去获取数据)
         */
        mPullRefreshScrollView = (PullToRefreshScrollView) rootView.findViewById(R.id.ptr_label_main_scrollview);
        mPullRefreshScrollView.setOnRefreshListener(this);
        if (isFirst) {
            //显示进度条
            avi.show();
            //初始化达人推荐gridview数据
            initUserRecommendGVData();
            //初始化标签画廊数据
            initTagGalleryData();
            //初始化文章数据
            initArticleRecommendGVData();
            isFirst = false;
        }
    }

    /**
     * 初始化文章推荐网格
     */
    private void initArticleRecommendGVData() {
        HttpServer.sendPostRequest(HttpServer.HTTPSERVER_GET, null, new ArticlePicParser(), URLConstants.BASE_URL + URLConstants.ARTICLE_RECOMMEND + updateUpFlag, new Callback() {
            @Override
            public void success(BeanData beanData) {
                ArticleData data = (ArticleData) beanData;
                /**
                 * 得到数据
                 */
                if (updateUpFlag > 1) {
                    List<ArticleEntity> newArticleList = data.getArticleList();
                    for (ArticleEntity ae : newArticleList) {
                        articleList.add(ae);
                    }
                    /**
                     * 最后掉调用该方法缩回头部（主线程中）
                     */
                    mPullRefreshScrollView.onRefreshComplete();
                } else {
                    articleList = data.getArticleList();
                }
//                toast("第 " + updateUpFlag + " 次" + ",数据长度为：  " + articleList.size());
                initArticleRecommendGV();
            }

            @Override
            public void failure(String error) {
                mPullRefreshScrollView.onRefreshComplete();
            }
        });
    }

    /**
     * 初始化文章推荐网格
     */
    private void initArticleRecommendGV() {
        LabelMessageArticleGVAdapter messageRecommendGVAdapter = null;
        /**
         * 设置适配器
         */
        messageRecommendGVAdapter = new LabelMessageArticleGVAdapter(mContext, articleList);
        gv_label_main_article.setAdapter(messageRecommendGVAdapter);
        /**
         * 防止没网状态，上拉崩溃
         */
        if (messageRecommendGVAdapter != null) {
            messageRecommendGVAdapter.notifyDataSetChanged();
        }


    }

    /**
     * 初始化达人推荐网格
     */
    private void initUserRecommendGV() {
        LabelMainRecommendUserGVAdapter userAdapter = new LabelMainRecommendUserGVAdapter(mContext, userList);
        gv_label_main_user_recommend.setAdapter(userAdapter);
    }

    /**
     * 初始化达人推荐gridview数据
     */
    private void initUserRecommendGVData() {
        HotUserParser parser = new HotUserParser();
        HttpServer.sendPostRequest(HttpServer.HTTPSERVER_GET, null, parser, URLConstants.BASE_URL + URLConstants.HOT_USER, new Callback() {
            @Override
            public void success(BeanData beanData) {
                HotUserData uData = (HotUserData) beanData;
                userList = uData.getUserList();
                initUserRecommendGV();
                if (updateDowmFlag > 0) {
                    /**
                     * 最后掉调用该方法缩回头部（主线程中）
                     */
                    mPullRefreshScrollView.onRefreshComplete();
                }
                //隐藏进度条
                avi.hide();
            }

            @Override
            public void failure(String error) {

            }
        });
    }


    /**
     * 初始化画廊数据
     */
    private void initTagGalleryData() {
        LabelTagParser parser = new LabelTagParser();
        HttpServer.sendPostRequest(HttpServer.HTTPSERVER_GET, null, parser, URLConstants.BASE_URL + URLConstants.TAG_GALLERY, new Callback() {
            @Override
            public void success(BeanData beanData) {
                LabelTagData data = (LabelTagData) beanData;
                tagList = data.getList();
                if (tagList != null) {
                    initTagGallery();
                }
            }

            @Override
            public void failure(String error) {

            }
        });
    }

    /**
     * 初始化画廊
     */
    private void initTagGallery() {
        int mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        vp_label_tag.setPageMargin(-mScreenWidth / 2);
        vp_label_tag.setOffscreenPageLimit(tagList.size());
        /**
         * 缩放
         */
        vp_label_tag.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                if (Math.abs(position) == 1) {
                    position = 1;
                }
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });
        /**
         * 实体类中有List不能通过序列化传
         */
        List<LabelTagNoListEntity> tagNoListEntityList = new ArrayList<>();
        for (LabelTagEntity lt : tagList) {
            int tagId = lt.getTagId();
            String tagTitle = lt.getTagTitle();
            LabelTagNoListEntity tagNoListEntity = new LabelTagNoListEntity();
            tagNoListEntity.setTagId(tagId);
            tagNoListEntity.setTagTitle(tagTitle);
            tagNoListEntityList.add(tagNoListEntity);
        }
        vp_label_tag.setTagList(tagNoListEntityList);
        TagGalleryVPAdapter galleryAdapter = new TagGalleryVPAdapter(tagList, mContext);
        /**
         * 动态设置vp的宽高
         */
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(screenWidth, screenWidth * 3 / 4);
        lp.setMargins(0, 30, 0, 0);
        vp_label_tag.setLayoutParams(lp);
        vp_label_tag.setAdapter(galleryAdapter);
        vp_label_tag.setCurrentItem(1000);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gv_label_main_user_recommend:
                AccountDetailActivity.actionStart(mContext, userList.get(position).getUserid());
                break;
            case R.id.gv_label_main_article:
                ArticleDetailActivity.actionStart(mContext, articleList.get(position).getArticleId());
                break;
        }
    }

    /**
     * 下拉刷新的监听方法
     * 用于去获取数据
     *
     * @param refreshView
     */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        updateDowmFlag++;
        initUserRecommendGVData();
    }

    /**
     * 上拉加载的监听方法
     * 用于去获取数据
     *
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        updateUpFlag++;
        initArticleRecommendGVData();
    }
}
