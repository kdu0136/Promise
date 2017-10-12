package com.example.kimdongun.promise.Tutorial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.kimdongun.promise.R;

/**
 * Created by KimDongun on 2016-12-30.
 */

public class FragmentTutorialStart extends Fragment implements View.OnClickListener{

    public String type_tutorial; //어떤 튜토리얼 화면인지

    public Drawable drawable;

    private ImageView imageView;
    private Button button_skip;

    public FragmentTutorialStart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tutorial_s, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        if(drawable != null){
            imageView.setBackground(drawable);
        }

        button_skip = (Button)view.findViewById(R.id.button_skip);
        button_skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_skip:
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage(type_tutorial + " 튜토리얼을 읽지 않고 넘기시겠습니까?\n끝낸 튜토리얼은 다시 볼 수 없습니다.").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((TutorialActivity) getActivity()).endTutorial();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                break;
        }
    }
}