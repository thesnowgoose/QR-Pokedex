package com.lcarrasco.pokedex;

import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang3.text.WordUtils;

import me.sargunvohra.lib.pokekotlin.PokeApi;
import me.sargunvohra.lib.pokekotlin.json.PokemonSpecies;

public class QrScannerActivity extends AppCompatActivity
        implements QRCodeReaderView.OnQRCodeReadListener {

    private ProgressBar spinner;
    private SimpleDraweeView pokemonPicture;
    private TextView pokemonTV;
    boolean permittedScanning = true;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

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

    private void eraseScreen(){
        pokemonPicture.setImageBitmap(null);
        pokemonTV.setText("");
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (permittedScanning) {
            eraseScreen();
            spinner.setVisibility(View.VISIBLE);

            permittedScanning = false;
            new FetchPokemon().execute(text);
        }
    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    private class FetchPokemon extends AsyncTask<String, Integer, Long> {
        boolean pokemonNotFound = false;
        String pkmnName;
        Uri uri;

        @Override
        protected Long doInBackground(String... url) {

            try {
                int pkmnID = Integer.parseInt(url[0].split("/pokemon/")[1]);
                PokemonSpecies pkmnInfo = PokeApi.INSTANCE.getPokemonSpecies(pkmnID);

                pkmnName = pkmnInfo.getName();
                System.out.println("MESSAGE: Pokemon identified");

                uri = Uri.parse("http://www.pkparaiso.com/imagenes/xy/sprites/animados/" + pkmnName + ".gif");

            } catch (Exception e) {
                pokemonNotFound = true;
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Long result) {

            spinner.setVisibility(View.GONE);

            if (!pokemonNotFound) {

                pokemonTV.setText(WordUtils.capitalize(pkmnName));
                pokemonPicture.setController(
                        Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setAutoPlayAnimations(true)
                        .build());
            } else {
                Toast.makeText(QrScannerActivity.this, "Pokémon not found", Toast.LENGTH_SHORT).show();
            }
            permittedScanning = true;
        }


    }
}
