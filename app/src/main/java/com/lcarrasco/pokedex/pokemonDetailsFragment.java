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
import org.json.JSONObject;

public class pokemonDetailsFragment extends Fragment {
    private final static String ARGS_PKMN_ID = "pkmnid";
    private static String urlDex = "http://pokeapi.co/api/v2/pokemon-species/<<id>>/";

    private String pkmnDesc;
    private int id;
    private View view;

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
    public void onAttach(Context context) {
        super.onAttach(context);

        id = getArguments().getInt(ARGS_PKMN_ID);
        String mutableUrl = urlDex.replace("<<id>>", Integer.toString(id));
        MySingleton.getInstance(context).addToRequestQueue(buildRequest(mutableUrl));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pokemon_details, container, false);
        final TextView name_tv = (TextView) view.findViewById(R.id.detailsName);
        final ImageView image_iv = (ImageView) view.findViewById(R.id.detailsImage);

        name_tv.setText(String.format("%03d", id) + " " + DownloadData.pokemonObjList.get(id-1).getName());
        image_iv.setImageBitmap(DownloadData.pkmnImagesList.get(id-1));

        return view;
    }

    private JsonObjectRequest buildRequest(String urlDex) {
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

                            System.out.println(pkmnDesc);

                        } catch (Exception e) {
//                        Toast.makeText(context, "Error Loading JSON", Toast.LENGTH_SHORT).show();
                            System.out.println("Error pokemonDetailsFragment - " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("No response from API - pokemonDetailsFragment: " + error);
                error.printStackTrace();

            }
        });

    }

}
