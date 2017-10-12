package com.example.kimdongun.promise.Tutorial;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kimdongun.promise.R;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentTutorial extends Fragment{

    public Drawable drawable;

    private ImageView imageView;

    public FragmentTutorial() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_0, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        if (drawable != null) {
            imageView.setBackground(drawable);
        }
    }
}