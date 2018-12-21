package id.net.gmedia.selby.Artis.Fragment;


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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Artis.Adapter.GaleriAdapter;
import id.net.gmedia.selby.Artis.EventActivity;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGaleri extends Fragment {

    //Variabel penampung id artis
    private String id = "";
    //Variabel konteks dimana fragment berjalan
    private Activity activity;
    //Variabel tampilan
    private View v;
    //Variabel flag pengatur load tampilan & data
    private boolean needLoad = true;

    //List foto galeri
    public List<String> listImage = new ArrayList<>();

    public FragmentGaleri() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();
        if(v == null || needLoad){
            //jika fragment pertama kali dibutuhkan atau fragment membutuhkan load data/tampilan
            v = inflater.inflate(R.layout.fragment_loading, container, false);
            if(getArguments() != null){
                id = getArguments().getString("id");
            }
            initGaleri();
        }
        else{
            //Jika list favorit kosong
            if(listImage.size() == 0){

                v = inflater.inflate(R.layout.fragment_kosong, container, false);

                //Inisialisasi UI
                TextView txt_kosong = v.findViewById(R.id.txt_kosong);
                v.findViewById(R.id.img_kosong).setVisibility(View.GONE);
                txt_kosong.setText(R.string.kosong_artis_galeri);
            }
            //Jika list favorit terisi
            else{
                v = inflater.inflate(R.layout.fragment_artis_galeri, container, false);

                //inisialisasi UI
                RecyclerView rv_event = v.findViewById(R.id.rv_event);
                GaleriAdapter adapter = new GaleriAdapter(listImage);
                rv_event.setLayoutManager(new GridLayoutManager(activity, 3, LinearLayoutManager.VERTICAL, false));
                rv_event.setItemAnimator(new DefaultItemAnimator());
                rv_event.setAdapter(adapter);
            }
        }
        return v;
    }

    private void initGaleri(){
        //Inisialisasi Galeri dari Web Service
        try{
            JSONObject body = new JSONObject();
            body.put("id", "");
            body.put("id_penjual", id);

            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_GALLERY, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200 || status == 404){
                            needLoad = false;
                            JSONArray images = jsonresult.getJSONArray("response");
                            for(int i = 0; i < images.length(); i++){
                                listImage.add(images.getJSONObject(i).getString("image"));
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
        }catch (JSONException e){
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
