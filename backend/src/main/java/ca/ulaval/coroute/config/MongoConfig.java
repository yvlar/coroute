package ca.ulaval.coroute.config;

import jakarta.inject.Singleton;

@Singleton
public class MongoConfig {

  private static final String DEFAULT_URI =
      "mongodb://admin:password@mongo:27017/?authSource=admin";
  private static final String DEFAULT_DB = "coroute";

  private final String connectionString;
  private final String databaseName;

  public MongoConfig() {
    this.connectionString =
        System.getenv("MONGO_URI") != null ? System.getenv("MONGO_URI") : DEFAULT_URI;

    this.databaseName = System.getenv("MONGO_DB") != null ? System.getenv("MONGO_DB") : DEFAULT_DB;
  }

  public String getConnectionString() {
    return connectionString;
  }

  public String getDatabaseName() {
    return databaseName;
  }
}
