package com.lcarrasco.pokedex;

import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lcarrasco.Controller.DownloadData;
import com.lcarrasco.Controller.PokemonRealmStorage;
import com.lcarrasco.model.IPokemonApi;
import com.lcarrasco.model.Pokemon;

import org.apache.commons.lang3.text.WordUtils;

import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QrScannerActivity extends AppCompatActivity
        implements QRCodeReaderView.OnQRCodeReadListener {

    private ProgressBar spinner;
    private SimpleDraweeView pokemonPicture;
    private TextView pokemonTV;

    private boolean permittedScanning = true;
    private String urlPokeApi = "http://pokeapi.co/api/v2/";

    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        pokemonPicture = (SimpleDraweeView) findViewById(R.id.pokemonPicture);
        pokemonTV = (TextView) findViewById(R.id.pokemonTextView);

        QRCodeReaderView mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        pokemonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseScreen();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void eraseScreen(){

        if (id != 0 && id <= DownloadData._totalPkmn) {
            if (!PokemonRealmStorage.alreadyCaptured(id))
                PokemonRealmStorage.updateCapturedState(id, true);
            else
                Toast.makeText(getApplication(), "Pokemon already captured", Toast.LENGTH_SHORT).show();
            id = 0;
        }

        pokemonPicture.setImageBitmap(null);
        pokemonTV.setText("");
    }

    @Override
    public void onQRCodeRead(String data, PointF[] points) {
        if (permittedScanning) {
            eraseScreen();
            spinner.setVisibility(View.VISIBLE);

            permittedScanning = false;
            consumePokeApi(data);
        }
    }

    private void consumePokeApi(String data) {

        String pkmnID;
        final boolean pokemonNotFound = false;

        if (!validQRCode(data))
            showMissingNo(pokemonNotFound);
        else {
            pkmnID = data.split(":code")[1];
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(urlPokeApi)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            IPokemonApi pokeApiService = retrofit.create(IPokemonApi.class);
            Call<Pokemon> pokemonCall = pokeApiService.getPokemon(pkmnID);

            pokemonCall.enqueue(new Callback<Pokemon>() {
                Uri uri;

                @Override
                public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                    int statusCode = response.code();
                    Pokemon p = response.body();
                    if (statusCode == 200) {
                        id = p.getId();
                        new GetPokemonImage(pokemonNotFound).execute(p.getName());
                    }
                    else {
                        Toast.makeText(getApplication(), "Unable to connect", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<Pokemon> call, Throwable t) {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(getApplication(), "Unable to connect", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showMissingNo(boolean pokemonNotFound){
        new GetPokemonImage(!pokemonNotFound).execute();
    }

    private boolean validQRCode(String qrCodeString){
        return qrCodeString.matches("\\w{5}[lk4r1a]\\w{5}[ek4r1a]\\w{5}[0k4r1a]:code[0-9]+");
    }

    @Override
    public void cameraNotFound() {
        Toast.makeText(this, "Camera not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    private class GetPokemonImage extends AsyncTask<String, Integer, Long> {

        private String pkmnName;
        private boolean pokemonNotFound = false;
        private Uri uri;

        public GetPokemonImage(boolean pokemonNotFound){
            this.pokemonNotFound = pokemonNotFound;
        }

        private boolean exists(URL url){
            try {
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");  //OR  huc.setRequestMethod ("HEAD");
                huc.connect();
                int code = huc.getResponseCode();

                if (code == 200)
                    return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected Long doInBackground(String... args) {
            if (args.length > 0)
                pkmnName = args[0];

            if (pokemonNotFound) {
                uri = Uri.parse("http://vignette3.wikia.nocookie.net/mugen/images/2/20/Missing-.gif");
                pkmnName = "?";
            } else {
                System.out.println("MESSAGE: Pokemon identified");
                String pkmnImage = "http://www.pokestadium.com/sprites/black-white/animated/" + pkmnName + ".gif";
                try {
                    if (exists(new URL(pkmnImage)))
                        uri = Uri.parse(pkmnImage);
                    else
                        uri = Uri.parse("http://www.pkparaiso.com/imagenes/xy/sprites/animados/" + pkmnName + ".gif");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Long result) {
            spinner.setVisibility(View.GONE);

            pokemonTV.setText(WordUtils.capitalize(pkmnName));
            pokemonPicture.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setUri(uri)
                            .setAutoPlayAnimations(true)
                            .build());

            pokemonPicture.getHierarchy()
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

            permittedScanning = true;
        }
    }
}
