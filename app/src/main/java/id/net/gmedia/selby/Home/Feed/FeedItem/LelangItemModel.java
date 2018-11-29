package id.net.gmedia.selby.Home.Feed.FeedItem;

import java.util.Date;

import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.LelangModel;

public class LelangItemModel extends FeedItemModel {
    private ArtisModel artis;
    private Date timestamp;
    private LelangModel lelang;

    public LelangItemModel(ArtisModel artis, LelangModel lelang, Date timestamp){
        super(FeedItemModel.TYPE_LELANG);
        this.artis = artis;
        this.lelang = lelang;
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public ArtisModel getArtis() {
        return artis;
    }

    public LelangModel getLelang() {
        return lelang;
    }
}
