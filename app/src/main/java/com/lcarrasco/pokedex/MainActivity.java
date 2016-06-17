package com.lcarrasco.pokedex;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity
                    implements PokemonListFragment.OnPokemonSelected,
                               DownloadData.OnFinishLoading {

    public final static String MEUNU_FRAGMENT = "menu";
    public final static String QR_FRAGMENT = "QR_FRAGMENT";
    public final static String PKMN_LIST = "listFragment";
    public final static String PKMN_DETAILS = "details";
    public final static String LOADING = "LOADING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_layout, LoadingFragment.newInstance(), LOADING)
                .commit();

        DownloadData.start(this, this);
    }

    public void qrScanner(View v){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, QrScannerFragment.newInstance(), QR_FRAGMENT)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void openPokedex(View v){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, PokemonListFragment.newInstance(), PKMN_LIST)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPokemonSelected(int id) {
        System.out.println(id);
        PokemonDetailsFragment fragment = PokemonDetailsFragment.newInstance(id);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, fragment, PKMN_DETAILS)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFinishLoading() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, MenuFragment.newInstance(), MEUNU_FRAGMENT)
                .commit();
    }

}
