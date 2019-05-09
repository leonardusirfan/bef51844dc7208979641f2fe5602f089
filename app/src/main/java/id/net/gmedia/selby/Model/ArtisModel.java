package id.net.gmedia.selby.Model;

import android.graphics.Bitmap;

public class ArtisModel {

    private String id = "";
    private String nama = "";
    private String image = "";

    private Bitmap bitmap;

    private String tempat_lahir = "";
    private String tgl_lahir = "";
    private int tinggi = 0;
    private String deskripsi = "";

    private float rating = 0;

    private String id_kota = "";

    public ArtisModel(String nama){
        this.nama = nama;
    }

    public ArtisModel(String nama, String image, float rating){
        this.nama = nama;
        this.image = image;
        this.rating = rating;
    }

    public ArtisModel(String id, String nama, String image){
        this.id = id;
        this.nama = nama;
        this.image = image;
    }

    public ArtisModel(String id, String nama, String image, String id_kota){
        this.id = id;
        this.nama = nama;
        this.image = image;
        this.id_kota = id_kota;
    }

    public ArtisModel(String id, String nama, String image, String tempat_lahir,
                      String tgl_lahir, int tinggi, String deskripsi){
        this.id = id;
        this.nama = nama;
        this.image = image;
        this.tempat_lahir = tempat_lahir;
        this.tgl_lahir = tgl_lahir;
        this.tinggi = tinggi;
        this.deskripsi = deskripsi;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getImage() {
        return image;
    }

    public int getTinggi() {
        return tinggi;
    }

    public String getTempat_lahir() {
        return tempat_lahir;
    }

    public String getTgl_lahir() {
        return tgl_lahir;
    }

    public String getDeskripsi(){
        return deskripsi;
    }

    public float getRating() {
        return rating;
    }

    public String getId_kota() {
        return id_kota;
    }
}