package id.net.gmedia.selby.Barang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.selby.Barang.Adapter.BidAdapter;
import id.net.gmedia.selby.Model.BidModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.Constant;

public class ActivityBidList extends AppCompatActivity {
    /*
        Activity yang menampilkan list/history bid yang sudah dilakukan terhadap barang lelang
     */

    //Variabel penampung id lelang yang akan ditampilkan list bid nya
    private String id_lelang;

    //Variabel navigasi halaman list bid yang ditampilkan
    private int page = 0;
    private int total = 0;

    //Variabel list bidding dan adapter
    private BidAdapter adapter;
    private ArrayList<ArrayList<BidModel>> listBid = new ArrayList<>();
    private List<BidModel> listShow = new ArrayList<>();
    private RecyclerView rv_bid;

    //Variabel UI
    TextView txt_info_bid;
    //TextView txt_jumlah_bid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_list);

        //Inisialisasi Toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Mengambil id lelang dari activity sebelumnya
        id_lelang = getIntent().getStringExtra("id_lelang");

        //Inisialisasi UI
        TextView txt_first, txt_next, txt_previous, txt_last;
        //txt_jumlah_bid = findViewById(R.id.txt_jumlah_bid);
        txt_info_bid = findViewById(R.id.txt_info_bid);
        txt_first = findViewById(R.id.txt_first);
        txt_next = findViewById(R.id.txt_next);
        txt_previous = findViewById(R.id.txt_previous);
        txt_last = findViewById(R.id.txt_last);
        rv_bid = findViewById(R.id.rv_bid);

        //Inisialisasi Recycler View list bid
        adapter = new BidAdapter(listShow);
        rv_bid.setItemAnimator(new DefaultItemAnimator());
        rv_bid.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_bid.setAdapter(adapter);

        //Inisialisasi Navigasi list show
        txt_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 0;
                ubahIsiBid();
            }
        });
        txt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page < listBid.size() - 1){
                    page++;
                    ubahIsiBid();
                }
            }
        });
        txt_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page != 0){
                    page--;
                    ubahIsiBid();
                }
            }
        });
        txt_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = listBid.size() - 1;
                ubahIsiBid();
            }
        });

        //Memuat keseluruhan List Bid dari Web Service
        initBid();
    }

    private void ubahIsiBid(){
        //Mengubah bid yang ditampilkan
        listShow = listBid.get(page);
        adapter = new BidAdapter(listShow);
        rv_bid.setAdapter(adapter);
        txt_info_bid.setText(String.format(Locale.getDefault(), "Showing %d to %d of %d entries", 10 * page, 10 * page + listShow.size(), total));
    }

    private void initBid(){
        try{
            JSONObject body = new JSONObject();
            body.put("id", id_lelang);
            body.put("start", 0);
            body.put("count", 0);

            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_LIST_BID, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        System.out.println(result);
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            ArrayList<BidModel> allBid = new ArrayList<>();
                            total = jsonresult.getJSONObject("response").getInt("total_records");
                            //txt_jumlah_bid.setText(String.valueOf(total));
                            JSONArray arraybid = jsonresult.getJSONObject("response").getJSONArray("bid");

                            for(int i = 0; i < arraybid.length(); i++){
                                JSONObject jsonlelang = arraybid.getJSONObject(i);
                                allBid.add(new BidModel(jsonlelang.getString("profile_name"), jsonlelang.getDouble("nominal")));
                            }

                            initList(allBid);
                        }
                        else if(status != 404){
                            Toast.makeText(ActivityBidList.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(ActivityBidList.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("ListBid", e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(ActivityBidList.this, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("ListBid", result);
                }
            });
        }
        catch (JSONException e){
            Toast.makeText(this, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("ListBid", e.getMessage());
        }
    }

    private void initList(ArrayList<BidModel> allBid){
        //Memecah keseluruhan bid kedalam halaman - halaman yang masing - masing berisi 10 bid
        int i = 0;
        while (i < allBid.size()){
            int maks = allBid.size() - i < 10 ? allBid.size() - i : 10;

            ArrayList<BidModel> temp = new ArrayList<>();
            for(int j = 0; j < maks; j++){
                temp.add(allBid.get(i));
                i++;
            }
            listBid.add(temp);
        }

        //Menginisialisasi halaman pertama listBId untuk ditampilkan dalam listShow
        page = 0;
        listShow = listBid.get(page);
        adapter = new BidAdapter(listShow);
        rv_bid.setAdapter(adapter);
        txt_info_bid.setText(String.format(Locale.getDefault(), "Showing %d to %d of %d entries", 10 * page, 10 * page + listShow.size(), total));
    }

    //FUNGSI MENU ACTION BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
