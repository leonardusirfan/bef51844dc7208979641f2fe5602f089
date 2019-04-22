package id.net.gmedia.selby.Feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.net.gmedia.selby.Feed.FeedItem.KegiatanItemModel;
import id.net.gmedia.selby.Model.KegiatanModel;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Feed.FeedItem.BarangItemModel;
import id.net.gmedia.selby.Feed.FeedItem.FeedItemModel;
import id.net.gmedia.selby.Feed.FeedItem.GambarItemModel;
import id.net.gmedia.selby.Feed.FeedItem.LelangItemModel;
import id.net.gmedia.selby.Feed.FeedItem.TextItemModel;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFeed extends Fragment {

    //Variabel view fragment
    private Context context;
    private View v;

    //Variabel load data
    private boolean needLoad = true;
    private boolean loading = false;
    private int loadedcount = 0;

    //Variabel data
    private List<FeedItemModel> listItem = new ArrayList<>();
    private List<String> listId = new ArrayList<>();

    //Adapter & Layout Manager
    private FeedAdapter adapter;
    private LinearLayoutManager layoutManager;

    public FragmentFeed() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        if(v == null || needLoad) {
            v = inflater.inflate(R.layout.fragment_loading, container, false);
            initItem();
        }
        else {
            v = inflater.inflate(R.layout.fragment_feed, container, false);

            //Inisialisasi Recycler View
            RecyclerView rv_feed = v.findViewById(R.id.rv_feed);
            rv_feed.setItemAnimator(new DefaultItemAnimator());
            layoutManager = new LinearLayoutManager(context);
            rv_feed.setLayoutManager(layoutManager);
            adapter = new FeedAdapter(listItem);
            rv_feed.setAdapter(adapter);
            rv_feed.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if(!loading && layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1){
                        //Melakukan load more
                        loading = true;
                        loadMore();
                    }
                }
            });

            //Memuat data baru
            renewFeed();
        }

        return v;
    }

    private void renewFeed(){
        //Memperbarui data feed
        loading = false;

        JSONBuilder body = new JSONBuilder();
        body.add("jenis", "");
        body.add("id", "");
        body.add("id_penjual", "");
        body.add("start", 0);
        body.add("count", loadedcount);

        ApiVolleyManager.getInstance().addRequest(getActivity(), Constant.URL_FEED, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    listItem.clear();
                    listId.clear();

                    JSONArray feed = new JSONArray(response);
                    for(int i = 0; i < feed.length(); i++){
                        JSONObject feeditem = feed.getJSONObject(i);
                        addFeedItem(feeditem);
                    }

                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void loadMore(){
        //Memuat data berikutnya
        final int LOAD_COUNT = 5;

        JSONBuilder body = new JSONBuilder();
        body.add("jenis", "");
        body.add("id", "");
        body.add("id_penjual", "");
        body.add("start", loadedcount);
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(getActivity(), Constant.URL_FEED, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    JSONArray feed = new JSONArray(response);
                    for(int i = 0; i < feed.length(); i++){
                        JSONObject feeditem = feed.getJSONObject(i);
                        String id = feeditem.getString("id");

                        boolean not_added = true;
                        for(String id_added : listId){
                            if(id_added.equals(id)){
                                not_added = false;
                                break;
                            }
                        }

                        if(not_added){
                            addFeedItem(feeditem);
                            loadedcount += 1;
                        }
                    }

                    adapter.notifyDataSetChanged();
                    loading = false;
                }
                catch (JSONException e){
                    Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                    loading = false;
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                loading = false;
            }
        }));
    }

    private void initItem(){
        //Menginisialisasi data
        loadedcount = 0;
        final int LOAD_COUNT = 5;

        JSONBuilder body = new JSONBuilder();
        body.add("jenis", "");
        body.add("id", "");
        body.add("id_penjual", "");
        body.add("start", "0");
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(getActivity(), Constant.URL_FEED, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    needLoad = false;

                    JSONArray feed = new JSONArray(response);
                    for(int i = 0; i < feed.length(); i++){
                        JSONObject feeditem = feed.getJSONObject(i);
                        addFeedItem(feeditem);
                        loadedcount += 1;
                    }

                    resetFragment();
                }
                catch (JSONException e){
                    Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void addFeedItem(JSONObject feeditem){
        //Menambahkan item kedalam list feed
       try{
           listId.add(feeditem.getString("id"));

           ArtisModel artis = new ArtisModel(feeditem.getString("id_penjual"), feeditem.getString("penjual"),
                   feeditem.getString("image_penjual"));
           Date timestamp = Converter.stringDTToDate(feeditem.getString("timestamp"));
           String text = feeditem.getString("title");
           int jenis = feeditem.getInt("jenis");
           FeedItemModel item = null;
           switch (jenis){
               case 1:{
                   KegiatanModel kegiatan = new KegiatanModel(feeditem.getString("title"),
                           feeditem.getString("tempat"), Converter.stringDToDate(feeditem.getString("tgl")),
                           feeditem.getString("deskripsi"));
                   item = new KegiatanItemModel(artis, kegiatan, timestamp);
                   break;
               }
               case 2:{
                   List<String> listGambar = new ArrayList<>();
                   JSONArray jsonGambar = feeditem.getJSONArray("images");
                   for(int j = 0; j < jsonGambar.length(); j++){
                       listGambar.add(jsonGambar.getJSONObject(j).getString("image"));
                   }
                   item = new GambarItemModel(artis, listGambar, text, timestamp);
                   break;
               }
               case 3:{
                   item = new TextItemModel(artis, text, timestamp);
                   break;
               }
               case 4:{
                   List<BarangModel> listBarang = new ArrayList<>();
                   JSONArray jsonGambar = feeditem.getJSONArray("images");
                   for(int j = 0; j < jsonGambar.length(); j++){
                       JSONObject jsonbarang = jsonGambar.getJSONObject(j);
                       listBarang.add(new BarangModel(jsonbarang.getString("id_barang"), jsonbarang.getString("teks"),
                               jsonbarang.getString("image"), jsonbarang.getString("jenis").equals("1")?
                               Constant.BARANG_PRELOVED:Constant.BARANG_MERCHANDISE));
                   }
                   item = new BarangItemModel(artis, listBarang, timestamp);
                   break;
               }
               case 5:{
                   JSONObject jsonlelang = feeditem.getJSONArray("images").getJSONObject(0);
                   LelangModel lelang = new LelangModel(jsonlelang.getString("id_lelang"),
                           jsonlelang.getString("teks"), jsonlelang.getString("image"));
                   item = new LelangItemModel(artis, lelang, timestamp);
                   break;
               }
           }

           listItem.add(item);
       }
       catch (JSONException e){
           Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
           Log.e(Constant.TAG, e.toString());
       }
    }

    public void resetFragment(){
        //mereset tampilan fragment
        if(getActivity() != null){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(this);
            ft.attach(this);
            ft.commit();
        }
    }
}
