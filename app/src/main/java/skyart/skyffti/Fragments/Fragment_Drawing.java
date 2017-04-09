/*
This is a templete for furture fragments
 */

package skyart.skyffti.Fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import skyart.skyffti.R;
import skyart.skyffti.Utils.DrawingView;

/**
 * Created by Coltan on 3/28/2017.
 */

public class Fragment_Drawing extends Fragment {

    public Fragment_Drawing() {

    }

    public void init() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Drawing newInstance(int sectionNumber) {
        Fragment_Drawing fragment = new Fragment_Drawing();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_draw, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.theColorPicker);


//        textView.setText(getString(R.string.section_format));
        //Color Picker Dialog
        final Dialog d = new Dialog(this.getContext());
        d.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Fragment_Color fc = Fragment_Color.newInstance(2);
        d.setContentView(fc.onCreateView(inflater, null, savedInstanceState));
        FloatingActionButton picker = (FloatingActionButton) rootView.findViewById(R.id.floatingColorPick2);
        picker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                d.show();

                return false;
            }
        });

        Button sendButton = (Button) rootView.findViewById(R.id.button2);
        sendButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    DrawingView.send();
                }
                return  true;
            }
        });



        return rootView;
    }
}
