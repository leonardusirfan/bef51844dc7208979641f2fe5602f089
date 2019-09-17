package id.net.gmedia.selby.Pembayaran;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.Converter;
import com.leonardus.irfan.ImageLoader;

import java.util.LinkedHashMap;
import java.util.List;

import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangJualModel;
import id.net.gmedia.selby.Model.OngkirModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class PembayaranAdapter extends RecyclerView.Adapter<PembayaranAdapter.PembayaranDetailViewHolder> {

    private PembayaranActivity activity;
    private LinkedHashMap<String, List<BarangJualModel>> keranjang;
    //private LinkedHashMap<String, OngkirModel> listOngkir;
    private List<ArtisModel> listHeader;

    /*PembayaranAdapter(PembayaranActivity activity, List<ArtisModel> listHeader, LinkedHashMap<String,
            List<BarangJualModel>> keranjang, LinkedHashMap<String, OngkirModel> listOngkir){
        this.activity = activity;
        this.listHeader = listHeader;
        this.keranjang = keranjang;
        this.listOngkir = listOngkir;
    }*/

    PembayaranAdapter(PembayaranActivity activity, List<ArtisModel> listHeader, LinkedHashMap<String,
            List<BarangJualModel>> keranjang){
        this.activity = activity;
        this.listHeader = listHeader;
        this.keranjang = keranjang;
    }

    @NonNull
    @Override
    public PembayaranDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PembayaranDetailViewHolder(LayoutInflater.from(activity).
                inflate(R.layout.item_pembayaran_detail_barang_header, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PembayaranDetailViewHolder holder, int i) {
        final ArtisModel header = listHeader.get(i);
        final PembayaranDetailViewHolder final_holder = holder;
        final List<BarangJualModel> listBarang = keranjang.get(header.getId());

        String penjual = "Produk " + header.getNama();
        holder.txt_penjual.setText(penjual);

        holder.rv_barang.setItemAnimator(new DefaultItemAnimator());
        holder.rv_barang.setLayoutManager(new LinearLayoutManager(activity));
        holder.rv_barang.setAdapter(new PembayaranChildAdapter(listBarang));

        //OngkirModel o = listOngkir.get(header.getId());
        /*if(o != null){
            String pengiriman = o.getService() + " - " + Converter.doubleToRupiah(o.getHarga());
            holder.txt_pengiriman.setText(pengiriman);
        }
        else{
            holder.txt_pengiriman.setText("Jasa pengiriman belum dipilih");
        }*/

        /*holder.layout_kurir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.alamat_dipilih == null){
                    Toast.makeText(activity, "Alamat pengiriman belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i  = new Intent(activity, PembayaranOngkirGanti.class);

                    i.putExtra(Constant.EXTRA_ALAMAT_ID_ASAL, header.getId_kota());
                    i.putExtra(Constant.EXTRA_ALAMAT_ID_TUJUAN, activity.alamat_dipilih.getId_kota());
                    i.putExtra(Constant.EXTRA_BERAT_BARANG, 200);
                    i.putExtra(Constant.EXTRA_POSITION, final_holder.getAdapterPosition());
                    activity.startActivityForResult(i, activity.KODE_GANTI_ONGKIR);
                }
            }
        });*/
    }

    /*boolean checkKurir(){
        for(ArtisModel p : listHeader){
            if(listOngkir.containsKey(p.getId())){
                for(String s : listOngkir.keySet()){
                    if(listOngkir.get(s).getId().equals("")){
                        return false;
                    }
                }
            }
            else{
                return false;
            }
        }

        return true;
    }*/

    @Override
    public int getItemCount() {
        return listHeader.size();
    }

    class PembayaranDetailViewHolder extends RecyclerView.ViewHolder{

        //View layout_kurir;
        //TextView txt_pengiriman;
        TextView txt_penjual, txt_harga;
        RecyclerView rv_barang;

        PembayaranDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_penjual = itemView.findViewById(R.id.txt_penjual);
            rv_barang = itemView.findViewById(R.id.rv_barang);
            //txt_pengiriman = itemView.findViewById(R.id.txt_pengiriman);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            //layout_kurir = itemView.findViewById(R.id.layout_kurir);
        }
    }

    class PembayaranChildAdapter extends RecyclerView.Adapter<PembayaranChildAdapter.PembayaranChildViewHolder>{

        private List<BarangJualModel> listBarang;

        PembayaranChildAdapter(List<BarangJualModel> listBarang){
            this.listBarang = listBarang;
        }

        @NonNull
        @Override
        public PembayaranChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new PembayaranChildViewHolder(LayoutInflater.from(activity).
                    inflate(R.layout.item_pembayaran_detail_barang, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PembayaranChildViewHolder holder, int i) {
            BarangJualModel b = listBarang.get(i);

            ImageLoader.load(activity, b.getUrl(), holder.img_barang);
            holder.txt_nama.setText(b.getNama());
            String jumlah = "Jumlah : " + b.getJumlah();
            holder.txt_jumlah.setText(jumlah);
        }

        @Override
        public int getItemCount() {
            return listBarang.size();
        }

        class PembayaranChildViewHolder extends RecyclerView.ViewHolder{

            TextView txt_nama, txt_jumlah;
            ImageView img_barang;

            PembayaranChildViewHolder(@NonNull View itemView) {
                super(itemView);
                txt_nama = itemView.findViewById(R.id.txt_nama);
                txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
                img_barang = itemView.findViewById(R.id.img_barang);
            }
        }
    }
}
