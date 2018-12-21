package id.net.gmedia.selby.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LelangModel {
    private String id;
    private String nama;
    private String url;

    private double harga = 0;
    private double hargaNormal = 0;
    private Date tglSelesai = new Date();

    private List<BidModel> listBid = new ArrayList<>();
    private List<String> gallery = new ArrayList<>();

    private ArtisModel penjual;

    public LelangModel(String id, String nama, String url){
        this.id = id;
        this.nama = nama;
        this.url = url;
    }

    public LelangModel(String id, String nama, String url, double harga, double hargaNormal, Date tglSelesai){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.hargaNormal = hargaNormal;
        this.tglSelesai = tglSelesai;
    }

    public LelangModel(String id, String nama, String url, double harga, double hargaNormal, ArtisModel penjual, Date tglSelesai){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.hargaNormal = hargaNormal;
        this.tglSelesai = tglSelesai;
        this.penjual = penjual;
    }

    public ArtisModel getPenjual() {
        return penjual;
    }

    public String getId() {
        return id;
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

    public String getNama() {
        return nama;
    }

    public String getUrl() {
        return url;
    }

    public double getHarga() {
        return harga;
    }

    public void bid(BidModel bid){
        this.harga = bid.getNilai();
        listBid.add(bid);
    }

    public Date getTglSelesai() {
        return tglSelesai;
    }

    public List<BidModel> getListBid() {
        return listBid;
    }
}
