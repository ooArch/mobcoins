package com.github.merelysnow.mobcoins.database;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class MobCoinsDatabase {

    private static final String TABLE  = "mobcoins_table";

    public void handleTable() {
        executor().updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "username TEXT NOT NULL," +
                "mobcoins DOUBLE NOT NULL" +
                ");");
    }

    public void insert(User user) {
        executor().updateQuery("REPLACE INTO " + TABLE + " VALUES(?,?)",
                simpleStatement -> {
                    simpleStatement.set(1, user.getName());
                    simpleStatement.set(2, user.getMobcoins());
                });
    }

    public Set<User> fetchAll() {
        return executor().resultManyQuery(
                "SELECT * FROM " + TABLE,
                simpleStatement -> {},
                MobCoinsDatabaseAdapter.class
        );
    }

    public void delete(User user) {
        executor().updateQuery("DELETE FROM " + TABLE + " WHERE username = ?", simpleStatement -> simpleStatement.set(1,  user.getName()));
    }

    private SQLExecutor executor()  {
        return new SQLExecutor(MobCoinsPlugin.CONNECTOR);
    }

    public static class MobCoinsDatabaseAdapter implements SQLResultAdapter<User> {

        @Override
        public User adaptResult(SimpleResultSet resultSet) {

            return new User(
                    resultSet.get("username"),
                    resultSet.get("mobcoins")
            );
        }
    }
}
