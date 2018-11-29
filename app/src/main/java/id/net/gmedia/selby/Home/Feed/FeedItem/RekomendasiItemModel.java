package id.net.gmedia.selby.Home.Feed.FeedItem;

import java.util.List;

import id.net.gmedia.selby.Model.ArtisModel;

public class RekomendasiItemModel extends FeedItemModel {

    private List<ArtisModel> listArtis;

    public RekomendasiItemModel(List<ArtisModel> listArtis){
        super(FeedItemModel.TYPE_REKOMENDASI);
        this.listArtis = listArtis;
    }

    public List<ArtisModel> getListArtis() {
        return listArtis;
    }
}
