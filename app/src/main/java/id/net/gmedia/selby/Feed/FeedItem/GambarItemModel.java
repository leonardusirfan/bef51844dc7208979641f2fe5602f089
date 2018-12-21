package id.net.gmedia.selby.Feed.FeedItem;

import java.util.Date;
import java.util.List;

import id.net.gmedia.selby.Model.ArtisModel;

public class GambarItemModel extends FeedItemModel{

    private ArtisModel artis;
    private List<String> listGambar;
    private String status = "";
    private Date timestamp;

    public GambarItemModel(ArtisModel artis, List<String> listGambar, String status, Date timestamp){
        super();

        if(listGambar.size() == 1){
            setType(TYPE_GAMBAR1);
        }
        else if(listGambar.size() == 2){
            setType(TYPE_GAMBAR2);
        }
        else{
            setType(TYPE_GAMBAR3);
        }

        this.artis = artis;
        this.listGambar = listGambar;
        this.status = status;
        this.timestamp = timestamp;
    }

    public List<String> getListGambar() {
        return listGambar;
    }

    public ArtisModel getArtis() {
        return artis;
    }

    public String getStatus() {
        return status;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
