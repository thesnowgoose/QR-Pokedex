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
public class Data {

//    public static List<Bitmap> pkmnImagesList = new ArrayList<>();
//    public static List<pokemon> pokemonObjList = new ArrayList<>();


    private static String POKEMON_IMAGES = "PokemonImages";

    public static void save(Context context, String data){
        context.getSharedPreferences(Integer.toString(R.string.preferencePkmnDesc), Context.MODE_PRIVATE)
                .edit()
                .putString(Integer.toString(R.string.saved_pkmnInfo), data)
                .commit();
    }

    public static String load(Context context){
        return context
                .getSharedPreferences(Integer.toString(R.string.preferencePkmnDesc), Context.MODE_PRIVATE)
                .getString(Integer.toString(R.string.preferencePkmnDesc), null);

    }

    public static boolean isDataSaved(Context context){
        if (load(context) != null)
            return true;
        else
            return false;
    }


    // IMAGES
    private static File getImageFile(String imageName, Context context){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(POKEMON_IMAGES, Context.MODE_PRIVATE);
        return new File(directory, imageName + ".png");
    }

    public static boolean existImage(Context context, String imageName){
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
}
