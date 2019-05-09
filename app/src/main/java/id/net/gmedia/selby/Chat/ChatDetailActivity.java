package id.net.gmedia.selby.Chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
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

import id.net.gmedia.selby.Model.UserModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class ChatDetailActivity extends AppCompatActivity {

    private UserModel user;
    private String sender;

    private List<ChatDetailModel> listChat = new ArrayList<>();
    private LoadMoreScrollListener loadManager;
    private ChatDetailAdapter adapter;
    private LinearLayoutManager layoutManager;

    private EditText txt_chat;
    private ProgressBar pb_kirim;
    private ImageView img_kirim;

    /*private BroadcastReceiver updates_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            loadChat(true);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        if(getIntent().hasExtra(Constant.EXTRA_USER)){
            Gson gson = new Gson();
            user = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_USER), UserModel.class);
        }

        if(getIntent().hasExtra(Constant.EXTRA_CHAT_FROM)){
            sender = getIntent().getStringExtra(Constant.EXTRA_CHAT_FROM);
        }

        if(getSupportActionBar() != null){
            if(user != null){
                getSupportActionBar().setTitle(user.getNama());
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txt_chat = findViewById(R.id.txt_chat);
        pb_kirim = findViewById(R.id.pb_kirim);
        img_kirim = findViewById(R.id.img_kirim);
        RecyclerView rv_chat = findViewById(R.id.rv_chat);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rv_chat.setLayoutManager(layoutManager);

        rv_chat.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatDetailAdapter(listChat);
        rv_chat.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadChat(false);
            }
        };
        loadManager.setReverse(true);
        rv_chat.addOnScrollListener(loadManager);

        img_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat = txt_chat.getText().toString();

                if(!chat.equals("")){
                    kirimChat(chat);
                    txt_chat.setText("");
                }
            }
        });

        /*LocalBroadcastManager.getInstance(this).registerReceiver(updates_receiver,
                new IntentFilter(AppFirebaseMessagingService.INFO_UPDATE_FILTER));*/

        loadChat(true);
    }

    private void kirimChat(final String chat){
        img_kirim.setVisibility(View.INVISIBLE);
        pb_kirim.setVisibility(View.VISIBLE);

        JSONBuilder body = new JSONBuilder();
        body.add("to", sender);
        body.add("message", chat);

        System.out.println(body.create());
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CHAT_TAMBAH, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ChatDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                        img_kirim.setVisibility(View.VISIBLE);
                        pb_kirim.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onSuccess(String result) {
                        listChat.add(0, new ChatDetailModel("", chat, true));
                        adapter.notifyItemInserted(0);
                        layoutManager.scrollToPosition(0);

                        img_kirim.setVisibility(View.VISIBLE);
                        pb_kirim.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ChatDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                        img_kirim.setVisibility(View.VISIBLE);
                        pb_kirim.setVisibility(View.INVISIBLE);
                    }
                }));
    }

    private void loadChat(final boolean init){
        if(init){
            loadManager.initLoad();
            AppLoading.getInstance().showLoading(this);
        }

        JSONBuilder body = new JSONBuilder();
        body.add("from", sender);
        body.add("start", loadManager.getLoaded());
        body.add("count", 20);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CHAT_DETAIL, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listChat.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listChat.clear();
                            }

                            int previous_loaded = loadManager.getLoaded();
                            JSONArray list_chat = new JSONArray(result);
                            for(int i = 0; i < list_chat.length(); i++){
                                JSONObject chat = list_chat.getJSONObject(i);
                                listChat.add(previous_loaded, new ChatDetailModel("", chat.getString("message"),
                                        chat.getString("from").equals(FirebaseAuth.getInstance().getUid())));
                            }

                            if(init){
                                layoutManager.scrollToPosition(0);
                            }
                            loadManager.finishLoad(list_chat.length());
                            adapter.notifyItemRangeInserted(previous_loaded + 1, list_chat.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(ChatDetailActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ChatDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(updates_receiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
