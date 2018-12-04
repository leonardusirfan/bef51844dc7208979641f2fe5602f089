package id.net.gmedia.selby.Home.Keranjang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Converter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentKeranjang extends Fragment {

    private View v;
    private boolean needLoad = true;

    //Variabel UI
    private TextView txt_subtotal;
    private Button btn_bayar;
    private TextView txt_hapus;
    public CheckBox cb_keranjang;
    private ProgressBar bar_loading;

    private List<BaseListItem> listItem = new ArrayList<>();

    //Adapter item keranjang
    public KeranjangAdapter keranjangAdapter;

    public FragmentKeranjang() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(v == null || needLoad){
            v = inflater.inflate(R.layout.fragment_loading, container, false);
            initKeranjang();
        }
        else{
            if(listItem.size() == 0){
                //Jika list favorit kosong
                v = inflater.inflate(R.layout.fragment_kosong, container, false);

                TextView txt_kosong = v.findViewById(R.id.txt_kosong);
                txt_kosong.setText(R.string.keranjang_kosong);
            }
            else{
                v = inflater.inflate(R.layout.fragment_keranjang, container, false);

                //Inisialisasi UI
                cb_keranjang = v.findViewById(R.id.cb_keranjang);
                txt_hapus = v.findViewById(R.id.txt_hapus);
                btn_bayar = v.findViewById(R.id.btn_bayar);
                txt_subtotal = v.findViewById(R.id.txt_subtotal);
                bar_loading = v.findViewById(R.id.bar_loading);
                RecyclerView rv_keranjang = v.findViewById(R.id.rv_keranjang);

                //inisialisasi keranjang
                rv_keranjang.setItemAnimator(new DefaultItemAnimator());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
                rv_keranjang.setLayoutManager(layoutManager);
                keranjangAdapter = new KeranjangAdapter(FragmentKeranjang.this, listItem);
                rv_keranjang.setAdapter(keranjangAdapter);

                txt_subtotal.setText(Converter.doubleToRupiah(0));
                btn_bayar.setEnabled(false);

                txt_hapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Hapus Item");
                        builder.setMessage("Yakin ingin menghapus item dari keranjang?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                List<String> listId = new ArrayList<>();
                                for(BaseListItem i : listItem){
                                    if(i.getType() == BaseListItem.TYPE_CONTENT){
                                        if(((ContentListItem)i).isSelected()){
                                            listId.add(((ContentListItem)i).getItem().getId());
                                        }
                                    }
                                }

                                try{
                                    JSONArray list = new JSONArray(listId);
                                    JSONObject body = new JSONObject();
                                    body.put("id_keranjang", list);
                                    System.out.println("List ID : " + list);

                                    ApiVolleyManager.getInstance().addRequest(getContext(), Constant.URL_HAPUS_KERANJANG, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body, new ApiVolleyManager.RequestCallback() {
                                        @Override
                                        public void onSuccess(String result) {
                                            try{
                                                JSONObject json = new JSONObject(result);
                                                System.out.println(result);
                                                int status = json.getJSONObject("metadata").getInt("status");
                                                String message = json.getJSONObject("metadata").getString("message");

                                                if(status == 200){
                                                    needLoad = true;
                                                    resetFragment();
                                                }
                                                else{
                                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                                    bar_loading.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                            catch (JSONException e){
                                                Toast.makeText(getContext(), R.string.error_json, Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                                bar_loading.setVisibility(View.INVISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onError(String result) {
                                            Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                                            bar_loading.setVisibility(View.INVISIBLE);
                                        }
                                    });

                                }
                                catch (JSONException e){
                                    Toast.makeText(getContext(), R.string.error_json, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                    bar_loading.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();
                    }
                });
                btn_bayar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //bayar item terpilih
                    }
                });

                //Jika checkbox pilih semua diklik, ubah semua checkbox didalam adapter
                cb_keranjang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(cb_keranjang.isChecked()){
                            for(BaseListItem l : listItem){
                                if(l.getType() == BaseListItem.TYPE_HEADER){
                                    ((HeaderListItem)l).setSelected(true);
                                }
                                else if(l.getType() == BaseListItem.TYPE_CONTENT){
                                    ((ContentListItem)l).setSelected(true);
                                }
                            }
                        }
                        else{
                            for(BaseListItem l : listItem){
                                if(l.getType() == BaseListItem.TYPE_HEADER){
                                    ((HeaderListItem)l).setSelected(false);
                                }
                                else if(l.getType() == BaseListItem.TYPE_CONTENT){
                                    ((ContentListItem)l).setSelected(false);
                                }
                            }
                        }

                        updateView();
                        keranjangAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        return v;
    }

    public void updateView(){
        double subtotal = 0;
        for(BaseListItem item : listItem){
            if(item.getType() == BaseListItem.TYPE_CONTENT){
                if(((ContentListItem)item).isSelected()){
                    subtotal += ((ContentListItem) item).getJumlah() * ((ContentListItem) item).getItem().getHarga();
                }
            }
        }
        txt_subtotal.setText(Converter.doubleToRupiah(subtotal));

        if(subtotal == 0){
            txt_hapus.setVisibility(View.INVISIBLE);
            btn_bayar.setBackgroundResource(R.color.dark_grey);
            btn_bayar.setEnabled(false);
        }
        else{
            txt_hapus.setVisibility(View.VISIBLE);
            btn_bayar.setBackgroundResource(R.color.orange);
            btn_bayar.setEnabled(true);
        }
    }

    private void initKeranjang(){

        ApiVolleyManager.getInstance().addRequest(getActivity(), Constant.URL_KERANJANG, ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), new ApiVolleyManager.RequestCallback() {
            @Override
            public void onSuccess(String result) {
                try{
                    System.out.println(result);
                    JSONObject json = new JSONObject(result);
                    int status = json.getJSONObject("metadata").getInt("status");
                    String message = json.getJSONObject("metadata").getString("message");

                    if(status == 200 || status == 404){
                        needLoad = false;
                        //listItem.clear();
                        listItem = new ArrayList<>();

                        Map<ArtisModel, List<BarangModel>> listKeranjang = new LinkedHashMap<>();
                        JSONArray response = json.getJSONArray("response");
                        for(int i = 0; i < response.length(); i++){
                            ArtisModel pelapak = new ArtisModel(response.getJSONObject(i).getString("penjual"));
                            JSONArray listBarang = response.getJSONObject(i).getJSONArray("barang");
                            List<BarangModel> barang = new ArrayList<>();

                            for(int j = 0; j < listBarang.length(); j++){
                                BarangModel barangbelanja = new BarangModel(listBarang.getJSONObject(j).getString("id"),listBarang.getJSONObject(j).getString("barang"), listBarang.getJSONObject(j).getString("image"),listBarang.getJSONObject(j).getDouble("harga"));
                                barangbelanja.setJumlah(listBarang.getJSONObject(j).getInt("jumlah"));
                                barang.add(barangbelanja);
                            }

                            listKeranjang.put(pelapak, barang);
                        }

                        for (ArtisModel p : listKeranjang.keySet()) {
                            HeaderListItem header = new HeaderListItem(p);
                            listItem.add(header);

                            for (BarangModel i : Objects.requireNonNull(listKeranjang.get(p))) {
                                ContentListItem item = new ContentListItem(i, i.getJumlah());
                                listItem.add(item);
                            }

                            //Menambah Divider
                            listItem.add(new FooterListItem());
                        }

                        resetFragment();
                    }
                    else{
                        showError(message);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                    showError(getString(R.string.error_json));
                }
            }

            @Override
            public void onError(String result) {
                showError(getString(R.string.error_database));
            }
        });
    }

    private void showError(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        if(bar_loading != null){
            bar_loading.setVisibility(View.INVISIBLE);
        }
    }

    public void setLoad(){
        needLoad = true;
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
