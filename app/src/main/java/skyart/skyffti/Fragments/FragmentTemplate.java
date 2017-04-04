/*
This is a templete for furture fragments
 */

package skyart.skyffti.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import skyart.skyffti.R;

/**
 * Created by Coltan on 3/28/2017.
 */

public class FragmentTemplate extends Fragment {


    public static FragmentTemplate instance;

    public FragmentTemplate() {

    }

    public void init() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentTemplate newInstance(int sectionNumber) {
        FragmentTemplate fragment = new FragmentTemplate();

        instance = fragment;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_temp, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.theColorPicker);


        textView.setText(getString(R.string.section_format));

        return rootView;
    }
}
