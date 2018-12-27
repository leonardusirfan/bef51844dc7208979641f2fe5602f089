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
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.ArtisAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.JSONBuilder;

public class SearchActivity extends AppCompatActivity {

    private ProgressBar pb_artis;

    private List<ArtisModel> listArtis = new ArrayList<>();
    private ArtisAdapter artisAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        pb_artis = findViewById(R.id.pb_artis);

        RecyclerView rv_artis = findViewById(R.id.rv_artis);
        artisAdapter = new ArtisAdapter(this, listArtis);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        rv_artis.setLayoutManager(mLayoutManager);
        rv_artis.setAdapter(artisAdapter);

        ((EditText)findViewById(R.id.txt_search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loadArtis(s.toString());
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
                loadArtis("");
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

    private void loadArtis(String keyword){
        pb_artis.setVisibility(View.VISIBLE);

        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", 0);
        body.add("id", "");
        body.add("keyword", keyword);
        body.add("pekerjaan", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ARTIS, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
            @Override
            public void onEmpty(String response) {
                listArtis.clear();
                artisAdapter.notifyDataSetChanged();
                pb_artis.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(String response) {
                try{
                    listArtis.clear();
                    JSONObject jsonresult = new JSONObject(response);
                    JSONArray array = jsonresult.getJSONArray("pelapak");
                    for(int i = 0; i < array.length(); i++){
                        JSONObject artis = array.getJSONObject(i);
                        listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"), artis.getString("image"), "Amerika Serikat","2 juni 1995", 167, artis.getString("deskripsi")));
                    }
                    artisAdapter.notifyDataSetChanged();
                    pb_artis.setVisibility(View.GONE);
                }
                catch (JSONException e){
                    Toast.makeText(SearchActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Search", e.getMessage());
                    pb_artis.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
                pb_artis.setVisibility(View.GONE);
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
