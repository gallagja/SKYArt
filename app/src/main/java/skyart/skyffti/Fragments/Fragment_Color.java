/*
This is a templete for furture fragments
 */

package skyart.skyffti.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import skyart.skyffti.R;

/**
 * Created by Coltan on 3/28/2017.
 */

public class Fragment_Color extends Fragment {


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static Fragment_Color instance;

    public Fragment_Color() {

    }

    private static int red = 0;
    private static int green = 0;
    private static int blue = 0;

    private int color;

    /*
    Returns an int of the color
     */
    public int getColor() {
        color = Color.argb(255, red, green, blue);
        return color;
    }

    public static int getRed() {
        return red;
    }

    public static int getGreen() {
        return green;
    }

    public static int getBlue() {
        return blue;
    }

    public void init() {


    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Color newInstance(int sectionNumber) {
        Fragment_Color fragment = new Fragment_Color();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        instance = fragment;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_color, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.theColorPicker);
        final ColorPicker picker = (ColorPicker) rootView.findViewById(R.id.theColorPicker);
        picker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                red = Color.red(picker.getColor());
                green = Color.green(picker.getColor());
                blue = Color.blue(picker.getColor());

                return false;
            }
        });

        // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        return rootView;
    }

}
