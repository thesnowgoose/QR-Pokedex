package com.lcarrasco.pokedex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadData {

    public static final int totalPkmn = 151;
    public static final String urlDex = "http://pokeapi.co/api/v2/pokemon/?limit=" + totalPkmn;
//    public static final String urlDex = "http://pokeapi.co/api/v2/pokemon-species/1/";
    public static final String urlImages = "http://pokeapi.co/media/img/<<id>>.png";
//    public static final String urlImages = "http://pokeapi.co/media/sprites/pokemon/<<id>>.png";

    private static OnFinishLoading finish;

    public static  List<Bitmap> pkmnImagesList = new ArrayList<>();
    public static List<pokemon> pokemonObjList = new ArrayList<>();

    public static void start(Context ctx, final OnFinishLoading finsh){

        finish = finsh; // finish = (OnFinishLoading)

        if (pokemonObjList.isEmpty())
            MySingleton.getInstance(ctx).addToRequestQueue(buildRequest(urlDex, ctx));

        else if (pkmnImagesList.isEmpty())
            new GetImages(ctx).execute();

    }

//    private static JsonObjectRequest pokemonList = new JsonObjectRequest(Request.Method.GET, urlDex, null,
//            new Response.Listener<JSONObject>() {
//
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        JSONArray pkmnArray = new JSONArray(response.getString("results"));
//                        for (int i = 0; i < pkmnArray.length(); i++) {
//                            System.out.println("Getting info from pokemon " + i);
//                            //pkmnList.add(new JSONObject(pkmnArray.get(i).toString()));
//
//                            String name = new JSONObject(pkmnArray.get(i)
//                                                        .toString())
//                                                        .getString("name");
//
//                            pokemonObjList.add(new pokemon(i+1, WordUtils.capitalize(name)));
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(context, "Error Loading JSON", Toast.LENGTH_SHORT).show();
//                        System.out.println(e.getMessage());
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            System.out.println("No response from API");
//
//        }
//    });

    private static JsonObjectRequest buildRequest(String urlDex, final Context context) {

        return new JsonObjectRequest(Request.Method.GET, urlDex, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray pkmnArray = new JSONArray(response.getString("results"));
                            for (int i = 0; i < pkmnArray.length(); i++) {

                                int id = i+1;
                                System.out.println("Getting info from pokemon " + id);

                                String name = new JSONObject(pkmnArray.get(i)
                                        .toString())
                                        .getString("name");

                                pokemonObjList.add(new pokemon(id, WordUtils.capitalize(name)));
                                Data.save(context, id + "|" + WordUtils.capitalize(name));

                            }
                            if (pkmnImagesList.isEmpty())
                                new GetImages(context).execute();
                        } catch (Exception e) {
                            Toast.makeText(context, "Error Loading JSON", Toast.LENGTH_SHORT).show();
                            System.out.println(e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("No response from API");

            }
        });
    }

    private static class GetImages extends AsyncTask<String, Integer, Long> {

        private Context context;

        public GetImages(Context context){
            this.context = context;
        }

        @Override
        protected Long doInBackground(String... params) {
            try {
                for (int i = 1; i <= totalPkmn ; i++) {
                    String imageName = pokemonObjList.get(i-1).getName();
                    if (!Data.existImage(context, imageName)) {
                        System.out.println("Saving image " + i);
                        String imageUrl = urlImages.replace("<<id>>", Integer.toString(i));
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                        pkmnImagesList.add(bitmap);
                        Data.saveImage(imageName, bitmap, context);
                    } else {
                        System.out.println("Loading image " + i);
                        pkmnImagesList.add(Data.loadImage(imageName, context));
                    }

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
