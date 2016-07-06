package com.lcarrasco.pokedex;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcarrasco.Controller.PokemonRealmStorage;

public class PokemonListFragment extends Fragment {

    private OnPokemonSelected mListener;
    private static PokemonListFragment instance;

    public static PokemonListFragment newInstance(){
        if (instance == null)
            instance = new PokemonListFragment();
        return instance;
    }

    public PokemonListFragment() { }

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
            private FrameLayout frameLayout;

            public ViewHolder(View v) {
                super(v);

                mImageView = (ImageView) v.findViewById(R.id.pokImage);
                mNameTextView = (TextView) v.findViewById(R.id.pokName);
                frameLayout = (FrameLayout) v.findViewById(R.id.frame);
            }

            protected void setData(String name, Bitmap image, boolean captured) {
                mImageView.setImageBitmap(image);
                mNameTextView.setText(name);
                if (captured)
                    frameLayout.setForeground(getResources().getDrawable(R.drawable.pokeball));
                else
                    frameLayout.setForeground(null);
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
            try {
                if (position < PokemonRealmStorage.pokemonList.size()) {
                    final int id = PokemonRealmStorage.pokemonList.get(position).getId();
                    final String name = PokemonRealmStorage.pokemonList.get(position).getName();
                    final boolean captured = PokemonRealmStorage.pokemonList.get(position).isCaptured();
                    final Bitmap picture = PokemonRealmStorage.pkmnImagesList.get(position);

                    holder.setData(name, picture, captured);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onPokemonSelected(id);
                        }
                    });
                }
            } catch (Exception e) {
                System.out.println("Error PokemonListFragment: onBindViewHolder - " + e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return PokemonRealmStorage.pokemonList.size();
        }
    }

    public interface OnPokemonSelected{
        void onPokemonSelected(int id);
    }

}
