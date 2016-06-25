package com.lcarrasco.pokedex;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class LoadingFragment extends Fragment {

    public LoadingFragment() {
        // Required empty public constructor
    }

    public static LoadingFragment newInstance(){
        return new LoadingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_loading, container, false);

        Animation animRotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate4ever);
        v.findViewById(R.id.loadingPokeball).startAnimation(animRotate);

        return v;
    }
}
