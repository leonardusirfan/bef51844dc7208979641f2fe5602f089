package id.net.gmedia.selby.Model;

public class MerchandiseModel extends BarangModel {
    public MerchandiseModel(String id, String nama, String url) {
        super(id, nama, url);
    }

    public MerchandiseModel(String id, String nama, String url, double harga) {
        super(id, nama, url, harga);
    }

    public MerchandiseModel(String id, String nama, String url, double harga, ArtisModel penjual) {
        super(id, nama, url, harga, penjual);
    }
}
