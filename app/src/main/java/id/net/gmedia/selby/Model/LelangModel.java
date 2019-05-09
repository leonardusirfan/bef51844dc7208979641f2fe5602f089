package id.net.gmedia.selby.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LelangModel extends BarangModel{
    private double hargaNormal = 0;
    private Date tglSelesai = new Date();

    private List<BidModel> listBid = new ArrayList<>();
    private List<String> gallery = new ArrayList<>();

    public LelangModel(String id, String nama, String url){
        super(id, nama, url);
    }

    public LelangModel(String id, String nama, String url, double harga, double hargaNormal, Date tglSelesai){
        super(id, nama, url, harga);
        this.hargaNormal = hargaNormal;
        this.tglSelesai = tglSelesai;
    }

    public LelangModel(String id, String nama, String url, double harga, double hargaNormal, ArtisModel penjual, Date tglSelesai, boolean donasi){
        super(id, nama, url, harga, penjual, donasi);
        this.hargaNormal = hargaNormal;
        this.tglSelesai = tglSelesai;
    }

    public void setGallery(List<String> gallery) {
        this.gallery = gallery;
    }

    public double getHargaNormal() {
        return hargaNormal;
    }

    public List<String> getGallery() {
        return gallery;
    }

    public void bid(BidModel bid){
        super.setHarga(bid.getNilai());
        listBid.add(bid);
    }

    public Date getTglSelesai() {
        return tglSelesai;
    }

    public List<BidModel> getListBid() {
        return listBid;
    }
}
