package com.lcarrasco.Controller;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImagesManager {
    private static String IMAGES_FOLDER = "pokemon_sprites";

    private static File getImageFile(String imgName, Context context){

        File directory = new ContextWrapper(context)
                .getDir(IMAGES_FOLDER, Context.MODE_PRIVATE);

        if (!directory.exists())
            directory.mkdir();

        return new File(directory,  imgName + ".png");
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
}
