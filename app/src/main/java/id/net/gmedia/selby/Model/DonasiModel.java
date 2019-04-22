package id.net.gmedia.selby.Model;

public class DonasiModel extends BarangModel{
    private String tujuan_donasi;
    private float persenan_donasi;

    public DonasiModel(String id, String nama, String url, double harga , boolean favorit, int jenis, ArtisModel penjual) {
        super(id, nama, url, harga, favorit, jenis, penjual, true);
    }

    public String getTujuan_donasi() {
        return tujuan_donasi;
    }

    public float getPersenan_donasi() {
        return persenan_donasi;
    }
}
