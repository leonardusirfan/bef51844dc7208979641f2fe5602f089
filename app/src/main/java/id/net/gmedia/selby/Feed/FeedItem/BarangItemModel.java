package id.net.gmedia.selby.Feed.FeedItem;

import java.util.Date;
import java.util.List;

import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.BarangModel;

public class BarangItemModel extends FeedItemModel {

    private ArtisModel artis;
    private Date timestamp;
    private List<BarangModel> listBarang;

    public BarangItemModel(ArtisModel artis, List<BarangModel> listBarang, Date timestamp){
        super(FeedItemModel.TYPE_BARANG);
        this.artis = artis;
        this.listBarang = listBarang;
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public ArtisModel getArtis() {
        return artis;
    }

    public List<BarangModel> getListBarang() {
        return listBarang;
    }
}
