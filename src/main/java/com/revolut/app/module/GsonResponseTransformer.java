package com.revolut.app.module;

import com.google.gson.Gson;
import spark.ResponseTransformer;

import javax.inject.Inject;

public class GsonResponseTransformer implements ResponseTransformer {
    private final Gson gson;

    @Inject
    public GsonResponseTransformer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
