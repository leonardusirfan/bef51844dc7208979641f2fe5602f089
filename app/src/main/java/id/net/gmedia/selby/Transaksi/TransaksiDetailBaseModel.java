package id.net.gmedia.selby.Transaksi;

import id.net.gmedia.selby.Model.BarangJualModel;

public class TransaksiDetailBaseModel {
    static final int BARANG = 900;
    static final int KURIR = 800;

    private int type;
    private BarangJualModel item;

    TransaksiDetailBaseModel(BarangJualModel item, int type){
        this.item = item;
        this.type = type;
    }

    public BarangJualModel getItem() {
        return item;
    }

    public int getType() {
        return type;
    }
}
