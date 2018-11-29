package id.net.gmedia.selby.Home.Feed.FeedItem;

import java.util.Date;

import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.Model.KegiatanModel;

public class KegiatanItemModel extends FeedItemModel{

    private ArtisModel artis;
    private Date timestamp;
    private KegiatanModel kegiatan;

    public KegiatanItemModel(ArtisModel artis, KegiatanModel kegiatan, Date timestamp){
        super(FeedItemModel.TYPE_KEGIATAN);

        this.artis = artis;
        this.kegiatan = kegiatan;
        this.timestamp = timestamp;
    }

    public ArtisModel getArtis() {
        return artis;
    }

    public KegiatanModel getKegiatan() {
        return kegiatan;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
