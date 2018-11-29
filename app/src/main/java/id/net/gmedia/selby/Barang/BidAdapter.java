package id.net.gmedia.selby.Barang;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.selby.Model.BidModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.Converter;

public class BidAdapter  extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {

    private Context context;
    private List<BidModel> listBid;

    BidAdapter(List<BidModel> listBid){
        this.listBid = listBid;
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new BidViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bid, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder bidViewHolder, int i) {
        if(i == 0){
            bidViewHolder.txt_bidder.setTypeface(Typeface.DEFAULT_BOLD);
            bidViewHolder.txt_bid.setTypeface(Typeface.DEFAULT_BOLD);
            bidViewHolder.txt_bidder.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text16));
            bidViewHolder.txt_bid.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text16));

            bidViewHolder.txt_bidder.setText(R.string.user);
            bidViewHolder.txt_bid.setText(R.string.bid);
        }
        else{
            if(i % 2 != 0){
                bidViewHolder.txt_bidder.setBackgroundResource(R.color.grey);
                bidViewHolder.txt_bid.setBackgroundResource(R.color.grey);
            }

            BidModel bid = listBid.get(i - 1);

            bidViewHolder.txt_bidder.setText(bid.getBidder());
            bidViewHolder.txt_bid.setText(Converter.doubleToRupiah(bid.getNilai()));
        }
    }

    @Override
    public int getItemCount() {
        return listBid.size() + 1;
    }

    class BidViewHolder extends RecyclerView.ViewHolder{

        TextView txt_bidder, txt_bid;

        BidViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_bidder = itemView.findViewById(R.id.txt_bidder);
            txt_bid = itemView.findViewById(R.id.txt_bid);
        }
    }
}
