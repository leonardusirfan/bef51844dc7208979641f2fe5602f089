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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Artis.Adapter.KegiatanAdapter;
import id.net.gmedia.selby.Artis.EventActivity;
import id.net.gmedia.selby.Model.KegiatanModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Util.Converter;


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
        try{
            JSONObject body = new JSONObject();
            body.put("id", "");
            body.put("id_penjual", id);

            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_KEGIATAN, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200 || status == 404){
                            needLoad = false;
                            JSONArray kegiatan = jsonresult.getJSONArray("response");
                            for(int i = 0; i < kegiatan.length(); i++){
                                listKegiatan.add(new KegiatanModel(kegiatan.getJSONObject(i).getString("judul"), kegiatan.getJSONObject(i).getString("tempat"), Converter.stringDToDate(kegiatan.getJSONObject(i).getString("tgl")), kegiatan.getJSONObject(i).getString("deskripsi")));
                            }

                            resetFragment();
                        }
                        else{
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("Galeri", e.toString());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("Galeri", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("Galeri", e.toString());
        }
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
