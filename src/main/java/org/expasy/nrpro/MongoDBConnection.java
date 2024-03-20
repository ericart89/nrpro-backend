package org.expasy.nrpro;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoDBConnection ourInstance = new MongoDBConnection();
    private MongoClient client;
    private MongoDatabase database;

    public static MongoDBConnection getInstance() {
        return ourInstance;
    }

    private MongoDBConnection() {
        System.out.println("Creating client");
        this.client = new MongoClient("db", 27017);
        this.database = this.client.getDatabase("nrpro");
    }

    public MongoCollection getCollection(String name) {
        return this.database.getCollection(name);
    }

    public void close() {
        System.out.println("close...");
        this.client.close();
    }
}
