package com.example.annation.presenter;


import com.example.annation.http.BaseNetWork;
import com.example.annation.http.HttpResponse;
import com.example.annation.status.FavEntity;
import com.example.annation.uri.Contants;
import com.example.annation.uri.ParameterKeySet;
import com.example.annation.utils.LogUtils;
import com.example.annation.utils.PreferenceUtils;
import com.example.annation.view.FavoriteView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sina.weibo.sdk.net.WeiboParameters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavPresenterImp implements BasePresenter {
    private FavoriteView mView;
    private PreferenceUtils mSPUtils;
    private WeiboParameters mParameters;
    private int page = 1;
    private List<FavEntity> mEntityList;

    public FavPresenterImp(FavoriteView view) {
        mView = view;
        mSPUtils = PreferenceUtils.getInstance(mView.getActivity());
        mParameters = new WeiboParameters(Contants.APP_KEY);
        mEntityList = new ArrayList<>();
    }

    @Override
    public void loadData(boolean showLoading) {
        page = 1;
        loadData(false, showLoading);
    }

    @Override
    public void loadMore(boolean showLoading) {
        page++;
        loadData(true, showLoading);

    }

    private void loadData(final boolean loadMore, boolean showLoading) {

        new BaseNetWork(mView, Contants.API.FAVOURITES, showLoading) {
            public WeiboParameters onPrepares() {
                mParameters.put(ParameterKeySet.AUTH_ACCESS_TOKEN, mSPUtils
                        .getToken().getToken());
                mParameters.put(ParameterKeySet.PAGE, page);
                mParameters.put(ParameterKeySet.COUNT, 10);
                return mParameters;
            }


            public void onFinish(HttpResponse response, boolean success) {
                if (success) {
                    LogUtils.e(response.response);
                    Type type = new TypeToken<ArrayList<FavEntity>>() {
                    }.getType();
                    List<FavEntity> list = new Gson().fromJson(response.response, type);
                    if (!loadMore) {
                        mEntityList.clear();
                    }
                    mEntityList.addAll(list);
                    mView.onSuccess(mEntityList);
                }
            }
        }.get();

    }
}
