package id.net.gmedia.selby;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Util.Constant;

public class HotNewsActivity extends AppCompatActivity {

    private HotNewsAdapter adapter;
    private LoadMoreScrollListener loadManager;
    private List<HotNewsModel> listNews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_news);

        //Inisialisasi toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Hot News");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_news = findViewById(R.id.rv_news);
        rv_news.setItemAnimator(new DefaultItemAnimator());
        rv_news.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HotNewsAdapter(this, listNews);
        rv_news.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadNews(false);
            }
        };
        rv_news.addOnScrollListener(loadManager);

        loadNews(true);
    }

    private void loadNews(final boolean init){
        if(init){
            AppLoading.getInstance().showLoading(this);
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("keyword", "");
        body.add("start", loadManager.getLoaded());
        body.add("count", 10);
        body.add("id", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_HOT_NEWS,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listNews.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listNews.clear();
                            }

                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject news = response.getJSONObject(i);
                                listNews.add(new HotNewsModel(news.getString("id"),
                                        news.getString("path") + news.getString("image"), news.getString("judul"),
                                        news.getString("teks"), news.getString("waktu")));
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(response.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(HotNewsActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(HotNewsActivity.this, message, Toast.LENGTH_SHORT).show();

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
