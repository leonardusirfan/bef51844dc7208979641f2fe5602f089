package id.net.gmedia.selby.Favorit;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Home.Adapter.BarangAdapter;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavorit extends Fragment {

    private Activity activity;
    private View v;
    private boolean needLoad = true;
    private List<BarangModel> listItem = new ArrayList<>();

    public FragmentFavorit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        if(v == null || needLoad){
            v = inflater.inflate(R.layout.fragment_loading, container, false);
            initFavorit();
        }
        else {
            if (listItem.size() == 0) {
                //Jika list favorit kosong
                v = inflater.inflate(R.layout.fragment_kosong, container, false);

                TextView txt_kosong = v.findViewById(R.id.txt_kosong);
                txt_kosong.setText(R.string.kosong_favorit);
                ((ImageView)v.findViewById(R.id.img_kosong)).setImageResource(R.drawable.wishlistkosong);
            } else {
                v = inflater.inflate(R.layout.fragment_favorit, container, false);

                RecyclerView rv_favorit = v.findViewById(R.id.rv_favorit);
                BarangAdapter adapter = new BarangAdapter(activity, listItem);
                rv_favorit.setLayoutManager(new GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false));
                rv_favorit.setItemAnimator(new DefaultItemAnimator());
                rv_favorit.setAdapter(adapter);
            }
        }
        return v;
    }

    private void initFavorit(){
        JSONBuilder body = new JSONBuilder();
        body.add("keyword", "");
        body.add("start", 0);
        body.add("count", 0);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_FAVORIT, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    needLoad = false;
                    JSONArray listfavorit = new JSONArray(response);
                    for(int i = 0; i < listfavorit.length(); i++){
                        JSONObject favorit = listfavorit.getJSONObject(i);
                        listItem.add(new BarangModel(favorit.getString("id_barang"),
                                favorit.getString("barang"), favorit.getString("image"),
                                favorit.getDouble("harga"), new ArtisModel(favorit.getString("penjual"),
                                favorit.getString("foto"), (float) favorit.getDouble("rating_penjual"))));
                    }

                    resetFragment();
                }
                catch (JSONException e){
                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.toString());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void resetFragment(){
        if(getActivity() != null){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(this);
            ft.attach(this);
            ft.commit();
        }
    }
}
