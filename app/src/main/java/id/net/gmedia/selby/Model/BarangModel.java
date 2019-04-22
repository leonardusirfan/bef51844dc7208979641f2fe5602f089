package id.net.gmedia.selby.Model;

import android.graphics.Bitmap;

public class BarangModel {
    private String id;
    private String nama;
    private String url;
    private double harga = 0;
    private boolean favorit = false;

    private Bitmap imgBitmap;

    private int jenis;
    private ArtisModel penjual;

    private boolean donasi = false;

    public BarangModel(String id, String nama, String url){
        this.id = id;
        this.nama = nama;
        this.url = url;
    }

    public BarangModel(String id, String nama, String url, int jenis){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.jenis = jenis;
    }

    public BarangModel(String id, String nama, String url, double harga){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
    }

    public BarangModel(String id, String nama, String url, double harga, ArtisModel penjual){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.penjual = penjual;
    }

    public BarangModel(String id, String nama, String url, double harga, boolean favorit, int jenis){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
        this.jenis = jenis;
    }

    public BarangModel(String id, String nama, String url, double harga, boolean favorit, int jenis, ArtisModel penjual, boolean donasi){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
        this.jenis = jenis;
        this.penjual = penjual;
        this.donasi = donasi;
    }

    public BarangModel(String id, String nama, String url, double harga, int jenis, ArtisModel penjual, boolean donasi){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.jenis = jenis;
        this.penjual = penjual;
        this.donasi = donasi;
    }


    public BarangModel(String id, String nama, String url, double harga, boolean favorit){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
    }

    /*public BarangModel(String id, String nama, String url, double harga, boolean favorit, int terpakai, String satuan_terpakai, ArtisModel penjual){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
        this.penjual = penjual;
        this.terpakai = terpakai;
        this.satuan_terpakai = satuan_terpakai;
    }*/

    public BarangModel(String id, String nama, String url, double harga, boolean favorit, ArtisModel penjual, boolean donasi){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
        this.penjual = penjual;
    }

    public BarangModel(String id, String nama, double harga, boolean favorit, ArtisModel penjual){
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.favorit = favorit;
        this.penjual = penjual;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public int getJenis() {
        return jenis;
    }

    public ArtisModel getPenjual() {
        return penjual;
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

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public boolean isDonasi() {
        return donasi;
    }

    public void setDonasi(boolean donasi) {
        this.donasi = donasi;
    }

    /*public String getTerpakai(){
        return terpakai + " " + satuan_terpakai;
    }*/
}
