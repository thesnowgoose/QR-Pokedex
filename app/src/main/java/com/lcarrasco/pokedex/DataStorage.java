package com.lcarrasco.pokedex;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theSnowGoose on 6/14/16.
 */
public class DataStorage {

    public static List<Bitmap> pkmnImagesList = new ArrayList<>();
    public static List<Pokemon> pokemonObjList = new ArrayList<>();

    private static String POKEMON_IMAGES = "Images";


    public static void save(Context context, int id, String data){
//        if (data.split("\\|").length < 3)
//            data = id + "|" + data;
        String newData = "";
        if (isStored(context, id)) {
            newData = load(context, id) + "|" + data;
        } else
            newData = id + "|" + data;

        String key = Integer.toString(R.string.saved_pkmnInfo) + id;

        context.getSharedPreferences(Integer.toString(R.string.preferencesPkmnFile), Context.MODE_PRIVATE)
                .edit()
                .putString(key, newData)
                .commit();
    }

    public static String load(Context context, int id){
        String key = Integer.toString(R.string.saved_pkmnInfo) + id;

        return context
                .getSharedPreferences(Integer.toString(R.string.preferencesPkmnFile), Context.MODE_PRIVATE)
                .getString(key, null);

    }

    public static boolean isStored(Context context, int id){
        return load(context, id) != null;
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

//    public static void deleteImagesCache(boolean delete, Context context){
//        //PokemonImages
//        if (delete) {
//            File directory = new ContextWrapper(context)
//                    .getDir(POKEMON_IMAGES, Context.MODE_PRIVATE);
//            if (directory.exists())
//                directory.delete();
//
//            File directory2 = new ContextWrapper(context)
//                    .getDir("PokemonImages", Context.MODE_PRIVATE);
//            if (directory2.exists())
//                directory2.delete();
//        }
//
//    }
}
