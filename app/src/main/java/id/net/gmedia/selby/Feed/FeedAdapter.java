package id.net.gmedia.selby.Feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.List;

import id.net.gmedia.selby.Artis.ArtisDetailActivity;
import id.net.gmedia.selby.Artis.EventActivity;
import id.net.gmedia.selby.Artis.BarangArtisActivity;
import id.net.gmedia.selby.Barang.LelangDetailActivity;
import id.net.gmedia.selby.Feed.FeedItem.BarangItemModel;
import id.net.gmedia.selby.Feed.FeedItem.FeedItemModel;
import id.net.gmedia.selby.Feed.FeedItem.GambarItemModel;
import id.net.gmedia.selby.Feed.FeedItem.KegiatanItemModel;
import id.net.gmedia.selby.Feed.FeedItem.LelangItemModel;
import id.net.gmedia.selby.Feed.FeedItem.RekomendasiItemModel;
import id.net.gmedia.selby.Feed.FeedItem.TextItemModel;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Converter;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<FeedItemModel> listItem;

    FeedAdapter(List<FeedItemModel> listItem){
        this.listItem = listItem;
    }

    @Override
    public int getItemViewType(int position) {
        return listItem.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if(i == FeedItemModel.TYPE_TEXT){
            return new TextViewHolder(inflater.inflate(R.layout.item_feed_text, viewGroup, false));
        }
        else if(i == FeedItemModel.TYPE_BARANG){
            return new BarangViewHolder(inflater.inflate(R.layout.item_feed_barang, viewGroup, false));
        }
        else if(i == FeedItemModel.TYPE_GAMBAR1){
            return new Gambar1ViewHolder(inflater.inflate(R.layout.item_feed_gambar_satu, viewGroup, false));
        }
        else if(i == FeedItemModel.TYPE_GAMBAR2){
            return new Gambar2ViewHolder(inflater.inflate(R.layout.item_feed_gambar_dua, viewGroup, false));
        }
        else if(i == FeedItemModel.TYPE_GAMBAR3){
            return new Gambar3ViewHolder(inflater.inflate(R.layout.item_feed_gambar_tiga, viewGroup, false));
        }
        else if(i == FeedItemModel.TYPE_LELANG){
            return new LelangViewHolder(inflater.inflate(R.layout.item_feed_lelang, viewGroup, false));
        }
        else if(i == FeedItemModel.TYPE_KEGIATAN){
            return new KegiatanViewHolder(inflater.inflate(R.layout.item_feed_kegiatan, viewGroup, false));
        }
        else{
            return new RekomendasiViewHolder(inflater.inflate(R.layout.item_feed_rekomendasi, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof TextViewHolder){
            final TextItemModel item = (TextItemModel)listItem.get(i);
            final TextViewHolder holder = (TextViewHolder)viewHolder;

            Glide.with(context).load(item.getArtis().getImage()).apply(new RequestOptions().circleCrop()).thumbnail(0.5f).into(holder.img_user);
            holder.txt_user.setText(item.getArtis().getNama());
            holder.txt_detail.setText(R.string.feed_status);
            holder.txt_tanggal.setText(Converter.DateToString(item.getTimestamp()));
            holder.txt_status.setText(item.getStatus());

            holder.layout_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, ArtisDetailActivity.class);
                    intent.putExtra("artis", gson.toJson(item.getArtis()));

                    context.startActivity(intent);
                }
            });
        }
        else if(viewHolder instanceof BarangViewHolder){
            final BarangItemModel item = (BarangItemModel)listItem.get(i);
            final BarangViewHolder holder = (BarangViewHolder)viewHolder;

            Glide.with(context).load(item.getArtis().getImage()).apply(new RequestOptions().circleCrop()).thumbnail(0.5f).into(holder.img_user);
            holder.txt_user.setText(item.getArtis().getNama());
            holder.txt_detail.setText(R.string.feed_barang);
            holder.txt_tanggal.setText(Converter.DateToString(item.getTimestamp()));

            BarangItemAdapter adapter = new BarangItemAdapter(item.getListBarang());
            holder.rv_barang.setItemAnimator(new DefaultItemAnimator());
            holder.rv_barang.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.rv_barang.setAdapter(adapter);

            holder.btn_lihat_semua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent i = new Intent(context, BarangArtisActivity.class);
                    i.putExtra("artis", gson.toJson(item.getArtis()));
                    context.startActivity(i);
                    ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            holder.layout_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, ArtisDetailActivity.class);
                    intent.putExtra("artis", gson.toJson(item.getArtis()));

                    context.startActivity(intent);
                }
            });
        }
        else if(viewHolder instanceof Gambar1ViewHolder){
            final GambarItemModel item = (GambarItemModel) listItem.get(i);
            final Gambar1ViewHolder holder = (Gambar1ViewHolder) viewHolder;

            holder.txt_user.setText(item.getArtis().getNama());
            holder.txt_tanggal.setText(Converter.DateToString(item.getTimestamp()));
            holder.txt_detail.setText(R.string.feed_gambar1);
            Glide.with(context).load(item.getArtis().getImage()).apply(new RequestOptions().circleCrop()).thumbnail(0.5f).into(holder.img_user);
            Glide.with(context).load(item.getListGambar().get(0)).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_artis);

            if(!item.getStatus().equals("")){
                holder.txt_status.setVisibility(View.VISIBLE);
                holder.txt_status.setText(item.getStatus());
            }

            holder.img_artis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).setView(item.getListGambar(), 0);
                }
            });

            holder.btn_lihat_semua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent i = new Intent(context, EventActivity.class);
                    i.putExtra("artis", gson.toJson(item.getArtis()));
                    i.putExtra("page", 1);
                    context.startActivity(i);
                    ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            holder.layout_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, ArtisDetailActivity.class);
                    intent.putExtra("artis", gson.toJson(item.getArtis()));

                    context.startActivity(intent);
                }
            });
        }
        else if(viewHolder instanceof Gambar2ViewHolder){
            final GambarItemModel item = (GambarItemModel) listItem.get(i);
            final Gambar2ViewHolder holder = (Gambar2ViewHolder) viewHolder;

            holder.txt_user.setText(item.getArtis().getNama());
            holder.txt_tanggal.setText(Converter.DateToString(item.getTimestamp()));
            holder.txt_detail.setText(context.getResources().getString(R.string.feed_gambar, 2));
            Glide.with(context).load(item.getArtis().getImage()).apply(new RequestOptions().circleCrop()).thumbnail(0.5f).into(holder.img_user);
            Glide.with(context).load(item.getListGambar().get(0)).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_artis1);
            Glide.with(context).load(item.getListGambar().get(1)).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_artis2);

            if(!item.getStatus().equals("")){
                holder.txt_status.setVisibility(View.VISIBLE);
                holder.txt_status.setText(item.getStatus());
            }

            holder.img_artis1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).setView(item.getListGambar(), 0);
                }
            });

            holder.img_artis2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).setView(item.getListGambar(), 1);
                }
            });

            holder.btn_lihat_semua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent i = new Intent(context, EventActivity.class);
                    i.putExtra("artis", gson.toJson(item.getArtis()));
                    i.putExtra("page", 1);
                    context.startActivity(i);
                    ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            holder.layout_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, ArtisDetailActivity.class);
                    intent.putExtra("artis", gson.toJson(item.getArtis()));

                    context.startActivity(intent);
                }
            });
        }
        else if(viewHolder instanceof Gambar3ViewHolder){
            final GambarItemModel item = (GambarItemModel) listItem.get(i);
            final Gambar3ViewHolder holder = (Gambar3ViewHolder) viewHolder;

            holder.txt_user.setText(item.getArtis().getNama());
            holder.txt_tanggal.setText(Converter.DateToString(item.getTimestamp()));

            String detail = context.getResources().getString(R.string.feed_gambar, item.getListGambar().size());
            holder.txt_detail.setText(detail);
            Glide.with(context).load(item.getArtis().getImage()).apply(new RequestOptions().circleCrop()).thumbnail(0.5f).into(holder.img_user);
            Glide.with(context).load(item.getListGambar().get(0)).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_artis1);
            Glide.with(context).load(item.getListGambar().get(1)).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_artis2);
            Glide.with(context).load(item.getListGambar().get(2)).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_artis3);

            if(!item.getStatus().equals("")){
                holder.txt_status.setVisibility(View.VISIBLE);
                holder.txt_status.setText(item.getStatus());
            }

            holder.img_artis1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).setView(item.getListGambar(), 0);
                }
            });

            holder.img_artis2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).setView(item.getListGambar(), 1);
                }
            });

            holder.img_artis3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).setView(item.getListGambar(), 2);
                }
            });

            holder.btn_lihat_semua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent i = new Intent(context, EventActivity.class);
                    i.putExtra("artis", gson.toJson(item.getArtis()));
                    i.putExtra("page", 1);
                    context.startActivity(i);
                    ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            holder.layout_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, ArtisDetailActivity.class);
                    intent.putExtra("artis", gson.toJson(item.getArtis()));

                    context.startActivity(intent);
                }
            });
        }
        else if(viewHolder instanceof LelangViewHolder){
            final LelangItemModel item = (LelangItemModel)listItem.get(i);
            final LelangViewHolder holder = (LelangViewHolder)viewHolder;

            Glide.with(context).load(item.getArtis().getImage()).apply(new RequestOptions().circleCrop()).thumbnail(0.5f).into(holder.img_user);
            holder.txt_user.setText(item.getArtis().getNama());
            holder.txt_detail.setText(R.string.feed_lelang);
            holder.txt_tanggal.setText(Converter.DateToString(item.getTimestamp()));
            holder.txt_barang.setText(item.getLelang().getNama());
            Glide.with(context).load(item.getLelang().getUrl()).thumbnail(0.3f).into(holder.img_barang);

            holder.layout_barang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(new Intent(context, LelangDetailActivity.class));
                    i.putExtra("lelang", item.getLelang().getId());
                    context.startActivity(i);
                }
            });

            holder.btn_lihat_semua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent i = new Intent(context, BarangArtisActivity.class);
                    i.putExtra("artis", gson.toJson(item.getArtis()));
                    context.startActivity(i);
                    ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            holder.layout_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, ArtisDetailActivity.class);
                    intent.putExtra("artis", gson.toJson(item.getArtis()));

                    context.startActivity(intent);
                }
            });
        }
        else if(viewHolder instanceof KegiatanViewHolder){
            final KegiatanItemModel item = (KegiatanItemModel) listItem.get(i);
            final KegiatanViewHolder holder = (KegiatanViewHolder) viewHolder;

            Glide.with(context).load(item.getArtis().getImage()).apply(new RequestOptions().circleCrop()).thumbnail(0.5f).into(holder.img_user);
            holder.txt_user.setText(item.getArtis().getNama());
            holder.txt_detail.setText(R.string.feed_kegiatan);
            holder.txt_tanggal.setText(Converter.DateToString(item.getTimestamp()));

            holder.txt_judul_kegiatan.setText(item.getKegiatan().getJudul());
            holder.txt_tempat_kegiatan.setText(item.getKegiatan().getTempat());
            holder.txt_tanggal_kegiatan.setText(Converter.DateToString(item.getKegiatan().getTanggal()));
            holder.txt_deskripsi_kegiatan.setText(item.getKegiatan().getDeskripsi());

            holder.btn_lihat_semua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent i = new Intent(context, EventActivity.class);
                    i.putExtra("artis", gson.toJson(item.getArtis()));
                    context.startActivity(i);
                    ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            holder.layout_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, ArtisDetailActivity.class);
                    intent.putExtra("artis", gson.toJson(item.getArtis()));
                    context.startActivity(intent);
                }
            });

        }
        else if(viewHolder instanceof RekomendasiViewHolder){
            RekomendasiItemModel item = (RekomendasiItemModel)listItem.get(i);
            RekomendasiViewHolder holder = (RekomendasiViewHolder)viewHolder;

            RekomendasiItemAdapter adapter = new RekomendasiItemAdapter(item.getListArtis());
            holder.rv_artis.setItemAnimator(new DefaultItemAnimator());
            holder.rv_artis.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.rv_artis.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    class TextViewHolder extends RecyclerView.ViewHolder{

        TextView txt_user, txt_status, txt_tanggal, txt_detail;
        ImageView img_user;
        RelativeLayout layout_user;

        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user = itemView.findViewById(R.id.txt_user);
            txt_detail = itemView.findViewById(R.id.txt_detail);
            txt_status = itemView.findViewById(R.id.txt_status);
            img_user = itemView.findViewById(R.id.img_user);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            layout_user = itemView.findViewById(R.id.layout_user);
        }
    }

    class RekomendasiViewHolder extends RecyclerView.ViewHolder{

        RecyclerView rv_artis;

        RekomendasiViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_artis = itemView.findViewById(R.id.rv_artis);
        }
    }

    class BarangViewHolder extends RecyclerView.ViewHolder{

        TextView txt_user, txt_tanggal, txt_detail;
        ImageView img_user;
        RecyclerView rv_barang;
        LinearLayout btn_lihat_semua;
        RelativeLayout layout_user;

        BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user = itemView.findViewById(R.id.txt_user);
            txt_detail = itemView.findViewById(R.id.txt_detail);
            rv_barang = itemView.findViewById(R.id.rv_barang);
            img_user = itemView.findViewById(R.id.img_user);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            btn_lihat_semua = itemView.findViewById(R.id.btn_lihat_semua);
            layout_user = itemView.findViewById(R.id.layout_user);
        }
    }

    class LelangViewHolder extends RecyclerView.ViewHolder{

        TextView txt_user, txt_tanggal, txt_barang, txt_detail;
        ImageView img_user, img_barang;
        LinearLayout btn_lihat_semua;
        MaterialCardView layout_barang;
        RelativeLayout layout_user;

        LelangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user = itemView.findViewById(R.id.txt_user);
            txt_detail = itemView.findViewById(R.id.txt_detail);
            img_barang = itemView.findViewById(R.id.img_barang);
            txt_barang = itemView.findViewById(R.id.txt_barang);
            img_user = itemView.findViewById(R.id.img_user);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            btn_lihat_semua = itemView.findViewById(R.id.btn_lihat_semua);
            layout_barang = itemView.findViewById(R.id.layout_barang);
            layout_user = itemView.findViewById(R.id.layout_user);
        }
    }

    class Gambar1ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_user, txt_tanggal, txt_detail;
        ImageView img_user;
        ImageView img_artis;
        TextView txt_status;
        LinearLayout btn_lihat_semua;
        RelativeLayout layout_user;

        Gambar1ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user = itemView.findViewById(R.id.txt_user);
            img_user = itemView.findViewById(R.id.img_user);
            txt_detail = itemView.findViewById(R.id.txt_detail);
            img_artis = itemView.findViewById(R.id.img_artis);
            txt_status = itemView.findViewById(R.id.txt_status);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            btn_lihat_semua = itemView.findViewById(R.id.btn_lihat_semua);
            layout_user = itemView.findViewById(R.id.layout_user);
        }
    }

    class Gambar2ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_user, txt_tanggal, txt_detail;
        ImageView img_user;
        ImageView img_artis1;
        ImageView img_artis2;
        TextView txt_status;
        LinearLayout btn_lihat_semua;
        RelativeLayout layout_user;

        Gambar2ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user = itemView.findViewById(R.id.txt_user);
            img_user = itemView.findViewById(R.id.img_user);
            txt_detail = itemView.findViewById(R.id.txt_detail);
            img_artis1 = itemView.findViewById(R.id.img_artis1);
            img_artis2 = itemView.findViewById(R.id.img_artis2);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_status = itemView.findViewById(R.id.txt_status);
            btn_lihat_semua = itemView.findViewById(R.id.btn_lihat_semua);
            layout_user = itemView.findViewById(R.id.layout_user);
        }
    }

    class Gambar3ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_user, txt_tanggal, txt_detail;
        ImageView img_user;
        ImageView img_artis1, img_artis2, img_artis3;
        TextView txt_status;
        LinearLayout btn_lihat_semua;
        RelativeLayout layout_user;

        Gambar3ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user = itemView.findViewById(R.id.txt_user);
            img_user = itemView.findViewById(R.id.img_user);
            txt_detail = itemView.findViewById(R.id.txt_detail);
            img_artis1 = itemView.findViewById(R.id.img_artis1);
            img_artis2 = itemView.findViewById(R.id.img_artis2);
            img_artis3 = itemView.findViewById(R.id.img_artis3);
            txt_status = itemView.findViewById(R.id.txt_status);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            btn_lihat_semua = itemView.findViewById(R.id.btn_lihat_semua);
            layout_user = itemView.findViewById(R.id.layout_user);
        }
    }

    class KegiatanViewHolder extends RecyclerView.ViewHolder{
        TextView txt_user, txt_tanggal, txt_detail;
        ImageView img_user;
        TextView txt_judul_kegiatan, txt_tempat_kegiatan, txt_tanggal_kegiatan, txt_deskripsi_kegiatan;
        LinearLayout btn_lihat_semua;
        RelativeLayout layout_user;

        KegiatanViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user = itemView.findViewById(R.id.txt_user);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_detail = itemView.findViewById(R.id.txt_detail);
            img_user = itemView.findViewById(R.id.img_user);
            btn_lihat_semua = itemView.findViewById(R.id.btn_lihat_semua);
            layout_user = itemView.findViewById(R.id.layout_user);
            txt_judul_kegiatan = itemView.findViewById(R.id.txt_judul_kegiatan);
            txt_tempat_kegiatan = itemView.findViewById(R.id.txt_tempat_kegiatan);
            txt_tanggal_kegiatan = itemView.findViewById(R.id.txt_tanggal_kegiatan);
            txt_deskripsi_kegiatan = itemView.findViewById(R.id.txt_deskripsi_kegiatan);
        }
    }
}
