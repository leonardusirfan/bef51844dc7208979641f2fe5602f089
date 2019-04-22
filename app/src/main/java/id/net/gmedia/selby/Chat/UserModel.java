package id.net.gmedia.selby.Chat;

public class UserModel {
    private String id;
    private String nama;
    private String image;

    public UserModel(String id, String nama, String image){
        this.id = id;
        this.nama = nama;
        this.image = image;
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
}
