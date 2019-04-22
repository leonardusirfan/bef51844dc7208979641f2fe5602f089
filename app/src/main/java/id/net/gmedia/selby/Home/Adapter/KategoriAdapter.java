package id.net.gmedia.selby.Home.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

import id.net.gmedia.selby.Home.ArtisActivity;
import id.net.gmedia.selby.Home.BarangActivity;
import id.net.gmedia.selby.Home.LelangActivity;
import id.net.gmedia.selby.R;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

    private Activity activity;
    private List<SimpleObjectModel> listKategori;
    private int selected = 0;

    public KategoriAdapter(Activity activity, List<SimpleObjectModel> listKategori){
        this.activity = activity;
        this.listKategori = listKategori;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new KategoriViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kategori, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriViewHolder kategoriViewHolder, int i) {
        final int position = kategoriViewHolder.getAdapterPosition();
        if(i == selected){
            kategoriViewHolder.txt_kategori.setBackgroundResource(R.drawable.style_fullrounded_dark_blue_rectangle);
            kategoriViewHolder.txt_kategori.setTextColor(activity.getResources().getColor(R.color.white));
        }
        else{
            kategoriViewHolder.txt_kategori.setBackgroundResource(R.drawable.style_fullrounded_darkblue_bordered_white_rectangle);
            kategoriViewHolder.txt_kategori.setTextColor(activity.getResources().getColor(R.color.dark_blue));
        }

        kategoriViewHolder.txt_kategori.setText(listKategori.get(i).getValue());

        kategoriViewHolder.txt_kategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set kategori dalam halaman tampilan
                if(activity instanceof ArtisActivity){
                    ((ArtisActivity)activity).setKategori(listKategori.get(position).getId());
                }
                else if(activity instanceof BarangActivity){
                    ((BarangActivity)activity).setKategori(listKategori.get(position).getId());
                }
                else if(activity instanceof LelangActivity){
                    ((LelangActivity)activity).setKategori(listKategori.get(position).getId());
                }

                int previous = selected;
                selected = position;
                notifyItemChanged(previous);
                notifyItemChanged(selected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listKategori.size();
    }

    class KategoriViewHolder extends RecyclerView.ViewHolder{

        TextView txt_kategori;

        KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_kategori = itemView.findViewById(R.id.txt_kategori);
        }
    }
}
