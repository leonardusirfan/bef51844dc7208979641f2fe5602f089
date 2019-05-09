package id.net.gmedia.selby.Model;

import android.support.annotation.NonNull;

public class AlamatModel {
    private String id;
    private String label;
    private String penerima;
    private String telepon;

    private String id_kota;
    private String nama_kota;
    private String id_provinsi;
    private String nama_provinsi;
    private String alamat;

    private String kodepos;

    public AlamatModel(String id, String label, String penerima, String telepon, String id_kota, String nama_kota,
                       String id_provinsi, String nama_provinsi, String alamat, String kodepos){
        this.id = id;
        this.label = label;
        this.penerima = penerima;
        this.telepon = telepon;
        this.id_kota = id_kota;
        this.nama_kota = nama_kota;
        this.alamat = alamat;
        this.id_provinsi = id_provinsi;
        this.nama_provinsi = nama_provinsi;
        this.kodepos = kodepos;
    }

    public String getId() {
        return id;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getNama_kota() {
        return nama_kota;
    }

    public String getNama_provinsi() {
        return nama_provinsi;
    }

    public String getId_kota() {
        return id_kota;
    }

    public String getId_provinsi() {
        return id_provinsi;
    }

    public String getKodepos() {
        return kodepos;
    }

    public String getLabel() {
        return label;
    }

    public String getPenerima() {
        return penerima;
    }

    public String getTelepon() {
        return telepon;
    }

    @NonNull
    @Override
    public String toString() {
        return alamat;
    }
}
