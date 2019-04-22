package id.net.gmedia.selby.Home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.ArtisAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class SearchActivity extends AppCompatActivity {

    private String search = "";

    private List<ArtisModel> listArtis = new ArrayList<>();
    private ArtisAdapter artisAdapter;
    private LoadMoreScrollListener loadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        //Inisialisasi toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_artis = findViewById(R.id.rv_artis);
        artisAdapter = new ArtisAdapter(this, listArtis);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3,
                LinearLayoutManager.VERTICAL, false);
        rv_artis.setLayoutManager(layoutManager);
        rv_artis.setAdapter(artisAdapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadArtis(false, search);
            }
        };
        rv_artis.addOnScrollListener(loadManager);

        ((EditText)findViewById(R.id.txt_search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search = s.toString();
                loadArtis(true, search);
            }
        });

        //Animasi + Transisi dari activity sebelumnya
        Transition enterTransition = getWindow().getSharedElementEnterTransition();
        enterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                loadArtis(true, search);
                transition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });
    }

    private void loadArtis(final boolean init, String keyword){
        final int LOAD_COUNT = 12;

        if(init){
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("id", "");
        body.add("keyword", keyword);
        body.add("pekerjaan", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ARTIS, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onEmpty(String response) {
                if(init){
                    listArtis.clear();
                }

                artisAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(String response) {
                try{
                    if(init){
                        listArtis.clear();
                    }

                    JSONObject jsonresult = new JSONObject(response);
                    JSONArray array = jsonresult.getJSONArray("pelapak");
                    for(int i = 0; i < array.length(); i++){
                        JSONObject artis = array.getJSONObject(i);
                        listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"),
                                artis.getString("image"), "Amerika Serikat","2 juni 1995",
                                167, artis.getString("deskripsi")));
                    }
                    loadManager.finishLoad(array.length());
                    artisAdapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(SearchActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                    loadManager.failedLoad();
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
                loadManager.failedLoad();
            }
        }));
    }

    @Override
    public void onBackPressed() {
        //animasi back shared element transition
        findViewById(R.id.layout_search).setBackgroundResource(R.drawable.style_fullrounded_light_gray_rectangle);
        super.onBackPressed();
    }
}
