package id.net.gmedia.selby.Model;

public class BidModel {
    private String bidder;
    private String foto;
    private double nilai;

    public BidModel(String bidder, String foto, double nilai){
        this.bidder = bidder;
        this.foto = foto;
        this.nilai = nilai;
    }

    public double getNilai() {
        return nilai;
    }

    public String getBidder() {
        return bidder;
    }

    public String getFoto() {
        return foto;
    }
}
