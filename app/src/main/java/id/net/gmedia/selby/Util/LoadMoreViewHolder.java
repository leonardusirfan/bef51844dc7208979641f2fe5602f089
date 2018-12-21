package id.net.gmedia.selby.Util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import id.net.gmedia.selby.R;

public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

    public TextView btn_load;
    public ProgressBar pb_load;

    public LoadMoreViewHolder(@NonNull View itemView) {
        super(itemView);
        btn_load = itemView.findViewById(R.id.btn_load);
        pb_load = itemView.findViewById(R.id.pb_load);
    }
}
