package com.ofoegbuvgmail.funbaking.rest;

import com.ofoegbuvgmail.funbaking.model.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingApiInterface {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<ArrayList<Recipe>> getRecipe();
}
