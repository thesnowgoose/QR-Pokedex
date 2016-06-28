package com.lcarrasco.pokedex;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class InfoFragment extends Fragment {


    public InfoFragment() {
    }

    public static InfoFragment newInstance(){
        return new InfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        TextView readme1 = (TextView) view.findViewById(R.id.readme1);
        readme1.setText(Html.fromHtml(getString(R.string.readme1)));

        TextView readme2 = (TextView) view.findViewById(R.id.readme2);
        readme2.setText(Html.fromHtml(getString(R.string.readme2)));
        readme2.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

}
