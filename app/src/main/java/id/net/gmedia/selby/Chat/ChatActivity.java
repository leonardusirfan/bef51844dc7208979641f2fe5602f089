package id.net.gmedia.selby.Chat;

import android.support.design.widget.TabLayout;
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

import id.net.gmedia.selby.Model.UserModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class ChatActivity extends AppCompatActivity {

    private List<ChatModel> listChat = new ArrayList<>();
    private List<ChatModel> listChatBelumTerbaca = new ArrayList<>();

    private RecyclerView rv_chat;
    private LoadMoreScrollListener loadManager;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Pesan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Semua Pesan"));
        tabLayout.addTab(tabLayout.newTab().setText("Belum Terbaca"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchChat(tab.getPosition()==0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        rv_chat = findViewById(R.id.rv_chat);
        rv_chat.setLayoutManager(new LinearLayoutManager(this));
        rv_chat.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatAdapter(this, listChat);
        rv_chat.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadChat(false);
            }
        };
        rv_chat.addOnScrollListener(loadManager);
    }

    private void switchChat(boolean semua){
        //fungsi untuk mengubah fragment
        if(semua){
            rv_chat.setAdapter(new ChatAdapter(this, listChat));
        }
        else{
            rv_chat.setAdapter(new ChatAdapter(this, listChatBelumTerbaca));
        }
    }

    private void loadChat(final boolean init){
        final int LOAD_COUNT = 10;
        if(init){
            loadManager.initLoad();
            AppLoading.getInstance().showLoading(this);
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CHAT, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listChat.clear();
                            listChatBelumTerbaca.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listChat.clear();
                                listChatBelumTerbaca.clear();
                            }

                            JSONArray list_chat = new JSONArray(result);
                            for(int i = 0; i < list_chat.length(); i++){
                                JSONObject chat = list_chat.getJSONObject(i);
                                ChatModel c = new ChatModel("", chat.getString("lastmessage"),
                                        chat.getString("lastdate"), new UserModel(chat.getString("from"),
                                        chat.getString("profile_name"), chat.getString("foto")),
                                        chat.getInt("baru"));
                                listChat.add(c);
                                if(c.getBaru() > 0){
                                    listChatBelumTerbaca.add(c);
                                }
                            }

                            loadManager.finishLoad(list_chat.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(ChatActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadManager.failedLoad();
                    }
                }));
    }

    @Override
    protected void onResume() {
        loadChat(true);
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
