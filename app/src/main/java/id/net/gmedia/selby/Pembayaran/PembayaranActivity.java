package id.net.gmedia.selby.Pembayaran;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.Constants;
import com.midtrans.sdk.corekit.core.LocalDataHandler;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.models.BillInfoModel;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.UserAddress;
import com.midtrans.sdk.corekit.models.UserDetail;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Model.AlamatModel;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangJualModel;
import id.net.gmedia.selby.Model.OngkirModel;
import id.net.gmedia.selby.Model.UserModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class PembayaranActivity extends AppCompatActivity implements TransactionFinishedCallback {

    private final int KODE_TAMBAH_ALAMAT = 910;
    private final int KODE_GANTI_ALAMAT = 911;
    final int KODE_GANTI_ONGKIR = 912;

    private static final String MERCHANT_BASE_CHECKOUT_URL = "http://gmedia.bz/selbi/api/transaksi/prepayment/";
    //SANDBOX
    private static final String MERCHANT_CLIENT_KEY = "SB-Mid-client-vFml_BHcmKOidw7B";
    //PRODUCTION
    //public static final String MERCHANT_CLIENT_KEY = "Mid-client-Ewtzxg4RgRfcxMHE";

    private double harga_total_barang = 0;
    private double harga_total_ongkir = 0;

    private UserModel user;
    public AlamatModel alamat_dipilih;
    private boolean alamat_kosong = false;

    //Variabel UI
    private TextView txt_alamat, txt_kota, txt_kodepos, txt_total;

    private LinkedHashMap<String, List<BarangJualModel>> listBarangBeli = new LinkedHashMap<>();
    private List<ArtisModel> listHeader = new ArrayList<>();
    private LinkedHashMap<String, OngkirModel> listOngkir = new LinkedHashMap<>();

    private PembayaranAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Transaksi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi Variabel
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_kota = findViewById(R.id.txt_kota);
        txt_kodepos = findViewById(R.id.txt_kodepos);
        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        txt_total = findViewById(R.id.txt_total);

        //Inisialisasi transaksi Midtrans
        initTransaksi();

        //Inisialisasi list barang
        if(getIntent().hasExtra(Constant.EXTRA_LIST_BARANG) && getIntent().hasExtra(Constant.EXTRA_LIST_PENJUAL)){
            Gson gson = new Gson();

            listBarangBeli = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_LIST_BARANG),
                    new TypeToken<LinkedHashMap<String, List<BarangJualModel>>>(){}.getType());
            listHeader = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_LIST_PENJUAL),
                    new TypeToken<List<ArtisModel>>(){}.getType());

            //Inisialisasi total barang
            for(String s : listBarangBeli.keySet()){
                for(BarangJualModel b : listBarangBeli.get(s)){
                    harga_total_barang += b.getJumlah() * b.getHarga();
                }
            }
            txt_total.setText(Converter.doubleToRupiah(harga_total_barang + harga_total_ongkir));
        }

        //Inisialisasi recycleView & Adapter barang beli
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PembayaranAdapter(this, listHeader, listBarangBeli, listOngkir);
        rv_barang.setAdapter(adapter);

        findViewById(R.id.btn_bayar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!adapter.checkKurir()){
                    Toast.makeText(PembayaranActivity.this,
                            "Jasa pengiriman yang belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyUser();
                }
            }
        });

        findViewById(R.id.txt_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tambah alamat_dipilih
                startActivityForResult(new Intent(PembayaranActivity.this,
                        PembayaranAlamatTambah.class), KODE_TAMBAH_ALAMAT);
            }
        });

        findViewById(R.id.layout_alamat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alamat_kosong){
                    startActivityForResult(new Intent(PembayaranActivity.this,
                            PembayaranAlamatTambah.class), KODE_TAMBAH_ALAMAT);
                }
                else{
                    Intent i = new Intent(PembayaranActivity.this,
                            PembayaranAlamatGanti.class);
                    /*Gson gson = new Gson();
                    ArrayList<String> listAlamatString = new ArrayList<>();
                    for(AlamatModel a : listAlamat){
                        listAlamatString.add(gson.toJson(a));
                    }
                    i.putStringArrayListExtra(Constant.EXTRA_LIST_ALAMAT,listAlamatString);*/
                    startActivityForResult(i, KODE_GANTI_ALAMAT);
                }
            }
        });

        loadAlamat();
    }

    private void pembayaran(){
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        for(ArtisModel a : listHeader){
            String header_id = a.getId();

            //Inisialisasi detail pembayaran
            for(int i = 0; i < listBarangBeli.get(header_id).size(); i++){
                BarangJualModel barang = listBarangBeli.get(header_id).get(i);
                itemDetailsList.add(new ItemDetails(barang.getId(), barang.getHarga(),
                        barang.getJumlah(), barang.getNama()));
            }

            //Biaya Ongkir
            itemDetailsList.add(new ItemDetails("0", listOngkir.get(header_id).getHarga(),
                    1, listOngkir.get(header_id).getService()));
        }

        String id_transaksi = String.valueOf(System.currentTimeMillis());
        TransactionRequest transactionRequest = new TransactionRequest(id_transaksi,
                harga_total_barang + harga_total_ongkir);
        Log.d(Constant.TAG, id_transaksi);
        transactionRequest.setItemDetails(itemDetailsList);

        BillInfoModel billInfoModel = new BillInfoModel("SELBI", "Transaksi");
        // Set the bill info on transaction details
        transactionRequest.setBillInfoModel(billInfoModel);
        MidtransSDK.getInstance().setTransactionRequest(transactionRequest);

        MidtransSDK.getInstance().startPaymentUiFlow(PembayaranActivity.this);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void loadAlamat(){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id", "");
        body.add("keyword", "");
        body.add("start", "");
        body.add("count", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ALAMAT_PENGIRIMAN_LIST,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        txt_alamat.setText("Tidak ada alamat tersedia");

                        alamat_kosong = true;
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            alamat_dipilih = null;
                            JSONArray response = new JSONArray(result);
                            if(response.length() > 0){
                                JSONObject alamat = response.getJSONObject(0);
                                alamat_dipilih = new AlamatModel(alamat.getString("id"), alamat.getString("label"),
                                        alamat.getString("penerima"), alamat.getString("telepon"),
                                        alamat.getString("ref_kota"), alamat.getString("kota"),
                                        alamat.getString("ref_provinsi"), alamat.getString("provinsi"),
                                        alamat.getString("alamat"), alamat.getString("kodepos"));
                                updateAlamatPengiriman(alamat_dipilih);
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(PembayaranActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void verifyUser(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PROFIL,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String response) {
                        try{
                            JSONObject akun = new JSONObject(response);

                            user = new UserModel(akun.getString("id"), akun.getString("profile_name"),
                                    akun.getString("alamat"), akun.getString("no_telp"),
                                    akun.getString("email"));

                            //userDetail = LocalDataHandler.readObject("user_details", UserDetail.class);
                            UserDetail userDetail = new UserDetail();
                            userDetail.setUserFullName(user.getNama());
                            userDetail.setEmail(user.getEmail());
                            userDetail.setPhoneNumber(user.getTelepon());

                            ArrayList<UserAddress> userAddresses = new ArrayList<>();
                            UserAddress userAddress = new UserAddress();
                            userAddress.setAddress(akun.getString("alamat"));
                            //userAddress.setCity("Jakarta");
                            userAddress.setAddressType(Constants.ADDRESS_TYPE_BILLING);
                            //userAddress.setZipcode("40184");
                            userAddress.setCountry("IDN");
                            userAddresses.add(userAddress);
                            userDetail.setUserAddresses(userAddresses);
                            LocalDataHandler.saveObject("user_details", userDetail);

                            pembayaran();
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                            Toast.makeText(PembayaranActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PembayaranActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void initTransaksi(){
        //Inisialisasi transaksi Midtrans
        SdkUIFlowBuilder.init()
                .setClientKey(MERCHANT_CLIENT_KEY) // client_key is mandatory
                .setContext(this) // context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(MERCHANT_BASE_CHECKOUT_URL) //set merchant url (required) BASE_URL
                .enableLog(true)// enable sdk log (optional)
                .buildSDK();
    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if (result.getResponse() != null) {
            switch (result.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaksi berhasil", Toast.LENGTH_SHORT).show();
                    Log.d(Constant.TAG, "Transaction Finished. ID: " + result.getResponse().getTransactionId());

                    //Kembali ke keranjang
                    Intent i = new Intent(this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(Constant.EXTRA_START, 3);
                    startActivity(i);

                    //hapus keranjang

                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaksi pending", Toast.LENGTH_SHORT).show();
                    Log.d(Constant.TAG, "Transaction Pending. ID: " + result.getResponse().getTransactionId());
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaksi gagal", Toast.LENGTH_SHORT).show();
                    Log.d(Constant.TAG, "Transaction Failed. ID: " + result.getResponse().getTransactionId() + ". Message: " + result.getResponse().getStatusMessage());
                    break;
            }
            result.getResponse().getValidationMessages();
        } else if (result.isTransactionCanceled()) {
            Toast.makeText(this, "Transaksi dibatalkan", Toast.LENGTH_LONG).show();
        } else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaksi Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction selesai dengan kegagalan.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateAlamatPengiriman(AlamatModel alamat_baru){
        alamat_dipilih = alamat_baru;
        txt_alamat.setText(alamat_dipilih.toString());
        String alamat = alamat_dipilih.getNama_kota() + ", " + alamat_dipilih.getNama_provinsi();
        txt_kota.setText(alamat);
        txt_kodepos.setText(alamat_dipilih.getKodepos());

        //reset ongkir
        listOngkir.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == KODE_TAMBAH_ALAMAT){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Gson gson = new Gson();
                    updateAlamatPengiriman(gson.fromJson(data.getStringExtra(Constant.RESULT_ALAMAT), AlamatModel.class));
                    alamat_kosong = false;
                }
            }
        }
        else if(requestCode == KODE_GANTI_ALAMAT){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Gson gson = new Gson();
                    updateAlamatPengiriman(gson.fromJson(data.getStringExtra(Constant.RESULT_ALAMAT), AlamatModel.class));
                }
            }
        }
        else if(requestCode == KODE_GANTI_ONGKIR){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Gson gson = new Gson();
                    int position = data.getIntExtra(Constant.EXTRA_POSITION, 0);
                    OngkirModel o = gson.fromJson(data.getStringExtra(Constant.EXTRA_ONGKIR), OngkirModel.class);

                    listOngkir.put(listHeader.get(position).getId(), o);
                    adapter.notifyDataSetChanged();

                    //Inisialisasi total barang
                    harga_total_ongkir = 0;
                    for(String s : listOngkir.keySet()){
                        harga_total_ongkir += listOngkir.get(s).getHarga();
                    }
                    txt_total.setText(Converter.doubleToRupiah(harga_total_barang + harga_total_ongkir));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
