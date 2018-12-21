package id.net.gmedia.selby.Feed.FeedItem;

public class FeedItemModel {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_GAMBAR1 = 1;
    public static final int TYPE_GAMBAR2 = 2;
    public static final int TYPE_GAMBAR3 = 3;
    public static final int TYPE_BARANG = 4;
    public static final int TYPE_LELANG = 5;
    public static final int TYPE_KEGIATAN = 6;
    public static final int TYPE_REKOMENDASI = 7;

    private int type;

    public FeedItemModel(){

    }

    public void setType(int type) {
        this.type = type;
    }

    public FeedItemModel(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
