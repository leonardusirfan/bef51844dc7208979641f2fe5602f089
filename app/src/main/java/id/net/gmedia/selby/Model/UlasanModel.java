package id.net.gmedia.selby.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UlasanModel{
    private String id = "";
    private String nama;
    private String ulasan;
    private Date tanggal;

    private String url;
    private float rating = 0;
    private List<UlasanModel> listBalasan = new ArrayList<>();

    public UlasanModel(String id, String url, String nama, String ulasan, float rating, Date tanggal){
        this.id = id;
        this.url = url;
        this.nama = nama;
        this.ulasan = ulasan;
        this.tanggal = tanggal;
        this.rating = rating;
    }

    public UlasanModel(String id, String url, String nama, String ulasan, Date tanggal){
        this.id = id;
        this.url = url;
        this.nama = nama;
        this.ulasan = ulasan;
        this.tanggal = tanggal;
    }

    public UlasanModel(String url, String nama, String ulasan, Date tanggal){
        this.url = url;
        this.nama = nama;
        this.ulasan = ulasan;
        this.tanggal = tanggal;
    }

    public String getId() {
        return id;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public String getNama() {
        return nama;
    }

    public String getUlasan() {
        return ulasan;
    }

    public void addBalasan(UlasanModel balasan){
        listBalasan.add(balasan);
    }

    public List<UlasanModel> getListBalasan() {
        return listBalasan;
    }

    public String getUrl() {
        return url;
    }

    public float getRating() {
        return rating;
    }
}