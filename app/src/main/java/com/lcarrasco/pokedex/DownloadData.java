package com.lcarrasco.pokedex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.lcarrasco.model.IPokemonApi;
import com.lcarrasco.model.Pokemon;
import com.lcarrasco.model.PokemonListResult;
import com.lcarrasco.model.Result;

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

// TODO change class name
public class DownloadData {

    public static final int _TotalPkmn = 151;
    private static String _UrlPokeApi = "http://pokeapi.co/api/v2/";
    public static final String _UrlImages = "http://pokeapi.co/media/img/";

    private static OnFinishLoading finish;

    public static void start(Context context, final OnFinishLoading finsh){

        finish = finsh;

        if (DataStorage.pokemonList.isEmpty()) {
            DataStorage.initRealm(context);
            if (DataStorage.pokemonSaved()) {
                // Loads realm onto DataStorage.pokemonList
                DataStorage.loadRealm();
                new getImages(context).execute();
            } else
                buildPkmnListRequest(context);
        }
    }

    private static Pokemon createPkmn(String... data){
        Pokemon p = new Pokemon();

        p.setId(Integer.parseInt(data[0]));
        p.setName(data[1]);

        return p;
    }

    private static void buildPkmnListRequest(final Context context) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(_UrlPokeApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IPokemonApi pokeApiService = retrofit.create(IPokemonApi.class);
        Call<PokemonListResult> pokemonListCall = pokeApiService.getPokemonList(Integer.toString(_TotalPkmn));

        pokemonListCall.enqueue(new Callback<PokemonListResult>() {
            @Override
            public void onResponse(Call<PokemonListResult> call, Response<PokemonListResult> response) {
                if (response.code() != 200)
                    // TODO do something if result is bad
                    System.out.println(response.code() + " it did not work");
                else {
                    List<Pokemon> pokemonList = new ArrayList<>();
                    for (int i = 0; i < response.body().getResults().size(); i++) {
                        String id = Integer.toString(i+1);
                        Result pokemonResult = response.body().getResults().get(i);
                        String name = pokemonResult.getName();
                        pokemonList.add(createPkmn(id, WordUtils.capitalize(name)));
                    }
                    DataStorage.saveRealm(pokemonList);
                    new getImages(context).execute();
                }
            }

            @Override
            public void onFailure(Call<PokemonListResult> call, Throwable t) {
                System.out.println("Failure to get request");
            }
        });
    }

    private static class getImages extends AsyncTask<String, Integer, Long> {

        private Context context;

        public getImages(Context context){
            this.context = context;
        }

        @Override
        protected Long doInBackground(String... params) {
            try {
                for (int i = 0; i < _TotalPkmn; i++) {
                    int id = i+1;
                    System.out.println("Downloading image " + id);
                    String imageUrl = _UrlImages + id + ".png";
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                    DataStorage.pkmnImagesList.add(bitmap);
                }
            } catch (Exception e) {
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
