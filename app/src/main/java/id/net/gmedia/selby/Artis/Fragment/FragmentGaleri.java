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

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import id.net.gmedia.selby.Artis.Adapter.GaleriAdapter;
import id.net.gmedia.selby.Artis.EventActivity;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.R;

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
    private List<SimpleObjectModel> listHeader = new ArrayList<>();
    private LinkedHashMap<SimpleObjectModel, List<String>> listGaleri = new LinkedHashMap<>();

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
            if(listHeader.size() == 0){

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
                GaleriAdapter adapter = new GaleriAdapter(activity, listHeader, listGaleri);
                rv_event.setLayoutManager(new LinearLayoutManager(activity));
                rv_event.setItemAnimator(new DefaultItemAnimator());
                rv_event.setAdapter(adapter);
            }
        }
        return v;
    }

    private void initGaleri(){
        //Inisialisasi Galeri dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("id", "");
        body.add("id_penjual", id);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_GALLERY, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    needLoad = false;

                    JSONArray images = new JSONArray(response);
                    List<String> listImage = new ArrayList<>();
                    for(int i = 0; i < images.length(); i++){
                        listImage.add(images.getJSONObject(i).getString("image"));
                    }

                    SimpleObjectModel header = new SimpleObjectModel("Album 1", "29 Januari 2019");
                    listHeader.add(header);
                    listGaleri.put(header, listImage);
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
