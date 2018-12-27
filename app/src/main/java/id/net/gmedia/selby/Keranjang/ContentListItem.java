package id.net.gmedia.selby.Keranjang;

import id.net.gmedia.selby.Model.BarangModel;

public class ContentListItem extends BaseListItem {

    private BarangModel item;
    private int jumlah;
    private boolean selected;

    public ContentListItem(BarangModel item, int jumlah){
        this.item = item;
        this.jumlah = jumlah;
    }

    public BarangModel getItem() {
        return item;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void increase(){
        jumlah++;
    }

    public void decrease(){
        jumlah--;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public int getType() {
        return TYPE_CONTENT;
    }
}