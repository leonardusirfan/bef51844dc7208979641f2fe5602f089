package id.net.gmedia.selby.Chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.selby.R;

public class ChatDetailAdapter extends RecyclerView.Adapter<ChatDetailAdapter.ChatDetailViewHolder> {

    private Context context;
    private List<ChatDetailModel> listChat;

    ChatDetailAdapter(List<ChatDetailModel> listChat){
        this.listChat = listChat;
    }

    @NonNull
    @Override
    public ChatDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new ChatDetailViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatDetailViewHolder chatDetailViewHolder, int i) {
        ChatDetailModel chat = listChat.get(i);
        int margin = context.getResources().getDimensionPixelOffset(R.dimen.dp20);

        if(chat.isPengirim()){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;

            chatDetailViewHolder.txt_chat.setLayoutParams(params);
            chatDetailViewHolder.txt_chat.setBackgroundResource(R.drawable.chat_white);
            //chatDetailViewHolder.txt_chat.setBackgroundResource(R.drawable.bubble_white);
        }
        else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.START;

            chatDetailViewHolder.txt_chat.setLayoutParams(params);
            chatDetailViewHolder.txt_chat.setBackgroundResource(R.drawable.chat_blue);
            //chatDetailViewHolder.txt_chat.setBackgroundResource(R.drawable.bubble_blue);
        }

        chatDetailViewHolder.txt_chat.setText(chat.getChat());
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    class ChatDetailViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout_root;
        TextView txt_chat;


        ChatDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_chat = itemView.findViewById(R.id.txt_chat);
        }
    }
}

