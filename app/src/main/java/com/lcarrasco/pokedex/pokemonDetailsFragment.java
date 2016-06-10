package com.lcarrasco.pokedex;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class pokemonDetailsFragment extends Fragment {
    private final static String ARGS_PKMN_ID = "pkmnid";
    private final static String ARGS_PKMN_NAME = "pkmnName";
    private final static String ARGS_PKMN_IMAGE = "pkmnImage";

    public static pokemonDetailsFragment newInstance(int id, String name){

        final Bundle bundle= new Bundle();
        bundle.putInt(ARGS_PKMN_ID, id);
        bundle.putString(ARGS_PKMN_NAME, name);

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
        final TextView name_tv = (TextView) view.findViewById(R.id.detailsPkmnName);
        final ImageView image_iv = (ImageView) view.findViewById(R.id.detailsImage);

        final Bundle args = getArguments();
        name_tv.setText(args.getString(ARGS_PKMN_ID) + " " + args.getString(ARGS_PKMN_NAME));
        image_iv.setImageBitmap(LoadData.pkmnImagesList.get(args.getInt(ARGS_PKMN_ID)));

        return view;
    }

}
