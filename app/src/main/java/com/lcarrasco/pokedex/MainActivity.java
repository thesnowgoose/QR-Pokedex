package com.lcarrasco.pokedex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lcarrasco.Controller.DownloadData;
import com.lcarrasco.Controller.PokemonRealmStorage;

public class MainActivity extends AppCompatActivity
                    implements PokemonListFragment.OnPokemonSelected,
                               MenuFragment.OnScreenLoading,
                               DownloadData.OnFinishLoading{

    public final static String MEUNU_FRAGMENT = "menu";
    public final static String PKMN_LIST = "listFragment";
    public final static String PKMN_DETAILS = "details";
    public final static String LOADING = "LOADING";

    public final static int PERMISIONS_REQUEST_CAMERA = 999;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PokemonRealmStorage.closeRealm();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.getSupportFragmentManager().popBackStack();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISIONS_REQUEST_CAMERA){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startActivity(new Intent(this, QrScannerActivity.class));

            } else {
                Toast.makeText(this, "You can't scan Pokémon", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void qrScanner(View v) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                Toast.makeText(this, "You can't scan Pokémon", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISIONS_REQUEST_CAMERA);
            }
        } else
            startActivity(new Intent(this, QrScannerActivity.class));
    }

    public void openPokedex(View v){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, PokemonListFragment.newInstance(), PKMN_LIST)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPokemonSelected(int id) {
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

    @Override
    public void onScreenLoading() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
