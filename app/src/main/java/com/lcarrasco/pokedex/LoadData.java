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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadData {

    public static final int totalPkmn = 151;
    public static final String urlDex = "http://pokeapi.co/api/v2/pokemon/?limit=" + totalPkmn;
//    public static final String urlDex = "http://pokeapi.co/api/v2/pokemon-species/1/";
    public static final String urlImages = "http://pokeapi.co/media/img/<<id>>.png";
//    public static final String urlImages = "http://pokeapi.co/media/sprites/pokemon/<<id>>.png";

    private static Context context;
    private static OnFinishLoading finish;

    public static  List<Bitmap> pkmnImagesList = new ArrayList<>();
    public static List<pokemon> pokemonObjList = new ArrayList<>();

    public static void start(Context ctx, final OnFinishLoading finsh){

        context = ctx;
        finish = finsh;

        if (pkmnImagesList.isEmpty())
            new GetImages().execute();
        if (pokemonObjList.isEmpty())
            MySingleton.getInstance(ctx).addToRequestQueue(pokemonList);
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

    private static JsonObjectRequest pokemonList = new JsonObjectRequest(Request.Method.GET, urlDex, null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray pkmnArray = new JSONArray(response.getString("results"));
                        for (int i = 0; i < pkmnArray.length(); i++) {
                            System.out.println("Getting info from pokemon " + i);
                            //pkmnList.add(new JSONObject(pkmnArray.get(i).toString()));

                            String name = new JSONObject(pkmnArray.get(i)
                                    .toString())
                                    .getString("name");

                            pokemonObjList.add(new pokemon(i+1, WordUtils.capitalize(name)));
                        }
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

    private static class GetImages extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... params) {
            try {
                for (int i = 1; i <= totalPkmn ; i++) {
                    System.out.println("Saving image " + i);
                    String imageUrl = urlImages.replace("<<id>>", Integer.toString(i));
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
                    pkmnImagesList.add(bitmap);
                }
            } catch (MalformedURLException e) {
                System.out.println("Malformed exception");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("IO Exception");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

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
