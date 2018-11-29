package id.net.gmedia.selby.Model;

import android.graphics.Bitmap;

public class UploadModel {

    private Bitmap bitmap;
    private boolean uploaded;
    private String id = "";

    public UploadModel(Bitmap bitmap){
        this.bitmap = bitmap;
        uploaded = false;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
}
