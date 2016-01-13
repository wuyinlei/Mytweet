package com.example.annation.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.annation.R;
import com.example.annation.adapter.ArticleCommentAdapter;
import com.example.annation.http.BaseNetWork;
import com.example.annation.http.HttpResponse;
import com.example.annation.status.CommentEntity;
import com.example.annation.status.StatusEntity;
import com.example.annation.uri.Contants;
import com.example.annation.uri.ParameterKeySet;
import com.example.annation.utils.DividerItemDecoration;
import com.example.annation.utils.PreferenceUtils;
import com.example.annation.widget.PullToRefreshRecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sina.weibo.sdk.net.WeiboParameters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 若兰 on 2016/1/13.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class ArticleCommentActivity extends BaseActivity {

    private StatusEntity mEntities;
    private PullToRefreshRecyclerView recycleview;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;

    private ArticleCommentAdapter articleCommentAdapter;

    private PreferenceUtils mPres;

    private List<CommentEntity> mDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getmToolbarX().setTitle(R.string.article_comment_title);
        mEntities = (StatusEntity) getIntent().getSerializableExtra(StatusEntity.class.getSimpleName());
        mPres = PreferenceUtils.getInstance(getApplicationContext());
        mDataSet = new ArrayList<>();
        initialize();
    }

    @Override
    public int getLayoutId() {
        return R.layout.tweet_commen_recycleview;
    }

    private void initialize() {

        recycleview = (PullToRefreshRecyclerView) findViewById(R.id.recycle_view);
        mLayoutManager = new LinearLayoutManager(this);
        recycleview.setLayoutManager(mLayoutManager);
        mItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);

        recycleview.addItemDecotation(mItemDecoration);
        articleCommentAdapter = new ArticleCommentAdapter(this,mEntities,mDataSet);
        recycleview.setAdapter(articleCommentAdapter);

        new BaseNetWork(this, Contants.API.COMMENT_SHOW) {
            @Override
            public WeiboParameters onPrepares() {
                WeiboParameters parameters = new WeiboParameters(Contants.APP_KEY);
                parameters.put(ParameterKeySet.ID,mEntities.id);
                parameters.put(ParameterKeySet.PAGE,1);
                parameters.put(ParameterKeySet.COUNT,10);
                parameters.put(ParameterKeySet.AUTH_ACCESS_TOKEN,mPres.getToken().getToken());

                return parameters;
            }

            @Override
            public void onFinish(HttpResponse response, boolean success) {
                if (success) {
                    Log.d("ArticleCommentActivity", "response:" + response.response);
                    Type type = new TypeToken<ArrayList<CommentEntity>>(){}.getType();
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(response.response);
                    if (element.isJsonArray()){
                        List<CommentEntity> list = new Gson().fromJson(element,type);
                        mDataSet.clear();
                        mDataSet.addAll(list);
                        articleCommentAdapter.notifyDataSetChanged();
                    }
                }
            }
        }.get();

    }
}