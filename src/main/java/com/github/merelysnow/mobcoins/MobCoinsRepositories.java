package com.github.merelysnow.mobcoins;


import com.github.merelysnow.mobcoins.cache.local.MobCoinsLocalCache;
import com.github.merelysnow.mobcoins.database.MobCoinsDatabase;
import com.github.merelysnow.mobcoins.model.User;

public class MobCoinsRepositories {

    public static MobCoinsLocalCache CACHE_LOCAL;
    public static MobCoinsDatabase MYSQL;

    public MobCoinsRepositories() {
        CACHE_LOCAL = new MobCoinsLocalCache();
        MYSQL = new MobCoinsDatabase();

        MYSQL.handleTable();
        for (User allUser : MYSQL.fetchAll()) {
            CACHE_LOCAL.validate(allUser);
        }
    }

}
