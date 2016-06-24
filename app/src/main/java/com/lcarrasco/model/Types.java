package com.lcarrasco.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Types {

    @SerializedName("types")
    @Expose
    private List<Type> types = new ArrayList<Type>();

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

}