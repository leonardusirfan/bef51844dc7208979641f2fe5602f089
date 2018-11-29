package id.net.gmedia.selby.Util;

import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import id.net.gmedia.selby.R;

public class ImageSliderAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private ArrayList<ImageContainer> images;
    private int currentPage = 0;
    //    private LayoutInflater inflater;
    private Context context;
    private ImageContainer imageContainer;
    private boolean crop;
    private Timer swipeTimer;
    private Runnable Update;
    private Handler handler;

    public ImageSliderAdapter(Context context, final ImageSliderViewPager slider, ArrayList<ImageContainer> images, boolean crop){
        this.context = context;
        this.images = images;
        this.crop = crop;
        this.Update = new Runnable() {
            @Override
            public void run() {
                if (currentPage + 1 == ImageSliderAdapter.this.images.size()) {
                    currentPage = 0;
                }
                else{
                    currentPage++;
                }
                slider.setCurrentItem(currentPage, true);
            }
        };
        handler = new Handler();
    }

    public void startTimer(){
        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(ImageSliderAdapter.this.Update);
            }
        }, 3000, 3000);
    }

    public void setPosition(int position){
        currentPage = position;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View myImageLayout = LayoutInflater.from(context).inflate(R.layout.slide, view, false);
//        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageView myImage = myImageLayout.findViewById(R.id.image);
        imageContainer = images.get(position);
        /*Picasso.with(context).load(imageContainer.getImage())
                .resize(720, 720).centerInside()
                .into(myImage);*/
        if(crop){
            Glide.with(context).load(imageContainer.getImage()).apply(RequestOptions.centerCropTransform()).into(myImage);
        }
        else{
            Glide.with(context).load(imageContainer.getImage()).apply(new RequestOptions().dontAnimate().dontTransform()).into(myImage);
        }
//        myImage.setImageResource(R.drawable.logo_osbond);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public void pauseSlider(){
        if(swipeTimer != null){
            swipeTimer.cancel();
            swipeTimer = null;
        }
    }

    public void resumeSlider(){
        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(ImageSliderAdapter.this.Update);
            }
        }, 0, 3000);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        System.out.println("MASUK");
        currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
