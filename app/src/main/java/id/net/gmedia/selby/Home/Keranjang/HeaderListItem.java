package id.net.gmedia.selby.Home.Keranjang;

import id.net.gmedia.selby.Model.ArtisModel;

public class HeaderListItem extends BaseListItem {

    private boolean selected;
    private ArtisModel pelapak;

    public HeaderListItem(ArtisModel pelapak){
        this.pelapak = pelapak;
    }

    public ArtisModel getPelapak(){
        return pelapak;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }
}
