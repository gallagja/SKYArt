package skyart.skyffti.Fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import skyart.skyffti.R;

public class fragment_colorpick extends Fragment {

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static PlaceholderFragment instance;

        private int red = 0;
        private int green = 0;
        private int blue = 255;

        private int color;

        public int getColor() {
            color = Color.argb(255, red, green, blue);
            return color;
        }

        public int getRed() {
            return red;
        }

        public int getGreen() {
            return green;
        }

        public int getBlue() {
            return blue;
        }

        public PlaceholderFragment() {

        }
        public void init(){




        }
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            instance = fragment;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_colorpick, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            final Button button = (Button) rootView.findViewById(R.id.button2);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    SectionsPagerAdapter.setView(1);
                    return true;
                }
            });

            final SeekBar redBar = (SeekBar) rootView.findViewById(R.id.redSeek);
            redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    red = progress;
                    Log.d("RED", "onProgressChanged: " + progress );
                    button.setBackgroundColor(Color.argb(255, red, green, blue));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            final SeekBar greenBar = (SeekBar) rootView.findViewById(R.id.greenSeek);
            greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    green = progress;
                    button.setBackgroundColor(Color.argb(255, red, green, blue));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            final SeekBar blueBar = (SeekBar) rootView.findViewById(R.id.blueSeek);
            blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    blue = progress;
                    button.setBackgroundColor(Color.argb(255, red, green, blue));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }


    }
}
