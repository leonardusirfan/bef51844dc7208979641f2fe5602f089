package id.net.gmedia.selby.Chat;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;
import com.leonardus.irfan.TopCropCircularImageView;

import java.util.List;

import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Activity activity;
    private List<ChatModel> listChat;

    ChatAdapter(Activity activity, List<ChatModel> listChat){
        this.activity = activity;
        this.listChat = listChat;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {
        final ChatModel chat = listChat.get(i);

        Glide.with(activity).load(chat.getPengirim().getImage()).transition(DrawableTransitionOptions.withCrossFade()).into(chatViewHolder.img_user);
        chatViewHolder.txt_nama.setText(chat.getPengirim().getNama());
        chatViewHolder.txt_chat.setText(chat.getChat());
        chatViewHolder.txt_tanggal.setText(chat.getTanggal());
        if(chat.getBaru() > 0){
            chatViewHolder.layout_belum_terbaca.setVisibility(View.VISIBLE);
            chatViewHolder.txt_belum_terbaca.setText(String.valueOf(chat.getBaru()));
        }
        else{
            chatViewHolder.layout_belum_terbaca.setVisibility(View.INVISIBLE);
        }

        chatViewHolder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, ChatDetailActivity.class);
                i.putExtra(Constant.EXTRA_USER, gson.toJson(chat.getPengirim()));
                i.putExtra(Constant.EXTRA_CHAT_FROM, chat.getPengirim().getId());
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout_root;
        TopCropCircularImageView img_user;
        FrameLayout layout_belum_terbaca;
        TextView txt_nama, txt_chat, txt_tanggal, txt_belum_terbaca;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            img_user = itemView.findViewById(R.id.img_user);
            layout_belum_terbaca = itemView.findViewById(R.id.layout_belum_terbaca);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_chat = itemView.findViewById(R.id.txt_chat);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_belum_terbaca = itemView.findViewById(R.id.txt_belum_terbaca);
        }
    }
}
