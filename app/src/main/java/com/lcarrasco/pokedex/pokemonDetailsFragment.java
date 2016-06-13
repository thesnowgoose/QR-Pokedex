package com.lcarrasco.pokedex;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLOutput;

public class pokemonDetailsFragment extends Fragment {
    private final static String ARGS_PKMN_ID = "pkmnid";

    public static pokemonDetailsFragment newInstance(int id){

        final Bundle bundle= new Bundle();
        bundle.putInt(ARGS_PKMN_ID, id);

        final pokemonDetailsFragment fragment = new pokemonDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public pokemonDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_pokemon_details, container, false);
        final TextView name_tv = (TextView) view.findViewById(R.id.detailsName);
        final ImageView image_iv = (ImageView) view.findViewById(R.id.detailsImage);

        final int id = getArguments().getInt(ARGS_PKMN_ID);

        name_tv.setText(String.format("%03d", id) + " " + LoadData.pokemonObjList.get(id-1).getName());
        image_iv.setImageBitmap(LoadData.pkmnImagesList.get(id-1));

        return view;
    }
}
