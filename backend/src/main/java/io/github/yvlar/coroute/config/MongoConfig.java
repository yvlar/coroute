package io.github.yvlar.coroute.config;

import jakarta.inject.Singleton;

@Singleton
public class MongoConfig {

  /** Fallback local sans identifiants — les identifiants réels viennent de {@code MONGO_URI}. */
  private static final String DEFAULT_URI = "mongodb://localhost:27017";

  private static final String DEFAULT_DB = "coroute";

  private final String connectionString;
  private final String databaseName;

  public MongoConfig() {
    this(System.getenv("MONGO_URI"), System.getenv("MONGO_DB"));
  }

  MongoConfig(final String uri, final String db) {
    this.connectionString = uri != null && !uri.isBlank() ? uri : DEFAULT_URI;
    this.databaseName = db != null && !db.isBlank() ? db : DEFAULT_DB;
  }

  public String getConnectionString() {
    return connectionString;
  }

  public String getDatabaseName() {
    return databaseName;
  }
}
