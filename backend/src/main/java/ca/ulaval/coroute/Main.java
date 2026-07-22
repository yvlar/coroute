package ca.ulaval.coroute;

import ca.ulaval.coroute.config.ApplicationConfig;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public final class Main {
  public static final String BASE_URI = "http://0.0.0.0:8080/";

  private Main() {
    // Utility class - private constructor to prevent instantiation
  }

  public static void main(final String[] args) throws Exception {
    final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), new ApplicationConfig());

    System.out.println("🚗 CoRoute API démarrée sur http://localhost:8080/");

    final CountDownLatch latch = new CountDownLatch(1);

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.out.println("Arrêt du serveur...");
                  server.shutdown();
                  latch.countDown();
                }));

    latch.await();
  }
}
