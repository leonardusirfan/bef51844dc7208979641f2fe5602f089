package id.net.gmedia.selby.Home.Feed;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.net.gmedia.selby.Home.Feed.FeedItem.KegiatanItemModel;
import id.net.gmedia.selby.Model.KegiatanModel;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Home.Feed.FeedItem.BarangItemModel;
import id.net.gmedia.selby.Home.Feed.FeedItem.FeedItemModel;
import id.net.gmedia.selby.Home.Feed.FeedItem.GambarItemModel;
import id.net.gmedia.selby.Home.Feed.FeedItem.LelangItemModel;
import id.net.gmedia.selby.Home.Feed.FeedItem.TextItemModel;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFeed extends Fragment {

    private View v;
    private boolean needLoad = true;
    private boolean loading = false;

    private int loadedcount = 0;

    //private boolean needLoad = true;
    private List<FeedItemModel> listItem;
    private List<String> listId;

    private FeedAdapter adapter;
    private LinearLayoutManager layoutManager;

    public FragmentFeed() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(v == null || needLoad) {
            v = inflater.inflate(R.layout.fragment_loading, container, false);
            initItem();
        }
        else {
            v = inflater.inflate(R.layout.fragment_feed, container, false);

            //bar_loading = v.findViewById(R.id.bar_loading);
            RecyclerView rv_feed = v.findViewById(R.id.rv_feed);
            rv_feed.setItemAnimator(new DefaultItemAnimator());

            layoutManager = new LinearLayoutManager(getContext());
            rv_feed.setLayoutManager(layoutManager);
            adapter = new FeedAdapter(listItem);
            rv_feed.setAdapter(adapter);
            rv_feed.addOnScrollListener(new RecyclerView.OnScrollListener() {
                //Jika perlu ditambah init ulang untuk memuat (load) feed terbaru
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if(!loading && layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1){
                        loading = true;
                        loadMore();
                    }
                }
            });
        }

        return v;
    }

    private void loadMore(){
        final int LOAD_COUNT = 5;
        try{
            JSONObject body = new JSONObject();
            body.put("jenis", "");
            body.put("id", "");
            body.put("id_penjual", "");
            body.put("start", loadedcount);
            body.put("count", LOAD_COUNT);

            ApiVolleyManager.getInstance().addRequest(getActivity(), Constant.URL_FEED, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        loading = false;
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            JSONArray feed = jsonresult.getJSONArray("response");
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
                                }
                            }

                            loadedcount += LOAD_COUNT;
                            adapter.notifyDataSetChanged();
                        }
                        else if(status != 404){
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(getContext(), R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Feed", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Feed", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(getContext(), R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Feed", e.toString());
        }
    }

    private void initItem(){
        final int LOAD_COUNT = 5;
        try{
            JSONObject body = new JSONObject();
            body.put("jenis", "");
            body.put("id", "");
            body.put("id_penjual", "");
            body.put("start", "0");
            body.put("count", loadedcount + LOAD_COUNT);

            ApiVolleyManager.getInstance().addRequest(getActivity(), Constant.URL_FEED, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200 || status == 404){
                            needLoad = false;

                            listItem = new ArrayList<>();
                            listId = new ArrayList<>();

                            JSONArray feed = jsonresult.getJSONArray("response");
                            for(int i = 0; i < feed.length(); i++){
                                JSONObject feeditem = feed.getJSONObject(i);
                                addFeedItem(feeditem);
                            }

                            loadedcount += LOAD_COUNT;
                            resetFragment();
                        }
                        else{
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(getContext(), R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Feed", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Feed", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(getContext(), R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Feed", e.toString());
        }
    }

    private void addFeedItem(JSONObject feeditem){
       try{
           listId.add(feeditem.getString("id"));

           ArtisModel artis = new ArtisModel(feeditem.getString("id_penjual"), feeditem.getString("penjual"), feeditem.getString("image_penjual"));
           Date timestamp = Converter.stringDTTToDate(feeditem.getString("timestamp"));
           String text = feeditem.getString("title");
           int jenis = feeditem.getInt("jenis");
           FeedItemModel item = null;
           switch (jenis){
               case 1:{
                   KegiatanModel kegiatan = new KegiatanModel(feeditem.getString("title"), feeditem.getString("tempat"), Converter.stringDToDate(feeditem.getString("tgl")), feeditem.getString("deskripsi"));
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
                       listBarang.add(new BarangModel(jsonbarang.getString("id_barang"), jsonbarang.getString("teks"), jsonbarang.getString("image")));
                   }
                   item = new BarangItemModel(artis, listBarang, timestamp);
                   break;
               }
               case 5:{
                   JSONObject jsonlelang = feeditem.getJSONArray("images").getJSONObject(0);
                   LelangModel lelang = new LelangModel(jsonlelang.getString("id_lelang"), jsonlelang.getString("teks"), jsonlelang.getString("image"));
                   item = new LelangItemModel(artis, lelang, timestamp);
                   break;
               }
           }

           listItem.add(item);
       }
       catch (JSONException e){
           Toast.makeText(getContext(), R.string.error_json, Toast.LENGTH_SHORT).show();
           Log.e("Feed", e.toString());
       }
    }

    public void resetFragment(){
        if(getActivity() != null){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(this);
            ft.attach(this);
            ft.commit();

            ((HomeActivity)getActivity()).appbar.setExpanded(true);
        }
    }
}
