package com.lcarrasco.pokedex;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PokemonDetailsFragment extends Fragment {
    private final static String ARGS_PKMN_ID = "pkmnid";
    private static String _RootUrl = "http://pokeapi.co/api/v2/";
    private static String _DescriptionUrl = "pokemon-species/";
    private static String _TypesUrl = "pokemon/";

    private String pkmnDesc;
    private int id;
    private View view;
    private boolean previousDescFound = false;

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

        if (DataStorage.load(context, id).split("\\|").length < 4) { // Has pokemon description and type?
            // If its less than 4, it means details hasn't been saved yet
            String mutableUrl = _RootUrl + _DescriptionUrl + id;
            VolleyRequestQueue.getInstance(context).addRequest(buildDescRequest(mutableUrl));
//            mutableUrl = _RootUrl + _TypesUrl + id;
//            VolleyRequestQueue.getInstance(context).addRequest(buildTypesRequest(mutableUrl));
        }
        else {
            previousDescFound = true;
            // Get description from shared preferences
            DataStorage.pokemonObjList.get(id-1).setDescription(DataStorage.load(context, id).split("\\|")[2]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pokemon_details, container, false);
        final TextView name_tv = (TextView) view.findViewById(R.id.detailsName);
        final ImageView image_iv = (ImageView) view.findViewById(R.id.detailsImage);

        if (previousDescFound){
            final TextView desc_tv = (TextView) view.findViewById(R.id.detailsDescription);
            desc_tv.setText(DataStorage.pokemonObjList.get(id-1).getDescription());
            final TextView type1_tv = (TextView) view.findViewById(R.id.type1);
            type1_tv.setText(DataStorage.pokemonObjList.get(id-1).getType1());
            final TextView type2_tv = (TextView) view.findViewById(R.id.type2);
            type2_tv.setText(DataStorage.pokemonObjList.get(id-1).getType2());
        }
        name_tv.setText(String.format("%03d", id) + " " + DataStorage.pokemonObjList.get(id-1).getName());
        image_iv.setImageBitmap(DataStorage.pkmnImagesList.get(id-1));

        return view;
    }

    private JsonObjectRequest buildDescRequest(String urlDex) {
        return new JsonObjectRequest(Request.Method.GET, urlDex, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray detailsPkmnArray = new JSONArray(response.getString("flavor_text_entries"));

                            pkmnDesc = new JSONObject(detailsPkmnArray.get(1)
                                    .toString())
                                    .getString("flavor_text");

                            final TextView desc_tv = (TextView) view.findViewById(R.id.detailsDescription);
                            desc_tv.setText(pkmnDesc);

                            DataStorage.pokemonObjList.get(id-1).setDescription(pkmnDesc);

                            Context context = getActivity();
                            DataStorage.save(context, id, pkmnDesc);
                            System.out.println("Downloaded description for pokemon " + id);

                        } catch (Exception e) {
//                        Toast.makeText(context, "Error Loading JSON", Toast.LENGTH_SHORT).show();
                            System.out.println("Error pokemonDetailsFragment - " + e.getMessage());
                        }

                        VolleyRequestQueue.getInstance(getActivity()).addRequest(buildTypesRequest(_RootUrl + _TypesUrl + id));

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                    System.out.println("No response from API pokemonDetailsFragment: buildDescRequest - " + error);
                    error.printStackTrace();
            }
        });
    }

    private JsonObjectRequest buildTypesRequest(String url) {
        return new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray types = new JSONArray(response.getString("types"));
                            JSONObject t1 = new JSONObject(types.getJSONObject(0).getString("type"));
                            String type1 = t1.getString("name");
                            String type2 = "";

                            if (types.length() > 1) {
                                JSONObject t2 = new JSONObject(types.getJSONObject(1).getString("type"));
                                type2 = t2.getString("name");
                            }

                            final TextView type1_tv = (TextView) view.findViewById(R.id.type1);
                            type1_tv.setText(type1);
                            final TextView type2_tv = (TextView) view.findViewById(R.id.type2);
                            type2_tv.setText(type2);

                            updateDataAndList(type1, type2);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("No response from API pokemonDetailsFragment: buildDescRequest - " + volleyError);
                        volleyError.printStackTrace();
                    }
        });
    }

    private void updateDataAndList(String t1, String t2){
        DataStorage.pokemonObjList.get(id-1).setType1(t1);
        DataStorage.pokemonObjList.get(id-1).setType2(t2);

        Context context = getActivity();
        DataStorage.save(context, id, t1 + "|" + t2);
    }
}
