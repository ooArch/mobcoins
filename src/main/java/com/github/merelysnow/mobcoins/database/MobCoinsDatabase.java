package com.github.merelysnow.mobcoins.database;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.database.connection.RepositoryProvider;
import com.github.merelysnow.mobcoins.model.User;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MobCoinsDatabase extends RepositoryProvider {

    private static final String TABLE_NAME = "mobcoins_table";
    private SQLExecutor sqlExecutor;

    public MobCoinsDatabase(Plugin plugin) {
        super(plugin);
        this.prepare();

        this.handleTable();
    }

    @Override
    public SQLConnector prepare() {
        final SQLConnector connector = super.prepare();
        this.sqlExecutor = new SQLExecutor(connector);

        return connector;
    }

    public void handleTable() {
        this.sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "name VARCHAR(33) NOT NULL," +
                "mobcoins DOUBLE NOT NULL" +
                ");");
    }

    public Set<User> selectMany() {
        return this.sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE_NAME, simpleStatement -> {
                }, RankingDataBaseAdapter.class);
    }

    public void insert(User user) {
        CompletableFuture.runAsync(() -> {
            this.sqlExecutor.updateQuery("REPLACE INTO " + TABLE_NAME + " VALUES(?,?)", simpleStatement -> {
                simpleStatement.set(1, user.getName());
                simpleStatement.set(2, user.getMobcoins());
            });
        });
    }

    public void deleteOne(User user) {
        this.sqlExecutor.updateQuery(
                "DELETE FROM " + TABLE_NAME + " WHERE name = ?", simpleStatement -> simpleStatement.set(1, user.getName()));
    }

    public static class RankingDataBaseAdapter implements SQLResultAdapter<User> {

        @Override
        public User adaptResult(SimpleResultSet resultSet) {

            return new User(
                    resultSet.get("name"),
                    resultSet.get("mobcoins")
            );
        }
    }
}
