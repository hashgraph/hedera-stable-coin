package com.hedera.hashgraph.stablecoin.app;

import io.github.cdimascio.dotenv.Dotenv;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class SqlConnectionManager {
    private final String url;

    private final String database;

    private final String username;

    private final String password;

    @Nullable
    private Connection connection;

    public SqlConnectionManager(Dotenv env) {
        url = Objects.requireNonNull(env.get("HSC_DATABASE_URL"), "missing environment variable DATABASE_URL");

        database = Objects.requireNonNull(env.get("HSC_POSTGRES_DB"), "missing environment variable HSC_POSTGRES_DB");

        username = Objects.requireNonNull(
            env.get("HSC_DATABASE_USERNAME"), "missing environment variable DATABASE_USERNAME");

        password = Objects.requireNonNull(
            env.get("HSC_DATABASE_PASSWORD"), "missing environment variable DATABASE_PASSWORD");
    }

    // Blank SqlConnectionManager for testing
    public SqlConnectionManager() {
        url = "";
        database = "";
        username = "";
        password = "";
    }

    public DSLContext dsl() throws SQLException {
        return DSL.using(getConnection(), SQLDialect.POSTGRES);
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = newConnection();
        }

        return connection;
    }

    private Connection newConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:" + url + database, username, password);
    }
}
