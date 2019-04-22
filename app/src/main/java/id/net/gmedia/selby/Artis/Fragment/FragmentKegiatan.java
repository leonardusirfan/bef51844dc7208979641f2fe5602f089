package id.net.gmedia.selby.Artis.Fragment;


import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Artis.Adapter.KegiatanAdapter;
import id.net.gmedia.selby.Artis.EventActivity;
import id.net.gmedia.selby.Model.KegiatanModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentKegiatan extends Fragment {

    private Activity activity;
    private View v;
    private boolean needLoad = true;

    private String id = "";

    private List<KegiatanModel> listKegiatan = new ArrayList<>();

    public FragmentKegiatan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        if(v == null || needLoad){
            //jika fragment pertama kali dibutuhkan atau fragment membutuhkan load data/tampilan
            v = inflater.inflate(R.layout.fragment_loading, container, false);
            if(getArguments() != null){
                id = getArguments().getString("id");
            }

            initKegiatan();
        }
        else{
            //Jika list favorit kosong
            if(listKegiatan.size() == 0){

                v = inflater.inflate(R.layout.fragment_kosong, container, false);

                //Inisialisasi UI
                TextView txt_kosong = v.findViewById(R.id.txt_kosong);
                v.findViewById(R.id.img_kosong).setVisibility(View.GONE);
                txt_kosong.setText(R.string.kosong_artis_kegiatan);
            }
            //Jika list favorit terisi
            else{
                v = inflater.inflate(R.layout.fragment_artis_kegiatan, container, false);

                //inisialisasi UI
                RecyclerView rv_event = v.findViewById(R.id.rv_event);
                KegiatanAdapter adapter = new KegiatanAdapter(listKegiatan);
                rv_event.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                rv_event.setItemAnimator(new DefaultItemAnimator());
                rv_event.setAdapter(adapter);
            }
        }
        return v;
    }

    private void initKegiatan(){
        JSONBuilder body = new JSONBuilder();
        body.add("id", "");
        body.add("id_penjual", id);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_KEGIATAN, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    needLoad = false;
                    JSONArray kegiatan = new JSONArray(response);
                    for(int i = 0; i < kegiatan.length(); i++){
                        listKegiatan.add(new KegiatanModel(kegiatan.getJSONObject(i).getString("judul"),
                                kegiatan.getJSONObject(i).getString("tempat"),
                                Converter.stringDToDate(kegiatan.getJSONObject(i).getString("tgl")),
                                kegiatan.getJSONObject(i).getString("deskripsi")));
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
        //me-reset tampilan fragment
        if(getActivity() != null){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(this);
            ft.attach(this);
            ft.commit();

            ((EventActivity)getActivity()).appbar.setExpanded(true);
        }
    }
}
