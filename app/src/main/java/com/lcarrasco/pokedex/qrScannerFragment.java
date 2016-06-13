package com.lcarrasco.pokedex;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import me.sargunvohra.lib.pokekotlin.PokeApi;
import me.sargunvohra.lib.pokekotlin.json.PokemonSpecies;

public class qrScannerFragment extends Fragment
        implements QRCodeReaderView.OnQRCodeReadListener{


    private ProgressBar spinner;
    private ImageView pokemonPicture;
    private TextView pokemonTV;
    boolean permittedScanning = true;
    View view;

    public static qrScannerFragment newInstance(){
        return new qrScannerFragment();
    }

    public qrScannerFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);
        spinner = (ProgressBar) view.findViewById(R.id.progressBar);
        pokemonPicture = (ImageView) view.findViewById(R.id.pokemonPicture);
        pokemonTV = (TextView) view.findViewById(R.id.pokemonTextView);

        QRCodeReaderView mydecoderview = (QRCodeReaderView) view.findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        pokemonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseScreen();
            }
        });

        return view;
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
        Bitmap bitmap;

        @Override
        protected Long doInBackground(String... params) {
            String imageUrl;

            try {
                int pkmnID = Integer.parseInt(params[0].split("/pokemon/")[1]);
                PokemonSpecies pkmnInfo = PokeApi.INSTANCE.getPokemonSpecies(pkmnID);

                pkmnName = pkmnInfo.getName();
                System.out.println("MESSAGE: Pokemon successfully identified");
                imageUrl = "http://pokeapi.co/media/img/" + pkmnID + ".png";
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());

            } catch (Exception e) {
                pokemonNotFound = true;
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
//            if (pokemonPicture)
//                pokemonPicture.setImageBitmap(bitmap);


        }

        protected void onPostExecute(Long result) {

            spinner.setVisibility(View.GONE);

            if (!pokemonNotFound) {

                pokemonTV.setText(pkmnName);
                pokemonPicture.setImageBitmap(bitmap);

                Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

                pokemonPicture.startAnimation(fadeInAnimation);
            } else {
                Toast.makeText(getContext(), "Pokémon not found", Toast.LENGTH_SHORT).show();
            }
            permittedScanning = true;
        }

    }

}
