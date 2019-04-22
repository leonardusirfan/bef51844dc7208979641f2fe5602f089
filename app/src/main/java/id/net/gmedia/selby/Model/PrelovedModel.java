package id.net.gmedia.selby.Model;

import id.net.gmedia.selby.Util.Constant;

public class PrelovedModel extends BarangModel {

    private int terpakai;
    private String satuan_terpakai;

    public PrelovedModel(String id, String nama, String url, double harga, boolean favorit, ArtisModel penjual,
                         int terpakai, String satuan_terpakai, boolean donasi){
        super(id, nama, url, harga, favorit, Constant.BARANG_PRELOVED, penjual, donasi);
        this.terpakai = terpakai;
        this.satuan_terpakai = satuan_terpakai;
    }

    public String getTerpakai(){
        return terpakai + " " + satuan_terpakai;
    }
}
