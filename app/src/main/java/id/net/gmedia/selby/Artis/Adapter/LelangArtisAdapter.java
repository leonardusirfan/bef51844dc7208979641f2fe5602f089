package id.net.gmedia.selby.Artis.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leonardus.irfan.Converter;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.selby.Barang.LelangDetailActivity;
import id.net.gmedia.selby.Home.LelangActivity;
import id.net.gmedia.selby.Model.LelangModel;
import id.net.gmedia.selby.R;

public class LelangArtisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String id_penjual = "";

    private final int TYPE_HEADER = 0;
    private final int TYPE_CONTENT = 1;

    private Context context;
    private List<LelangModel> listLelang;

    public LelangArtisAdapter(List<LelangModel> listLelang, String id_penjual){
        this.listLelang = listLelang;
        this.id_penjual = id_penjual;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        if(getItemViewType(i) == TYPE_CONTENT){
            return new LelangViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artis_lelang, viewGroup, false));
        }
        else{
            return new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artis_lelang_header, viewGroup, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder lelangViewHolder, int i) {
       if(lelangViewHolder instanceof LelangViewHolder){
           final LelangModel lelang = listLelang.get(i - 1);
           final LelangViewHolder holder = (LelangViewHolder)lelangViewHolder;

           holder.txt_nama.setText(lelang.getNama());
           String harga = "Harga Normal : " + Converter.doubleToRupiah(lelang.getHargaNormal());
           holder.txt_harga.setText(harga);
           Glide.with(context).load(lelang.getUrl()).into(holder.img_lelang);

           //Inisialisasi TImer
           Date now = new Date();
           Date end = lelang.getTglSelesai();
           long timeLeft = 0;
           if(end != null){
               timeLeft = end.getTime() - now.getTime();
           }
           new CountDownTimer(timeLeft, 1000){

               long secondsInMilli = 1000;
               long minutesInMilli = secondsInMilli * 60;
               long hoursInMilli = minutesInMilli * 60;
               long daysInMilli = hoursInMilli * 24;

               @Override
               public void onTick(long millisUntilFinished) {
                   long elapsedDays = millisUntilFinished / daysInMilli;
                   millisUntilFinished =  millisUntilFinished % daysInMilli;

                   long elapsedHours =  millisUntilFinished / hoursInMilli;
                   millisUntilFinished =  millisUntilFinished % hoursInMilli;

                   long elapsedMinutes =  millisUntilFinished / minutesInMilli;
                   millisUntilFinished =  millisUntilFinished % minutesInMilli;

                   long elapsedSeconds =  millisUntilFinished / secondsInMilli;

                   holder.txt_hari.setText(String.format(Locale.getDefault(), "%02d",elapsedDays));
                   holder.txt_jam.setText(String.format(Locale.getDefault(), "%02d",elapsedHours));
                   holder.txt_menit.setText(String.format(Locale.getDefault(), "%02d",elapsedMinutes));
                   holder.txt_detik.setText(String.format(Locale.getDefault(), "%02d",elapsedSeconds));
               }

               @Override
               public void onFinish() {
                   //hapus item
                   listLelang.remove(lelangViewHolder.getAdapterPosition());
                   notifyItemRemoved(lelangViewHolder.getAdapterPosition());
               }
           }.start();

           holder.layout_lelang.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = new Intent(new Intent(context, LelangDetailActivity.class));
                   i.putExtra("lelang", lelang.getId());
                   context.startActivity(i);
                   //((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
               }
           });
       }
       else if(lelangViewHolder instanceof HeaderViewHolder){
           ((HeaderViewHolder)lelangViewHolder).txt_lihat_semua.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = new Intent(context, LelangActivity.class);
                   i.putExtra("id_penjual", id_penjual);
                   context.startActivity(i);
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        return listLelang.size()>3 ? 4 : listLelang.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }
        else{
            return TYPE_CONTENT;
        }
    }

    class LelangViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_nama, txt_harga, txt_hari, txt_jam, txt_menit, txt_detik;
        private ImageView img_lelang;
        private CardView layout_lelang;

        LelangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_hari = itemView.findViewById(R.id.txt_hari);
            txt_jam = itemView.findViewById(R.id.txt_jam);
            txt_menit = itemView.findViewById(R.id.txt_menit);
            txt_detik = itemView.findViewById(R.id.txt_detik);
            img_lelang = itemView.findViewById(R.id.img_lelang);
            layout_lelang = itemView.findViewById(R.id.layout_lelang);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{

        TextView txt_lihat_semua;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_lihat_semua = itemView.findViewById(R.id.txt_lihat_semua);
        }
    }
}
