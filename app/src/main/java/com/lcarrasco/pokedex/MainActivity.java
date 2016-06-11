package com.lcarrasco.pokedex;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity
                    implements pokemonListFragment.OnPokemonSelected{

    String url = "http://www.google.com";
    public final static String mainFragment = "mainFragment";
    public final static String qrFragment = "qrFragment";
    public final static String pkmnListFragment = "listFragment";
    public final static String pkmnDetails = "pokemonDetails";
    public final static String loadingFragment = "loading";


//    TextView text;

//    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//            new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    // Display the first 500 characters of the response string.
//                    text.setText("Response is: "+ response.substring(0,500));
//                }
//            }, new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            text.setText("That didn't work!");
//        }
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        text = (TextView)findViewById(R.id.text);

//        Button btn = (Button)findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendRequest4();
//            }
//        });

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.main_layout, LoadingFragment.newInstance(), loadingFragment)
//                .commit();

        LoadData.start(this);

        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_layout, MainFragment.newInstance(), mainFragment)
                    .commit();
        }
    }

    public void qrScanner(View v){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, qrScannerFragment.newInstance(), qrFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void openPokedex(View v){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, pokemonListFragment.newInstance(), pkmnListFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPokemonSelected(int id) {
        System.out.println(id);
        pokemonDetailsFragment fragment = pokemonDetailsFragment.newInstance(id);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, fragment, pkmnDetails)
                .addToBackStack(null)
                .commit();
    }

    //    String url2 = "http://pokeapi.co/api/v2/pokemon/?limit=151";
//
//    JsonObjectRequest JsonObj = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
//
//        @Override
//        public void onResponse(JSONObject response) {
//            String name = "";
//
//            try {
//                name = response.getString("results");
//                JSONArray pokemonList = new JSONArray(name);
//                for (int i = 0; i < pokemonList.length() ; i++) {
//                    JSONObject singlePkmn = new JSONObject(pokemonList.get(i).toString());
//                    String pkmnName = singlePkmn.getString("name");
//                    System.out.println(pkmnName);
//                }
//
//            } catch (Exception e){
//                System.out.println(e.getMessage());
//            }
//
//            text.setText("Response: " + name );
//        }
//    }, new Response.ErrorListener() {
//
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            text.setText("Error");
//
//        }
//    });

//    private void sendRequest(){
//
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        // Request a string response from the provided URL.
//
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//    }
//
//    private void sendRequest2() {
//
//        RequestQueue mRequestQueue;
//
//        // Instantiate the cache
//        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
//
//        // Set up the network to use HttpURLConnection as the HTTP client.
//        Network network = new BasicNetwork(new HurlStack());
//
//        // Instantiate the RequestQueue with the cache and network.
//        mRequestQueue = new RequestQueue(cache, network);
//
//        // Start the queue
//        mRequestQueue.start();
//
//        // Add the request to the RequestQueue.
//        mRequestQueue.add(stringRequest);
//        //mRequestQueue.stop();
//    }
//
//    private void sendRequest3(){
//
//        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
//                getRequestQueue();
//
//        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
//    }
//
//    private void sendRequest4(){
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        queue.add(JsonObj);
//    }

}
