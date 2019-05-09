package id.net.gmedia.selby.Model;

public class OngkirModel {
    private String id;
    private String service = "";
    private String deskripsi = "";

    private double harga = 0;
    private String perkiraan_sampai = "";
    private String catatan = "";

    public OngkirModel(String id, String service){
        this.id = id;
        this.service = service;
    }

    public OngkirModel(String id, String service, String deskripsi, double harga, String perkiraan_sampai, String catatan){
        this.id = id;
        this.service = service;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.perkiraan_sampai = perkiraan_sampai;
        this.catatan = catatan;
    }

    public String getId() {
        return id;
    }

    public double getHarga() {
        return harga;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getPerkiraan_sampai() {
        return perkiraan_sampai;
    }

    public String getService() {
        return service;
    }
}
