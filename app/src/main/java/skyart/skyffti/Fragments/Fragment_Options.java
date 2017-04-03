/*
This is a templete for furture fragments
 */

package skyart.skyffti.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import skyart.skyffti.R;

/**
 * Created by Coltan on 3/28/2017.
 */

public class Fragment_Options extends Fragment {

   /*
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static Fragment_Options instance;
        public Fragment_Options() {

        }
        public void init(){




        }
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment_Options newInstance(int sectionNumber) {
            Fragment_Options fragment = new Fragment_Options();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            instance = fragment;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag_options, container, false);

            SeekBar mapStyle = (SeekBar) rootView.findViewById(R.id.mapStyleSeekbar);
            mapStyle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Fragment_Maps.setMapStyle(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            return rootView;
        }

}
