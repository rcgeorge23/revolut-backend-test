package com.revolut.app.module;

import com.google.gson.*;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.revolut.app.api.CreateTransactionRoute;
import com.revolut.app.api.GetTransactionsRoute;
import com.revolut.app.dao.AccountDao;
import com.revolut.app.dao.TransactionDao;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import static java.lang.Long.parseLong;

public class ApplicationModule extends AbstractModule {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Timestamp.class, new TimestampJsonDeserializer())
            .registerTypeAdapter(Timestamp.class, new TimestampJsonSerializer())
            .create();

    @Override
    protected void configure() {
        bind(TransactionDao.class).in(Scopes.SINGLETON);
        bind(AccountDao.class).in(Scopes.SINGLETON);
        bind(GetTransactionsRoute.class).in(Scopes.SINGLETON);
        bind(CreateTransactionRoute.class).in(Scopes.SINGLETON);
        bind(GsonResponseTransformer.class).in(Scopes.SINGLETON);
        bind(Gson.class).toProvider(() -> GSON);
    }

    public static class TimestampJsonDeserializer implements JsonDeserializer<Timestamp> {
        @Override
        public Timestamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Timestamp(parseLong(jsonElement.getAsJsonPrimitive().getAsString()));
        }
    }

    public static class TimestampJsonSerializer implements JsonSerializer<Timestamp> {
        @Override
        public JsonElement serialize(Timestamp timestamp, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(timestamp.toInstant().toEpochMilli());
        }
    }
}
