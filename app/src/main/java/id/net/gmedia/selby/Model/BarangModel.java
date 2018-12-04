package id.net.gmedia.selby.Model;

import java.util.ArrayList;
import java.util.List;

public class BarangModel {
    private String id;
    private String nama;
    private String url;
    private double harga = 0;
    private boolean favorit = false;

    private int jumlah = 0;

    public BarangModel(String id, String nama, String url){
        this.id = id;
        this.nama = nama;
        this.url = url;
    }

    public BarangModel(String id, String nama, String url, double harga){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
    }

    public BarangModel(String id, String nama, String url, double harga, boolean favorit){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getUrl() {
        return url;
    }

    public void setFavorit(boolean favorit) {
        this.favorit = favorit;
    }

    public boolean isFavorit() {
        return favorit;
    }

    public double getHarga() {
        return harga;
    }
}
