package id.net.gmedia.selby;

public class HotNewsModel {
    private String id;
    private String image;
    private String judul;
    private String teks;
    private String tanggal;

    public HotNewsModel(String id, String image, String judul, String teks, String tanggal){
        this.id = id;
        this.image = image;
        this.judul = judul;
        this.teks = teks;
        this.tanggal = tanggal;
    }

    public String getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getImage() {
        return image;
    }

    public String getTeks() {
        return teks;
    }
}
