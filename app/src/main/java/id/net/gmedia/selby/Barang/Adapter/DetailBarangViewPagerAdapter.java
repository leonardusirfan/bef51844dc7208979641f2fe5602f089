package id.net.gmedia.selby.Barang.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selby.R;

public class DetailBarangViewPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public DetailBarangViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.barang_detail_informasi);
            case 1:
                return context.getString(R.string.barang_detail_ulasan);
            case 2:
                return context.getString(R.string.barang_detail_diskusi);
        }
        return null;
    }
}
