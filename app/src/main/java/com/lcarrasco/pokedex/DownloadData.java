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

// TODO change class name
public class DownloadData {

    public static final int _TotalPkmn = 151;
    public static final String _UrlPkmnList = "http://pokeapi.co/api/v2/pokemon/?limit=" + _TotalPkmn;
    public static final String _UrlImages = "http://pokeapi.co/media/img/";

    private static OnFinishLoading finish;

    public static void start(Context context, final OnFinishLoading finsh){

        finish = finsh;

        if (DataStorage.pokemonObjList.isEmpty()) {
            if (DataStorage.isStored(context, 1)) {
                for (int i = 0; i < _TotalPkmn; i++) {
                    int id = i + 1;
                    System.out.println("Loading data from " + id);
                    String[] pokemonInfo = DataStorage.load(context, id).split("\\|");
                    DataStorage.pokemonObjList.add(createPkmn(pokemonInfo));
                }
                new getImages(context).execute();
            } else
                VolleyRequestQueue.getInstance(context).addRequest(buildPkmnListRequest(_UrlPkmnList, context));
        }
    }

    private static Pokemon createPkmn(String... data){
        Pokemon p = new Pokemon(data[0], data[1]);

        if(data.length > 2)
            p.setDescription(data[2]);
        if(data.length > 3)
            p.setType1(data[3]);
        if(data.length > 4)
            p.setType2(data[4]);

        return p;
    }

    private static JsonObjectRequest buildPkmnListRequest(String urlDex, final Context context) {

        return new JsonObjectRequest(Request.Method.GET, urlDex, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray pkmnArray = new JSONArray(response.getString("results"));
                            for (int id = 1; id <= _TotalPkmn; id++) {
                                String name = new JSONObject(
                                        pkmnArray.get(id-1)
                                        .toString())
                                        .getString("name");

                                System.out.println("Downloading info from pokemon " + id);
                                DataStorage.save(context, id,  WordUtils.capitalize(name));
                                DataStorage.pokemonObjList.add(createPkmn(
                                        Integer.toString(id),
                                        WordUtils.capitalize(name)));
                            }
                            if (DataStorage.pkmnImagesList.isEmpty())
                                new getImages(context).execute();
                        } catch (Exception e) {
                            Toast.makeText(context, "Error Loading JSON", Toast.LENGTH_SHORT).show();
                            System.out.println(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No response from internet", Toast.LENGTH_LONG).show();
                System.out.println("No response from API");
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
                    String imageName = DataStorage.pokemonObjList.get(i).getName();
                    if (!DataStorage.imageExists(context, imageName)) {
                        System.out.println("Downloading image " + id);
                        String imageUrl = _UrlImages + id + ".png";
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                        DataStorage.pkmnImagesList.add(bitmap);
                        DataStorage.saveImage(imageName, bitmap, context);
                    } else {
                        System.out.println("Loading image " + id);
                        DataStorage.pkmnImagesList.add(DataStorage.loadImage(imageName, context));
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
