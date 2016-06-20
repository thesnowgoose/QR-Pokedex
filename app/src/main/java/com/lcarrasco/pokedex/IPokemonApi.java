package com.lcarrasco.pokedex;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IPokemonApi {
    @GET("pokemon/{id}")
    Call<Pokemon> getPokemon(@Path("id") String id);

    @GET("pokemon")
    Call<List<Pokemon>> getPokemonList(@Query("limit") String limit);

    @GET("pokemon-species/{id}")
    Call<Pokemon> getDescription(@Path("id") String id);

}
