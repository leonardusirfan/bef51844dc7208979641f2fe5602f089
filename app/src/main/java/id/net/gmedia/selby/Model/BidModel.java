package id.net.gmedia.selby.Model;

public class BidModel {
    private String bidder;
    private double nilai;

    public BidModel(String bidder, double nilai){
        this.bidder = bidder;
        this.nilai = nilai;
    }

    public double getNilai() {
        return nilai;
    }

    public String getBidder() {
        return bidder;
    }
}
