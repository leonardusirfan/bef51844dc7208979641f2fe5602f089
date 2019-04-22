package id.net.gmedia.selby.Keranjang;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.Converter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.R;


public class KeranjangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Fragment fragment;
    private Context context;
    private List<BaseListItem> listItem;

    KeranjangAdapter(Fragment fragment, List<BaseListItem> listItem){
        this.fragment = fragment;
        this.listItem = listItem;
    }

    @Override
    public int getItemViewType(int position) {
        return listItem.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == BaseListItem.TYPE_HEADER){
            return new HeaderViewHolder(inflater.inflate(R.layout.item_keranjang_header, parent, false));
        }
        else if(viewType == BaseListItem.TYPE_CONTENT){
            return new ContentViewHolder(inflater.inflate(R.layout.item_keranjang_content, parent, false));
        }
        else{
            return new DividerViewHolder(inflater.inflate(R.layout.view_divider_gap, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        //Untuk HEADER
        if(holder.getItemViewType() == BaseListItem.TYPE_HEADER){
            final HeaderListItem item = (HeaderListItem) listItem.get(position);
            final HeaderViewHolder viewHolder =  ((HeaderViewHolder) holder);
            viewHolder.txt_keranjang_header.setText(item.getPelapak().getNama());

            //Mengubah kondisi checkbox berdasarkan kondisi BaseListItem
            if (item.isSelected()) {
                viewHolder.cb_keranjang_header.setChecked(true);
            } else {
                viewHolder.cb_keranjang_header.setChecked(false);
            }

            viewHolder.cb_keranjang_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = 1;
                    if(viewHolder.cb_keranjang_header.isChecked()){
                        //menyalakan checkbox semua item dalam pelapak yang sama
                        item.setSelected(true);
                        for(int i = viewHolder.getAdapterPosition() + 1; listItem.get(i).getType() != BaseListItem.TYPE_DIVIDER; i++){
                            ((ContentListItem)listItem.get(i)).setSelected(true);
                            count++;
                        }
                    }
                    else{
                        //mematikan checkbox semua item dalam pelapak yang sama
                        item.setSelected(false);
                        for(int i = viewHolder.getAdapterPosition() + 1; listItem.get(i).getType() != BaseListItem.TYPE_DIVIDER; i++){
                            ((ContentListItem)listItem.get(i)).setSelected(false);
                            count++;
                        }

                        //Mematikan checkbox select all
                        ((FragmentKeranjang)fragment).cb_keranjang.setChecked(false);
                    }

                    ((FragmentKeranjang)fragment).updateView();
                    notifyItemRangeChanged(viewHolder.getAdapterPosition(), count);
                }
            });
        }
        //untuk CONTENT
        else if(holder.getItemViewType() == BaseListItem.TYPE_CONTENT){
            final ContentListItem item = (ContentListItem) listItem.get(position);
            final ContentViewHolder viewholder = (ContentViewHolder) holder;

            viewholder.txt_nama.setText(item.getItem().getNama());
            viewholder.txt_harga.setText(Converter.doubleToRupiah(item.getItem().getHarga()));
            viewholder.txt_jumlah.setText(String.valueOf(item.getItem().getJumlah()));

            Glide.with(context).load(item.getItem().getUrl()).transition(DrawableTransitionOptions.withCrossFade()).
                    thumbnail(0.3f).into((viewholder.img_item));

            viewholder.txt_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.getItem().getJumlah() < 20){
                        item.getItem().increase();
                        viewholder.txt_jumlah.setText(String.valueOf(item.getItem().getJumlah()));
                        if(viewholder.cb_item.isChecked()){
                            ((FragmentKeranjang)fragment).updateView();
                        }
                    }
                }
            });

            viewholder.txt_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.getItem().getJumlah() > 1){
                        item.getItem().decrease();
                        viewholder.txt_jumlah.setText(String.valueOf(item.getItem().getJumlah()));
                        if(viewholder.cb_item.isChecked()){
                            ((FragmentKeranjang)fragment).updateView();
                        }
                    }

                }
            });

            //Mengubah kondisi checkbox berdasarkan kondisi BaseListItem
            if (item.isSelected()) {
                viewholder.cb_item.setChecked(true);
            } else {
                viewholder.cb_item.setChecked(false);
            }

            viewholder.cb_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!viewholder.cb_item.isChecked()){
                        item.setSelected(false);
                        int i = viewholder.getAdapterPosition();
                        while(listItem.get(i).getType() != BaseListItem.TYPE_HEADER){
                            i--;
                        }
                        ((HeaderListItem)listItem.get(i)).setSelected(false);
                        notifyItemChanged(i);
                        ((FragmentKeranjang)fragment).cb_keranjang.setChecked(false);
                    }
                    else{
                        item.setSelected(true);
                    }

                    ((FragmentKeranjang)fragment).updateView();
                    notifyItemChanged(viewholder.getAdapterPosition());
                }
            });

            viewholder.btn_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        List<String> listId = new ArrayList<>();
                        listId.add(item.getItem().getId());

                        JSONArray list = new JSONArray(listId);
                        JSONObject body = new JSONObject();
                        body.put("id_keranjang", list);
                        System.out.println("List ID : " + list);

                        ApiVolleyManager.getInstance().addRequest(context, Constant.URL_HAPUS_KERANJANG,
                                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                                body, new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try{
                                    JSONObject json = new JSONObject(result);
                                    int status = json.getJSONObject("metadata").getInt("status");
                                    String message = json.getJSONObject("metadata").getString("message");

                                    if(status == 200){
                                        ((FragmentKeranjang)fragment).setLoad();
                                        ((FragmentKeranjang)fragment).resetFragment();
                                    }
                                    else{
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e){
                                    Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    catch (JSONException e){
                        Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listItem != null ? listItem.size() : 0;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        CheckBox cb_keranjang_header;
        TextView txt_keranjang_header;

        HeaderViewHolder(View itemView) {
            super(itemView);
            txt_keranjang_header = itemView.findViewById(R.id.txt_keranjang_header);
            cb_keranjang_header = itemView.findViewById(R.id.cb_keranjang_header);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder{

        ImageView img_item, btn_hapus;
        TextView txt_nama, txt_harga, txt_minus, txt_plus, txt_jumlah;
        CheckBox cb_item;

        ContentViewHolder(View itemView) {
            super(itemView);
            img_item = itemView.findViewById(R.id.img_item);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_minus = itemView.findViewById(R.id.txt_minus);
            txt_plus = itemView.findViewById(R.id.txt_plus);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            cb_item = itemView.findViewById(R.id.cb_item);
            btn_hapus = itemView.findViewById(R.id.btn_hapus);
        }
    }

    class DividerViewHolder extends RecyclerView.ViewHolder{

        DividerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
