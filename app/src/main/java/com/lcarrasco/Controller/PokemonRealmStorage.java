package com.lcarrasco.Controller;

import android.content.Context;
import android.graphics.Bitmap;

import com.lcarrasco.model.Pokemon;
import com.lcarrasco.model.Type;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PokemonRealmStorage {

    public static List<Bitmap> pkmnImagesList = new ArrayList<>();
    public static List<Pokemon> pokemonList = new ArrayList<>();

    private static Realm realm;

    public static void init(Context context) {
        RealmConfiguration config = new RealmConfiguration
                .Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
//        Realm.deleteRealm(config);
        realm = Realm.getDefaultInstance();
    }

    protected static boolean hasData(){
        return !realm.where(Pokemon.class).findAll().isEmpty();
    }

    public static void save(List<Pokemon> list){
        realm.beginTransaction();
        Collection<Pokemon> realmPokemon = realm.copyToRealm(list);
        realm.commitTransaction();

        pokemonList = new ArrayList<>(realmPokemon);
    }

    protected static void loadData(){
        pokemonList = realm.where(Pokemon.class).findAll();
    }

    public static void updatePokemonDescription(int id, String description){
        realm.beginTransaction();
        PokemonRealmStorage.pokemonList.get(id-1).setDescription(description);
        realm.commitTransaction();
    }

    public static void updatePokemonTypes(int id, List<Type> types){

        String t1 = "";

        if (types.size() == 2)
            t1 = WordUtils.capitalize(types.get(1).getType().getName());
        String t2 = WordUtils.capitalize(types.get(0).getType().getName());

        realm.beginTransaction();
        PokemonRealmStorage.pokemonList.get(id-1).setType1(t1);
        PokemonRealmStorage.pokemonList.get(id-1).setType2(t2);
        realm.commitTransaction();
    }

    public static void updateCapturedState(int id, boolean captured){
        realm.beginTransaction();
        PokemonRealmStorage.pokemonList.get(id-1).setCaptured(captured);
        realm.commitTransaction();
    }

    public static boolean alreadyCaptured(int id){
        Pokemon p = realm.where(Pokemon.class).equalTo("id", id).findFirst();
        return p.isCaptured();
    }

    public static void closeRealm(){
        realm.close();
    }

}
