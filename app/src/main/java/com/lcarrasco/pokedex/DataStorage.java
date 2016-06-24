package com.lcarrasco.pokedex;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lcarrasco.model.Pokemon;
import com.lcarrasco.model.Type;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;

public class DataStorage {

    public static List<Bitmap> pkmnImagesList = new ArrayList<>();
    public static List<Pokemon> pokemonObjList = new ArrayList<>(); // TODO Delete this list
    public static List<Pokemon> pokemonList = new ArrayList<>();

    private static String POKEMON_IMAGES = "Images";

    private static Realm realm;

    public static void initRealm(Context context){
        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(config);
//        Realm.deleteRealm(config);
        realm = Realm.getDefaultInstance();
    }

    protected static boolean pokemonSaved(){
        return !realm.where(Pokemon.class).findAll().isEmpty();
    }

    public static void saveRealm(List<Pokemon> list){
        realm.beginTransaction();
        Collection<Pokemon> realmPokemon = realm.copyToRealm(list);
//        Person trainer = realm.createObject(Person.class); // Create managed objects directly
//        person.getPkmns().add(managedPkmn);
        realm.commitTransaction();

        pokemonList = new ArrayList<>(realmPokemon);
    }

    protected static List<Pokemon> loadRealm(){
        pokemonList = realm.where(Pokemon.class).findAll();
        return pokemonList;
    }

    protected static void updatePokemonDescription(int id, String description){
        realm.beginTransaction();
        DataStorage.pokemonList.get(id-1).setDescription(description);
        realm.commitTransaction();
    }

    protected static void updatePokemonTypes(int id, List<Type> types){

        String t1 = "";
        String t2 = "";

        if (types.size() == 2)
            t1 = WordUtils.capitalize(types.get(1).getType().getName());
        t2 = WordUtils.capitalize(types.get(0).getType().getName());

        realm.beginTransaction();
        DataStorage.pokemonList.get(id-1).setType1(t1);
        DataStorage.pokemonList.get(id-1).setType2(t2);
        realm.commitTransaction();
    }


    protected static void closeRealm(){
        realm.close();
    }


    // Shared Preferences
    public static void saveSharedPreferences(Context context, int id, String data){
        String newData = "";
        if (verifySharedPreferences(context, id)) {
            newData = loadSharedPreferences(context, id) + "|" + data;
        } else
            newData = id + "|" + data;

        String key = Integer.toString(R.string.saved_pkmnInfo) + id;

        context.getSharedPreferences(Integer.toString(R.string.preferencesPkmnFile), Context.MODE_PRIVATE)
                .edit()
                .putString(key, newData)
                .commit();
    }

    public static String loadSharedPreferences(Context context, int id){
        String key = Integer.toString(R.string.saved_pkmnInfo) + id;

        return context
                .getSharedPreferences(Integer.toString(R.string.preferencesPkmnFile), Context.MODE_PRIVATE)
                .getString(key, null);

    }

    public static boolean verifySharedPreferences(Context context, int id){
        return loadSharedPreferences(context, id) != null;
    }


    // IMAGES
    private static File getImageFile(String imageName, Context context){
        File directory = new ContextWrapper(context)
                .getDir(POKEMON_IMAGES, Context.MODE_PRIVATE);
        return new File(directory, imageName + ".png");
    }

    public static boolean imageExists(Context context, String imageName){
        File file = getImageFile(imageName, context);

        if(file.exists()) {
            return true;
        }
        return false;
    }

    public static void saveImage(String imageName, Bitmap image, Context context){
        OutputStream outStream = null;
        File file = getImageFile(imageName, context);

        if(!file.exists()) {
            try {
                outStream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                System.out.println("Error Data.java - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static Bitmap loadImage(String imageName, Context context) {
        Bitmap image = null;
        File file = getImageFile(imageName, context);

        try {
            image = BitmapFactory.decodeStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error Data.java: loadImageFromStorage - " + e.getMessage());
            e.printStackTrace();
        }

        return image;

    }

    public static void deleteCache(Context context){
        File directory = new ContextWrapper(context)
                .getDir(POKEMON_IMAGES, Context.MODE_PRIVATE);

        if (directory.exists())
            directory.delete();

        for (int i = 0; i <= 151; i++) {
            boolean deleted = DataStorage.verifySharedPreferences(context, i);
            if (!deleted) {
                String s = DataStorage.loadSharedPreferences(context, i);
                System.out.println(s);
            }
        }
    }

    public static void deleteImagesCache(boolean delete, Context context){
        //PokemonImages
        if (delete) {
            File directory = new ContextWrapper(context)
                    .getDir(POKEMON_IMAGES, Context.MODE_PRIVATE);
            if (directory.exists())
                directory.delete();

            File directory2 = new ContextWrapper(context)
                    .getDir("PokemonImages", Context.MODE_PRIVATE);
            if (directory2.exists())
                directory2.delete();
        }

    }
}
