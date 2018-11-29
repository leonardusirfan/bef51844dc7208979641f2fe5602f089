package id.net.gmedia.selby.Model;

public class UserModel {
    private String id;
    private String nama;
    private String alamat;
    private String telepon;

    public UserModel(String id, String nama, String alamat, String telepon){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.telepon = telepon;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getTelepon() {
        return telepon;
    }
}
