package com.lcarrasco.pokedex;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class pokemonListFragment extends Fragment {

    private OnPokemonSelected mListener;

    public static pokemonListFragment newInstance(){
        return new pokemonListFragment();
    }

    public pokemonListFragment() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnPokemonSelected)
            mListener = (OnPokemonSelected) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnPokemonSelected.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final View view = inflater.inflate(R.layout.fragment_pokemon_list, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        recyclerView.setAdapter(new PokemonAdapter(activity));

        return view;
    }

    private class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {
        private LayoutInflater mLayoutInflater;

        protected class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private ImageView mImageView;
            private TextView mNameTextView;

            public ViewHolder(View v) {
                super(v);

                mImageView = (ImageView) v.findViewById(R.id.pokImage);
                mNameTextView = (TextView) v.findViewById(R.id.pokName);
            }

            protected void setData(String name, Bitmap image) {
                mImageView.setImageBitmap(image);
                mNameTextView.setText(name);
            }
        }

        protected PokemonAdapter(Context context){
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mLayoutInflater
                    .inflate(R.layout.recycler_item_pokemon, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final String name = LoadData.pokemonObjList.get(position).getName();
            final Bitmap picture = LoadData.pkmnImagesList.get(position);
            final int id = LoadData.pokemonObjList.get(position).getId();

            holder.setData(name, picture);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPokemonSelected(id);
                }
            });

        }

        @Override
        public int getItemCount() {
            return LoadData.pokemonObjList.size();
        }
    }

    public interface OnPokemonSelected{
        void onPokemonSelected(int id);
    }

//    private class GetImages extends AsyncTask<String, Integer, Long> {
//
//        @Override
//        protected Long doInBackground(String... params) {
//            try {
//                for (int i = 1; i <= totalPkmn ; i++) {
//                    System.out.println("Saving image " + i);
//                    String imageUrl = urlImages.replace("<<id>>", Integer.toString(i));
//                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
//                    pkmnImagesList.add(bitmap);
//                }
//            } catch (MalformedURLException e) {
//                System.out.println("Malformed exception");
//                e.printStackTrace();
//            } catch (IOException e) {
//                System.out.println("IO Exception");
//                e.printStackTrace();
//            } catch (Exception e) {
//                System.out.println("Exception");
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        protected void onProgressUpdate(Integer... progress) {
//
//        }
//
//        protected void onPostExecute(Long result) {
//            System.out.println("Load images Finished");
//
//        }
//    }
}
