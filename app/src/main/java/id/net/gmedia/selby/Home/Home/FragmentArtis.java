package id.net.gmedia.selby.Home.Home;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.azoft.carousellayoutmanager.ItemTransformation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.Util.Constant;
import id.net.gmedia.selby.Model.ArtisModel;
import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentArtis extends Fragment {

    private View v;
    private boolean view_slider = true;
    private boolean loading = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private int loaded = 0;
    private boolean canLoad = true;
    private boolean needLoad = true;

    private Activity activity;
    private ImageView img_artis1, img_artis2, img_overlay, used;
    private RecyclerView rv_artis;
    private ArtisAdapter artisAdapter;
    private CarouselLayoutManager carouselView;
    private List<ArtisModel> listArtis = new ArrayList<>();

    public FragmentArtis() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(v == null || needLoad){
            v = inflater.inflate(R.layout.fragment_artis, container, false);

            activity = getActivity();

            //Inisialisasi UI
            img_artis1 = v.findViewById(R.id.img_artis1);
            img_artis2 = v.findViewById(R.id.img_artis2);
            img_overlay = v.findViewById(R.id.img_overlay);
            rv_artis = v.findViewById(R.id.rv_artis);

            initCarouselView();

            //Inisialisasi Recycler View
            artisAdapter = new ArtisAdapter(activity, listArtis, ArtisAdapter.VIEW_CAROUSEL);
            rv_artis.setItemAnimator(new DefaultItemAnimator());
            rv_artis.setLayoutManager(carouselView);
            rv_artis.addOnScrollListener(new CenterScrollListener());
            rv_artis.setAdapter(artisAdapter);

            loadArtis();
        }
        return v;
    }

    public boolean changeView(){
        if(view_slider){
            view_slider = false;

            img_overlay.setAlpha(1f);
            final GridLayoutManager mLayoutManager = new GridLayoutManager(activity, 3, LinearLayoutManager.VERTICAL, false);
            rv_artis.setLayoutManager(mLayoutManager);
            rv_artis.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(canLoad){
                        if (dy > 0) { // scroll down
                            visibleItemCount = mLayoutManager.getChildCount();
                            totalItemCount = mLayoutManager.getItemCount();
                            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                            if (!loading && (totalItemCount - visibleItemCount <= pastVisiblesItems)) {
                                loading = true;
                                loadArtis();
                            }
                        }
                    }
                }
            });
            artisAdapter = new ArtisAdapter(activity, listArtis, ArtisAdapter.VIEW_THUMBNAIL);
            rv_artis.setAdapter(artisAdapter);
        }
        else{
            view_slider = true;

            img_overlay.setAlpha(0.5f);
            rv_artis.setLayoutManager(carouselView);
            artisAdapter = new ArtisAdapter(activity, listArtis, ArtisAdapter.VIEW_CAROUSEL);
            rv_artis.setAdapter(artisAdapter);
        }
        return view_slider;
    }

    private void initCarouselView(){
        carouselView = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false);
        carouselView.setPostLayoutListener(new CarouselZoomPostLayoutListener() {
            @Override
            public ItemTransformation transformChild(@NonNull View child, float itemPositionToCenterDiff, int orientation) {
                //Pengatur ukuran
                float scale = (float) (2 * (2 * -StrictMath.atan(Math.abs(itemPositionToCenterDiff) + 1.0) / Math.PI + 1));
                float translateY;
                float translateX;
                //Pengatur jarak
                if (CarouselLayoutManager.VERTICAL == orientation) {
                    final float translateYGeneral = child.getMeasuredHeight() * (1 - scale) * 1.75f;
                    translateY = Math.signum(itemPositionToCenterDiff) * translateYGeneral;
                    translateX = 0;
                } else {
                    final float translateXGeneral = child.getMeasuredWidth() * (1 - scale) * 1.75f;
                    translateX = Math.signum(itemPositionToCenterDiff) * translateXGeneral;
                    translateY = 0;
                }
                //CommonHelper.log("itemPositionToCenterDiff: " + itemPositionToCenterDiff + ", scale: " + scale + ", x: " + translateX + ",y: " + translateY);
                //scale = 1;
                if (-0.5 < itemPositionToCenterDiff && itemPositionToCenterDiff < 0.5) {
                    child.setAlpha(1f);
                } else {
                    child.setAlpha(0.5f);
                }
                return new ItemTransformation(scale, scale, translateX, translateY);
            }
        });
    }

    public boolean isView_slider(){
        return view_slider;
    }

    private void loadArtis(){
        final int LOADING_COUNT = 9;

        try{
            JSONObject body = new JSONObject();
            body.put("start", loaded);
            body.put("count", LOADING_COUNT);
            body.put("id", "");

            ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_ARTIS, ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body, new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);

                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            needLoad = false;
                            JSONArray array = jsonresult.getJSONObject("response").getJSONArray("pelapak");
                            int total_count = jsonresult.getJSONObject("response").getInt("total_records");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject artis = array.getJSONObject(i);
                                listArtis.add(new ArtisModel(artis.getString("id"), artis.getString("nama"), artis.getString("image"), "Amerika Serikat","2 juni 1995", 167, artis.getString("deskripsi")));
                            }
                            artisAdapter.notifyDataSetChanged();

                            if(loaded == 0){
                                //Inisialisasi carousel
                                carouselView.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {
                                    @Override
                                    public void onCenterItemChanged(int adapterPosition) {
                                        //load more
                                        if(adapterPosition == listArtis.size() - 1){
                                            if(canLoad){
                                                loadArtis();
                                            }
                                        }

                                        if(used == null){
                                            Glide.with(activity).load(listArtis.get(adapterPosition).getImage()).thumbnail(0.3f).apply(new RequestOptions().transform(new BlurTransformation())).listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    img_artis1.animate().alpha(1f).setDuration(500);
                                                    return false;
                                                }
                                            }).into(img_artis1);
                                            used = img_artis1;
                                        }
                                        else if(used.equals(img_artis1)){
                                            //Glide.with(ArtisActivity.this).load(listArtis.get(adapterPosition).getImage()).apply(new RequestOptions().transform(new BlurTransformation())).into(img_artis2);
                                            Glide.with(activity).load(listArtis.get(adapterPosition).getImage()).thumbnail(0.3f).apply(new RequestOptions().transform(new BlurTransformation())).into(img_artis2);
                                            fade(img_artis1, img_artis2);
                                            used = img_artis2;
                                        }
                                        else{
                                            //Glide.with(ArtisActivity.this).load(listArtis.get(adapterPosition).getImage()).apply(new RequestOptions().transform(new BlurTransformation())).into(img_artis1);
                                            Glide.with(activity).load(listArtis.get(adapterPosition).getImage()).thumbnail(0.3f).apply(new RequestOptions().transform(new BlurTransformation())).into(img_artis1);
                                            fade(img_artis2, img_artis1);
                                            used = img_artis1;
                                        }
                                    }
                                });
                            }

                            loaded += array.length();
                            if(loaded >= total_count){
                                canLoad = false;
                            }
                            loading = false;
                        }
                        else{
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            loading = false;
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e("InitArtis", e.getMessage());
                        loading = false;
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(activity, R.string.error_database, Toast.LENGTH_SHORT).show();
                    Log.e("InitArtis", result);
                    loading = false;
                }
            });

        }
        catch (JSONException e){
            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
            Log.e("InitArtis", e.getMessage());
            loading = false;
        }
    }

    private void fade(ImageView v1, ImageView v2) {
        v1.animate().alpha(0f).setDuration(500);
        v2.animate().alpha(1f).setDuration(500);
        v2.bringToFront();
    }
}
