package id.net.gmedia.selby.Barang.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.List;

import id.net.gmedia.selby.R;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private List<Integer> listRating;
    private float sum;

    public RatingAdapter(List<Integer> listRating){
        this.listRating = listRating;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RatingViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rating, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder ratingViewHolder, int i) {
        int count = listRating.get(5 - (i + 1));

        ratingViewHolder.rate_ulasan.setRating(5 - i);
        ratingViewHolder.progress_ulasan.setProgress(Math.round(((float)count)/sum*100));
        ratingViewHolder.txt_ulasan.setText(String.valueOf(count));
    }

    /*public void calculateSum(){
        sum = 0;
        for(int s = 0; s < listRating.size(); s++){
            sum += listRating.get(s) * (s + 1);
        }
    }*/

    public void setSum(float sum){
        this.sum = sum;
    }

    @Override
    public int getItemCount() {
        return listRating.size();
    }

    class RatingViewHolder extends RecyclerView.ViewHolder{

        private AppCompatRatingBar rate_ulasan;
        private ProgressBar progress_ulasan;
        private TextView txt_ulasan;

        RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            rate_ulasan = itemView.findViewById(R.id.rate_ulasan);
            progress_ulasan = itemView.findViewById(R.id.progress_ulasan);
            txt_ulasan = itemView.findViewById(R.id.txt_ulasan);
        }
    }
}
