package com.lcarrasco.pokedex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lcarrasco.Controller.ImagesManager;
import com.lcarrasco.model.IPokemonApi;
import com.lcarrasco.model.Pokemon;
import com.lcarrasco.model.PokemonListResult;

import org.apache.commons.lang3.text.WordUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadData {

    private static final int _totalPkmn = 151;
    private static final String _urlPokeApi = "http://pokeapi.co/api/v2/";
    private static final String _urlImages = "http://pokeapi.co/media/img/";

    private static OnFinishLoading finish;

    public static void start(Context context, final OnFinishLoading finsh){

        finish = finsh;

        PokemonRealmStorage.init(context);
        if (PokemonRealmStorage.hasData()) {
            PokemonRealmStorage.loadData();
            new GetImages(context).execute();
        } else
            buildPkmnListRequest(context);
    }

    private static Pokemon createPkmn(int id, String name){
        Pokemon p = new Pokemon();

        p.setId(id);
        p.setName(name);

        return p;
    }

    private static void buildPkmnListRequest(final Context context) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(_urlPokeApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IPokemonApi pokeApiService = retrofit.create(IPokemonApi.class);
        Call<PokemonListResult> pokemonListCall = pokeApiService.getPokemonList(_totalPkmn);

        pokemonListCall.enqueue(new Callback<PokemonListResult>() {
            @Override
            public void onResponse(Call<PokemonListResult> call, Response<PokemonListResult> response) {
                if (response.code() != 200)
                    Toast.makeText(context, "Pokémon list not available", Toast.LENGTH_SHORT).show();
                else {
                    List<Pokemon> pokemonList = new ArrayList<>();
                    for (int i = 0; i < response.body().getResults().size(); i++) {
                        int id = i+1;
                        String name = response.body().getResults().get(i).getName();
                        pokemonList.add(createPkmn(id, WordUtils.capitalize(name)));
                    }
                    PokemonRealmStorage.save(pokemonList);
                    new GetImages(context).execute();
                }
            }

            @Override
            public void onFailure(Call<PokemonListResult> call, Throwable t) {
                Toast.makeText(context, "Pokémon list not available", Toast.LENGTH_SHORT).show();
                System.out.println("Failure to get request");
            }
        });
    }

    private static class GetImages extends AsyncTask<String, Integer, Long> {

        private Context context;
        protected GetImages(Context context){ this.context = context; }

        @Override
        protected Long doInBackground(String... params) {
            try {
                for (int i = PokemonRealmStorage.pkmnImagesList.size(); i < _totalPkmn; i++) {
                    int id = i+1;
                    if (!ImagesManager.imageExists(context, "pkmn" + id)) {
                        System.out.println("Downloading image " + id);
                        String imageUrl = _urlImages + id + ".png";
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                        PokemonRealmStorage.pkmnImagesList.add(bitmap);
                        ImagesManager.saveImage("pkmn" + id, bitmap, context);
                    } else {
                        PokemonRealmStorage.pkmnImagesList.add(
                                ImagesManager.loadImage("pkmn" + id, context)
                        );
                    }
                }
            } catch (Exception e) {
                Toast.makeText(context, "Error downloading images", Toast.LENGTH_SHORT).show();
                System.out.println("Error DownloadData.java: doInBackground() - " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Long result) {
            System.out.println("Load images Finished");
            finish.onFinishLoading();
        }
    }

    public interface OnFinishLoading {
        void onFinishLoading();
    }

}
