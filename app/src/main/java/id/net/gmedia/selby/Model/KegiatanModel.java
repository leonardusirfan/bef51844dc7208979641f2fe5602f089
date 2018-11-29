package id.net.gmedia.selby.Model;

import java.util.Date;

public class KegiatanModel {
    private String judul;
    private String tempat;
    private Date tanggal;
    private String deskripsi;

    public KegiatanModel(String judul, String tempat, Date tanggal, String deskripsi){
        this.judul = judul;
        this.tempat = tempat;
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
    }

    public String getJudul() {
        return judul;
    }

    public String getTempat() {
        return tempat;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
}
