package id.net.gmedia.selby.Home.Akun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.net.gmedia.selby.Util.AppSharedPreferences;
import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Home.HomeActivity;
import id.net.gmedia.selby.Model.UserModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.VolleyMultipartRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAkun extends Fragment {

    public UserModel user;

    //Variabel Activity
    private Activity activity;

    //Variabel UI
    private View v;
    private ImageView img_akun, img_akun_album;
    private TextView txt_alamat, txt_telepon, txt_email, txt_akun, txt_berlangganan;

    private RequestQueue requestQueue;

    public FragmentAkun() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();

        if(v == null){
            v = inflater.inflate(R.layout.fragment_akun, container, false);

            //Inisialisasi UI
            img_akun_album = v.findViewById(R.id.img_akun_album);
            txt_alamat = v.findViewById(R.id.txt_alamat);
            txt_telepon = v.findViewById(R.id.txt_telepon);
            txt_email = v.findViewById(R.id.txt_email);
            img_akun = v.findViewById(R.id.img_akun);
            txt_akun = v.findViewById(R.id.txt_akun);
            txt_berlangganan = v.findViewById(R.id.txt_berlangganan);

            //Inisialisasi Profil
            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_PROFIL, ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            JSONObject akun = jsonresult.getJSONObject("response");

                            user = new UserModel(akun.getString("id"), akun.getString("profile_name"), akun.getString("alamat"), akun.getString("no_telp"));
                            txt_akun.setText(user.getNama());

                            String berlangganan = "Berlangganan : " + akun.getInt("jumlah_following");
                            txt_berlangganan.setText(berlangganan);

                            Glide.with(activity).load(akun.getString("sampul")).transition(DrawableTransitionOptions.withCrossFade()).apply(new RequestOptions().placeholder(R.color.grey)).thumbnail(0.5f).into(img_akun_album);
                            Glide.with(activity)
                                    .load(akun.getString("foto"))
                                    .thumbnail(0.2f)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .apply(new RequestOptions().circleCrop())
                                    .into(img_akun);
                            txt_telepon.setText(user.getTelepon());
                            txt_email.setText(akun.getString("email"));
                            txt_alamat.setText(user.getAlamat());
                        }
                        else{
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Log.e("Akun", e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Log.e("Akun", result);
                }
            });

            img_akun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pix.start((FragmentActivity) activity, 999, 1);
                }
            });

            img_akun_album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pix.start((FragmentActivity) activity, 998, 1);
                }
            });

            //Profil User Local
            /*if(FirebaseAuth.getInstance().getCurrentUser() != null){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                txt_akun.setText(user.getDisplayName());
                Glide.with(activity).load(user.getPhotoUrl()).transition(DrawableTransitionOptions.withCrossFade()).apply(new RequestOptions().placeholder(R.color.grey)).thumbnail(0.5f).into(img_akun_album);
                Glide.with(activity)
                        .load(user.getPhotoUrl())
                        .thumbnail(0.2f)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(new RequestOptions().circleCrop())
                        .into(img_akun);
                txt_telepon.setText(user.getPhoneNumber());
                txt_email.setText(user.getEmail());
            }*/

            v.findViewById(R.id.txt_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Log Out");
                    builder.setMessage("Yakin ingin keluar Selby?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Log out pengguna
                            FirebaseAuth.getInstance().signOut();
                            AppSharedPreferences.setLoggedIn(getContext(), false);
                            Intent i = new Intent(getContext(), HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create().show();
                }
            });
        }
        return v;
    }

    public void upload(String path, final boolean sampul){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(activity);
        }

        String url = Constant.URL_UPLOAD_FOTO_PROFIL;
        if(sampul){
            url += "?flag=2";
        }
        else{
            url += "?flag=1";
        }

        try{
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.fromFile(new File(path)));

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try{
                        JSONObject jsonresult = new JSONObject(new String(response.data));
                        System.out.println(jsonresult);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            Toast.makeText(activity, "Foto berhasil diubah", Toast.LENGTH_SHORT).show();
                            if(sampul){
                                Glide.with(activity).load(bitmap).transition(DrawableTransitionOptions.withCrossFade()).apply(new RequestOptions().placeholder(R.color.grey)).thumbnail(0.5f).into(img_akun_album);
                            }
                            else{
                                Glide.with(activity)
                                        .load(bitmap)
                                        .thumbnail(0.2f)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .apply(new RequestOptions().circleCrop())
                                        .into(img_akun);
                            }

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
                    params.put("pic", new DataPart(imageName + ".jpg", getFileDataFromDrawable(bitmap)));
                    return params;
                }
            };

            requestQueue.add(volleyMultipartRequest);
        }
        catch (IOException e){
            Toast.makeText(activity, "File tidak ditemukan", Toast.LENGTH_SHORT).show();
            Log.e("UploadGambar", e.toString());
        }
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
