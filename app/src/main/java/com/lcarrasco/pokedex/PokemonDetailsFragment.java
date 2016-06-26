package com.lcarrasco.pokedex;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lcarrasco.Controller.PokemonRealmStorage;
import com.lcarrasco.model.IPokemonApi;
import com.lcarrasco.model.PokemonDescription;
import com.lcarrasco.model.Types;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonDetailsFragment extends Fragment {
    private final static String ARGS_PKMN_ID = "pkmnid";
    private static String _UrlPokeApi = "http://pokeapi.co/api/v2/";

    private int id;
    private View view;

    private boolean descriptionFound = false;
    private boolean typesFound = false;

    public static PokemonDetailsFragment newInstance(int id){

        final Bundle bundle= new Bundle();
        bundle.putInt(ARGS_PKMN_ID, id);

        final PokemonDetailsFragment fragment = new PokemonDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public PokemonDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        id = getArguments().getInt(ARGS_PKMN_ID);

        if (PokemonRealmStorage.pokemonList.get(id-1).getDescription() == null)
            buildDescriptionRequest();
        else
            descriptionFound = true;

        if (PokemonRealmStorage.pokemonList.get(id-1).getType1() == null)
            buildTypesRequest();
        else
            typesFound = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pokemon_details, container, false);

        final TextView name_tv = (TextView) view.findViewById(R.id.detailsName);
        final ImageView image_iv = (ImageView) view.findViewById(R.id.detailsImage);
        name_tv.setText(String.format("%03d", id) + " " +
                PokemonRealmStorage.pokemonList.get(id-1).getName());
        image_iv.setImageBitmap(PokemonRealmStorage.pkmnImagesList.get(id-1));

        if (descriptionFound)
            updateDescriptionOnUI();
        if (typesFound)
            updateTypesOnUI();

        return view;
    }

    private void updateDescriptionOnUI(){
        if (view != null) {
            final TextView desc_tv = (TextView) view.findViewById(R.id.detailsDescription);
            desc_tv.setText(PokemonRealmStorage.pokemonList.get(id - 1).getDescription());
        }
    }

    private void updateTypesOnUI(){
        if (view != null) {
            final TextView type1_tv = (TextView) view.findViewById(R.id.type1);
            final TextView type2_tv = (TextView) view.findViewById(R.id.type2);
            type1_tv.setText(PokemonRealmStorage.pokemonList.get(id - 1).getType1());
            type2_tv.setText(PokemonRealmStorage.pokemonList.get(id - 1).getType2());
        }
    }

    private void buildDescriptionRequest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(_UrlPokeApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IPokemonApi pokeApiService = retrofit.create(IPokemonApi.class);
        Call<PokemonDescription> pokemonDescription = pokeApiService.getDescription(id);

        pokemonDescription.enqueue(new Callback<PokemonDescription>() {
            @Override
            public void onResponse(Call<PokemonDescription> call, retrofit2.Response<PokemonDescription> response) {
                if (response.code() != 200)
                    Toast.makeText(getContext(), "Description not found", Toast.LENGTH_SHORT).show();
                else {
                    String description = response.body().getFlavorTextEntries().get(1).getFlavorText();
                    PokemonRealmStorage.updatePokemonDescription(id, description);
                    updateDescriptionOnUI();
                }
            }

            @Override
            public void onFailure(Call<PokemonDescription> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to load description", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void buildTypesRequest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(_UrlPokeApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IPokemonApi pokeApiService = retrofit.create(IPokemonApi.class);
        Call<Types> pokemonTypes = pokeApiService.getPokemonTypes(id);

        pokemonTypes.enqueue(new Callback<Types>() {
            @Override
            public void onResponse(Call<Types> call, retrofit2.Response<Types> response) {
                if (response.code() != 200)
                    Toast.makeText(getContext(), "Types not found", Toast.LENGTH_SHORT).show();
                else {
                    PokemonRealmStorage.updatePokemonTypes(id, response.body().getTypes());
                    updateTypesOnUI();
                }
            }

            @Override
            public void onFailure(Call<Types> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to load types", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
