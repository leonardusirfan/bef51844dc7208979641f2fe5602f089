package id.net.gmedia.selby.Home.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.List;

import id.net.gmedia.selby.Artis.ArtisDetailActivity;
import id.net.gmedia.selby.Home.ArtisActivity;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Constant;

import com.leonardus.irfan.TopCropImageView;

public class ArtisAdapter extends RecyclerView.Adapter<ArtisAdapter.ArtisViewHolder> {

    //public static final int VIEW_THUMBNAIL = 1;

    private Activity activity;
    private Context context;
    private List<ArtisModel> listArtis;
    //private int view;

    public ArtisAdapter(Activity activity, List<ArtisModel> listArtis){
        this.activity = activity;
        //this.view = view;
        this.listArtis = listArtis;
    }

    @NonNull
    @Override
    public ArtisViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new ArtisViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artis, viewGroup, false));
        /*if(view == 0){
            return new ArtisViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artis_carousel, viewGroup, false));
        }
        else{
            return new ArtisViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artis, viewGroup, false));
        }*/
    }

    @Override
    public void onBindViewHolder(@NonNull final ArtisViewHolder artisViewHolder, int i) {
        final ArtisModel artis = listArtis.get(i);

        artisViewHolder.txt_artis.setText(artis.getNama());
        Glide.with(context).load(artis.getImage()).apply(new RequestOptions().dontAnimate().dontTransform()).
                thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).into(artisViewHolder.img_artis);
        artisViewHolder.img_artis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.getCallingActivity() == null){
                    Gson gson = new Gson();
                    Intent intent = new Intent(activity, ArtisDetailActivity.class);
                    intent.putExtra(Constant.EXTRA_ARTIS, gson.toJson(artis));

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, artisViewHolder.layout_artis, "artis");
                    context.startActivity(intent, options.toBundle());
                }
                else{
                    ((ArtisActivity)activity).sendResult(artis);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listArtis.size();
    }

    class ArtisViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_artis;
        private TopCropImageView img_artis;
        private CardView layout_artis;

        ArtisViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_artis = itemView.findViewById(R.id.layout_artis);
            txt_artis = itemView.findViewById(R.id.txt_artis);
            img_artis = itemView.findViewById(R.id.img_artis);
        }
    }
}
