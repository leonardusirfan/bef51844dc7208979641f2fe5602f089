package id.net.gmedia.selby.Model;

public class ArtisModel {

    private String id = "";
    private String nama = "";
    private String image = "";

    private String tempat_lahir = "";
    private String tgl_lahir = "";
    private int tinggi = 0;
    private String deskripsi = "";

    private float rating = 0;

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

    public ArtisModel(String id, String nama, String image, String tempat_lahir, String tgl_lahir, int tinggi, String deskripsi){
        this.id = id;
        this.nama = nama;
        this.image = image;
        this.tempat_lahir = tempat_lahir;
        this.tgl_lahir = tgl_lahir;
        this.tinggi = tinggi;
        this.deskripsi = deskripsi;
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
}
