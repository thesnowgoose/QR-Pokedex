package com.lcarrasco.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IPokemonApi {
    @GET("pokemon/{id}")
    Call<Pokemon> getPokemon(@Path("id") String id);

    @GET("pokemon/{id}")
    Call<Types> getPokemonTypes(@Path("id") int id);

    @GET("pokemon/")
    Call<PokemonListResult> getPokemonList(@Query("limit") int limit);

    @GET("pokemon-species/{id}")
    Call<PokemonDescription> getDescription(@Path("id") int id);

}
