package id.net.gmedia.selby.Home.Feed.FeedItem;

import java.util.Date;

import id.net.gmedia.selby.Model.ArtisModel;

public class TextItemModel extends FeedItemModel {

    private ArtisModel artis;
    private Date timestamp;
    private String status;

    public TextItemModel(ArtisModel artis, String status, Date timestamp){
        super(FeedItemModel.TYPE_TEXT);
        this.artis = artis;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public ArtisModel getArtis() {
        return artis;
    }

    public String getStatus() {
        return status;
    }
}

