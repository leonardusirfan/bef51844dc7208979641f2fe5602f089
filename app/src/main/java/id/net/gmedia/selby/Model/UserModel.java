package id.net.gmedia.selby.Model;

public class UserModel {
    private String id;
    private String nama;

    private String image = "";

    private String alamat = "";
    private String telepon = "";
    private String email = "";

    public UserModel(String id, String nama, String image){
        this.id = id;
        this.nama = nama;
        this.image = image;
    }

    public UserModel(String id, String nama, String alamat, String telepon){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.telepon = telepon;
    }

    public UserModel(String id, String nama, String alamat, String telepon, String email){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.telepon = telepon;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
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
