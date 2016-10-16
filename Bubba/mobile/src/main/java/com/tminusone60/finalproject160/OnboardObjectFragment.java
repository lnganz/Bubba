package com.tminusone60.finalproject160;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// Instances of this class are fragments representing a single
// object in our collection.
public class OnboardObjectFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_onboard_image, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.fragment_help_image);
        Bundle args = getArguments();
        int imageNum = args.getInt(ARG_OBJECT);
        switch (imageNum) {
            case 1:
                imageView.setImageResource(R.drawable.onboard_1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.onboard_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.onboard_3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.onboard_4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.onboard_5);
                break;
        }
//        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
//                Integer.toString(args.getInt(ARG_OBJECT)));
        return rootView;
    }
}