package id.net.gmedia.selby.Keranjang;

import id.net.gmedia.selby.Model.BarangJualModel;

public class ContentListItem extends BaseListItem {

    private BarangJualModel item;
    private boolean selected;

    public ContentListItem(BarangJualModel item){
        this.item = item;
    }

    public BarangJualModel getItem() {
        return item;
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
