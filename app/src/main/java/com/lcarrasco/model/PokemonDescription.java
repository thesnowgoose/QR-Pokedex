package com.lcarrasco.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PokemonDescription {

    @SerializedName("flavor_text_entries")
    @Expose
    private List<FlavorTextEntry> flavorTextEntries = new ArrayList<FlavorTextEntry>();

    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavorTextEntries;
    }

    public void setFlavorTextEntries(List<FlavorTextEntry> flavorTextEntries) {
        this.flavorTextEntries = flavorTextEntries;
    }

}