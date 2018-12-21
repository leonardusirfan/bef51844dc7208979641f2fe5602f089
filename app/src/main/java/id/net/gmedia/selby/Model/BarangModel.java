package id.net.gmedia.selby.Model;

public class BarangModel {
    private String id;
    private String nama;
    private String url;
    private double harga = 0;
    private boolean favorit = false;

    private int jumlah = 0;

    private String jenis;
    private ArtisModel penjual;

    public BarangModel(String id, String nama, String url){
        this.id = id;
        this.nama = nama;
        this.url = url;
    }

    public BarangModel(String id, String nama, String url, String jenis){
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

    public BarangModel(String id, String nama, String url, double harga, boolean favorit, String jenis){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
        this.jenis = jenis;
    }

    public BarangModel(String id, String nama, String url, double harga, boolean favorit, String jenis, ArtisModel penjual){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
        this.jenis = jenis;
        this.penjual = penjual;
    }

    public BarangModel(String id, String nama, String url, double harga, String jenis, ArtisModel penjual){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.jenis = jenis;
        this.penjual = penjual;
    }


    public BarangModel(String id, String nama, String url, double harga, boolean favorit){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
    }

    public BarangModel(String id, String nama, String url, double harga, boolean favorit, ArtisModel penjual){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.favorit = favorit;
        this.penjual = penjual;
    }

    public String getJenis() {
        return jenis;
    }

    public ArtisModel getPenjual() {
        return penjual;
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
