package id.net.gmedia.selby.Barang.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.selby.Model.BidModel;
import id.net.gmedia.selby.R;
import com.leonardus.irfan.TopCropCircularImageView;

public class BidAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int HEADER_TYPE = 999;

    private Context context;
    private List<BidModel> listBid;

    public BidAdapter(List<BidModel> listBid){
        this.listBid = listBid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        if(i == HEADER_TYPE){
            return new BidHeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bid_text, viewGroup, false));
        }
        else{
            return new BidViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bid, viewGroup, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return HEADER_TYPE;
        }
        else{
            return super.getItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder bidViewHolder, int i) {
        if(bidViewHolder instanceof BidHeaderViewHolder){
            ((BidHeaderViewHolder)bidViewHolder).txt_bidder.setTypeface(Typeface.DEFAULT_BOLD);
            ((BidHeaderViewHolder)bidViewHolder).txt_bid.setTypeface(Typeface.DEFAULT_BOLD);
            ((BidHeaderViewHolder)bidViewHolder).txt_bidder.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text16));
            ((BidHeaderViewHolder)bidViewHolder).txt_bid.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text16));

            ((BidHeaderViewHolder)bidViewHolder).txt_bidder.setText(R.string.user);
            ((BidHeaderViewHolder)bidViewHolder).txt_bid.setText(R.string.lelang_bid);
        }
        else{
            BidModel bid = listBid.get(i - 1);

            //((BidViewHolder)bidViewHolder).txt_bidder.setText(bid.getBidder());
            ((BidViewHolder)bidViewHolder).txt_bid.setText(Converter.doubleToRupiah(bid.getNilai()));
            Glide.with(context).load(bid.getFoto()).apply(new RequestOptions()).into(((BidViewHolder)bidViewHolder).img_bidder);
        }
    }

    @Override
    public int getItemCount() {
        return listBid.size() + 1;
    }

    class BidViewHolder extends RecyclerView.ViewHolder{

        TopCropCircularImageView img_bidder;
        TextView txt_bid;

        BidViewHolder(@NonNull View itemView) {
            super(itemView);
            img_bidder = itemView.findViewById(R.id.img_bidder);
            txt_bid = itemView.findViewById(R.id.txt_bid);
        }
    }

    class BidHeaderViewHolder extends RecyclerView.ViewHolder{

        TextView txt_bidder, txt_bid;

        BidHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_bidder = itemView.findViewById(R.id.txt_bidder);
            txt_bid = itemView.findViewById(R.id.txt_bid);
        }
    }
}
