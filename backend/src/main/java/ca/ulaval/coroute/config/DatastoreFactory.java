package ca.ulaval.coroute.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.inject.Inject;
import org.bson.UuidRepresentation;
import org.glassfish.hk2.api.Factory;

public class DatastoreFactory implements Factory<Datastore> {

  private final Datastore datastore;
  private final MongoClient mongoClient;

  @Inject
  public DatastoreFactory(final MongoConfig mongoConfig) {
    final MongoClientSettings settings = MongoClientSettings.builder()
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .applyConnectionString(new ConnectionString(mongoConfig.getConnectionString()))
        .build();

    this.mongoClient = MongoClients.create(settings);
    this.datastore = Morphia.createDatastore(this.mongoClient, mongoConfig.getDatabaseName());
    this.datastore.getMapper().mapPackage("ca.ulaval.coroute.domain.model");
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Singleton managed by HK2, thread-safe")
  public Datastore provide() {
    return this.datastore;
  }

  @Override
  public void dispose(final Datastore instance) {
    // Close MongoDB client when datastore is disposed
    if (this.mongoClient != null) {
      this.mongoClient.close();
    }
  }
}
