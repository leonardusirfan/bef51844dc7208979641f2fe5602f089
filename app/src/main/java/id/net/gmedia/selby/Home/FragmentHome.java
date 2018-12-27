package id.net.gmedia.selby.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import org.json.JSONArray;
import org.json.JSONException;

import id.net.gmedia.selby.R;
import id.net.gmedia.selby.Util.ApiVolleyManager;
import id.net.gmedia.selby.Util.AppRequestCallback;
import id.net.gmedia.selby.Util.Constant;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {

    //Variabel Fragment
    private boolean loaded = false;
    private Activity activity;
    private View v;

    //Variabel slider
    private SliderLayout slider;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        if(v == null){
            v = inflater.inflate(R.layout.fragment_home, container, false);

            slider = v.findViewById(R.id.slider);

            final RelativeLayout layout_search = v.findViewById(R.id.layout_search);
            layout_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, SearchActivity.class);

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, layout_search, "search");
                    activity.startActivity(i, options.toBundle());
                }
            });

            v.findViewById(R.id.img_artis).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, ArtisActivity.class));
                }
            });

            v.findViewById(R.id.img_artis_favorit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, ArtisActivity.class);
                    i.putExtra("follow", true);
                    startActivity(i);
                }
            });

            v.findViewById(R.id.img_hot_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, BarangActivity.class));
                }
            });

            v.findViewById(R.id.img_merchandise).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, BarangActivity.class);
                    i.putExtra("jenis", "Merchandise");
                    startActivity(i);
                }
            });

            v.findViewById(R.id.img_lelang).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, LelangActivity.class));
                }
            });

            v.findViewById(R.id.img_preloved).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, BarangActivity.class);
                    i.putExtra("jenis", "Preloved");
                    startActivity(i);
                }
            });
        }

        //jika slider belum dimuat
        if(!loaded){
            initSlider();
        }

        return v;
    }

    @Override
    public void onResume() {
        slider.startAutoCycle();
        super.onResume();
    }

    private void initSlider(){
        //Inisialisasi slider
        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_HOME_SLIDE, ApiVolleyManager.METHOD_GET, Constant.HEADER_AUTH, new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    JSONArray arrayslider = new JSONArray(response);
                    for(int i = 0; i < arrayslider.length(); i++){
                        //Inisialisasi slider
                        DefaultSliderView sliderView = new DefaultSliderView(activity);
                        sliderView.image(arrayslider.getJSONObject(i).getString("image")).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                        slider.addSlider(sliderView);
                    }
                    slider.movePrevPosition(false);
                    slider.setDuration(3000);
                    slider.setCustomIndicator((PagerIndicator) v.findViewById(R.id.indicator));
                    loaded = true;
                }
                catch (JSONException e){
                    Log.e("Slider", e.getMessage());
                    Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void onStop() {
        slider.stopAutoCycle();
        super.onStop();
    }
}