package ca.ulaval.coroute.repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.bson.UuidRepresentation;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class InMongoTrajetRepositoryTest extends TrajetRepositoryTest {

  @Container
  private final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

  @Override
  protected TrajetRepository createTrajetRepository() {
    final MongoClientSettings settings = MongoClientSettings.builder()
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
        .build();

    final MongoClient mongoClient = MongoClients.create(settings);
    final Datastore datastore = Morphia.createDatastore(mongoClient, "testCoroute");
    datastore.getMapper().mapPackage("ca.ulaval.coroute.domain.model");

    return new MongoTrajetRepository(datastore, trajetFactory);
  }
}
