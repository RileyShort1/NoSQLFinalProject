package org.example.nosqlfinalproject;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public final class Neo4jClient implements AutoCloseable {
    private final Driver driver;

    public Neo4jClient(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public Driver getDriver() { return driver;}

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
