package id.net.gmedia.selby.Home.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import id.net.gmedia.selby.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    private List<String> listImage;

    public SliderAdapter(List<String> listImage){
        this.listImage = listImage;
    }

    @NonNull
    @Override
    public SliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new SliderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rounded_slider, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.SliderViewHolder sliderViewHolder, int i) {
        Glide.with(context).load(listImage.get(i)).into(sliderViewHolder.img_slider);
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{

        ImageView img_slider;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            img_slider = itemView.findViewById(R.id.img_slider);
        }
    }
}
