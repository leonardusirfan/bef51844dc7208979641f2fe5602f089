package id.net.gmedia.selby.Upload;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Model.UploadModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.VolleyMultipartRequest;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder> {

    private FragmentActivity activity;
    private List<UploadModel> listUpload;
    private RequestQueue requestQueue;

    private String url;

    UploadAdapter(FragmentActivity activity, List<UploadModel> listUpload, String url){
        this.activity = activity;
        this.listUpload = listUpload;
        this.url = url;
        requestQueue = Volley.newRequestQueue(activity);
    }

    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UploadViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_upload, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UploadViewHolder uploadViewHolder, int i) {
        if(i == 0){
            uploadViewHolder.img_overlay.setAlpha(0f);
            uploadViewHolder.bar_loading.setVisibility(View.INVISIBLE);
            uploadViewHolder.img_artis.setImageResource(R.drawable.tambahgambar);
            uploadViewHolder.img_hapus.setVisibility(View.INVISIBLE);

            uploadViewHolder.img_artis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pix.start(activity, 999, 5);
                }
            });
        }
        else{
            uploadViewHolder.img_artis.setImageBitmap(listUpload.get(i - 1).getBitmap());
            if(listUpload.get(i - 1).isUploaded()){
                uploadViewHolder.bar_loading.setVisibility(View.INVISIBLE);
                uploadViewHolder.img_overlay.setAlpha(0f);
            }

            uploadViewHolder.img_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listUpload.remove(uploadViewHolder.getAdapterPosition() - 1);
                    notifyItemRemoved(uploadViewHolder.getAdapterPosition());
                }
            });
        }
    }

    void upload(ArrayList<String> listPath){
        for(int i = 0; i < listPath.size(); i++){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.fromFile(new File(listPath.get(i))));
                System.out.println("width : " + bitmap.getWidth() + " height : " + bitmap.getHeight());
                UploadModel u = new UploadModel(bitmap);
                listUpload.add(u);
                notifyDataSetChanged();
                uploadBitmap(u);
            }
            catch (IOException e){
                Toast.makeText(activity, "File tidak ditemukan", Toast.LENGTH_SHORT).show();
                Log.e("UploadGambar", e.toString());
            }
        }
    }

    private void uploadBitmap(final UploadModel upload){
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try{
                    JSONObject jsonresult = new JSONObject(new String(response.data));
                    int status = jsonresult.getJSONObject("metadata").getInt("status");
                    String message = jsonresult.getJSONObject("metadata").getString("message");

                    if(status == 200){
                        upload.setUploaded(true);
                        notifyDataSetChanged();
                        upload.setId(jsonresult.getJSONObject("response").getString("id"));

                        /*if(main_image.equals("")){
                            main_image = upload.getId();
                        }
                        else{
                            list_image.add(upload.getId());
                        }*/
                    }
                    else{
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e){
                    Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("UploadGambar", e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Upload gagal", Toast.LENGTH_SHORT).show();
                Log.e("UploadGambar",error.toString());
            }
        }){
            @Override
            protected Map<String, DataPart> getByteData(){
                Map<String, DataPart> params = new HashMap<>();
                long imageName = System.currentTimeMillis();
                params.put("pic", new DataPart(imageName + ".jpg", getFileDataFromDrawable(upload.getBitmap())));
                return params;
            }
        };

        requestQueue.add(volleyMultipartRequest);
    }

    boolean isAllUploaded(){
        for(UploadModel u : listUpload){
            if(!u.isUploaded()){
                return false;
            }
        }
        return true;
    }

    boolean isNoPic(){
        return listUpload.size() == 0;
    }

    public String getMainImage(){
        if(listUpload.size() == 0){
            return "";
        }
        else{
            return listUpload.get(0).getId();
        }
    }

    public List<String> getListImage(){
        List<String> listImage = new ArrayList<>();
        for(UploadModel u : listUpload){
            listImage.add(u.getId());
        }
        return listImage;
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public int getItemCount() {
        return listUpload.size() + 1;
    }

    class UploadViewHolder extends RecyclerView.ViewHolder{

        ImageView img_artis, img_overlay;
        ImageView img_hapus;
        ProgressBar bar_loading;

        UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            img_artis = itemView.findViewById(R.id.img_artis);
            img_overlay = itemView.findViewById(R.id.img_overlay);
            img_hapus = itemView.findViewById(R.id.img_hapus);
            bar_loading = itemView.findViewById(R.id.bar_loading);
        }
    }
}
