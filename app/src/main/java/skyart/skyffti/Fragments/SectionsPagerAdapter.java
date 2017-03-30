package skyart.skyffti.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import skyart.skyffti.Fragments.FragmentTemplate;
import skyart.skyffti.Fragments.Fragment_Camera;
import skyart.skyffti.Fragments.Fragment_Maps;

/**
 * Created by Coltan on 3/28/2017.
 * Controler of the fragments
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        instance = this;
    }

    public static SectionsPagerAdapter instance;


    //only called at start
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
                    return Fragment_Maps.newInstance(position + 1);
            case 1:
                if(Fragment_Camera.PlaceholderFragment.instance == null) {
                return  Fragment_Camera.PlaceholderFragment.newInstance(position+1);
                }else{
                    return Fragment_Camera.PlaceholderFragment.instance;
                }

            case 2:
                return fragment_colorpick.PlaceholderFragment.newInstance(position + 1);

        }
        return FragmentTemplate.PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }

    public static void setView(int i) {
        instance.getItem(i);
    }
}