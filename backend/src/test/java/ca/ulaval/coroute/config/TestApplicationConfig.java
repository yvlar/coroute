package ca.ulaval.coroute.config;

import ca.ulaval.coroute.domain.model.ReservationFactory;
import ca.ulaval.coroute.domain.model.TrajetFactory;
import ca.ulaval.coroute.domain.service.JwtService;
import ca.ulaval.coroute.domain.service.MatchingService;
import ca.ulaval.coroute.domain.service.TrajetService;
import ca.ulaval.coroute.domain.service.TrajetServiceImpl;
import ca.ulaval.coroute.domain.service.UtilisateurService;
import ca.ulaval.coroute.domain.service.UtilisateurServiceImpl;
import ca.ulaval.coroute.repository.InMemoryTrajetRepository;
import ca.ulaval.coroute.repository.InMemoryUtilisateurRepository;
import ca.ulaval.coroute.repository.TrajetRepository;
import ca.ulaval.coroute.repository.UtilisateurRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class TestApplicationConfig extends ResourceConfig {

        public TestApplicationConfig() {
                packages(
                                "ca.ulaval.coroute.api.controller",
                                "ca.ulaval.coroute.api.mapper",
                                "ca.ulaval.coroute.config");

                register(ApplicationConfig.JacksonConfig.class);

                register(
                                new AbstractBinder() {
                                        @Override
                                        protected void configure() {
                                                // InMemory pour les tests
                                                bind(InMemoryTrajetRepository.class).to(TrajetRepository.class)
                                                                .in(Singleton.class);

                                                bind(InMemoryUtilisateurRepository.class)
                                                                .to(UtilisateurRepository.class)
                                                                .in(Singleton.class);

                                                bind(ReservationFactory.class).to(ReservationFactory.class)
                                                                .in(Singleton.class);

                                                bind(TrajetFactory.class).to(TrajetFactory.class).in(Singleton.class);

                                                bind(TrajetServiceImpl.class).to(TrajetService.class)
                                                                .in(Singleton.class);

                                                bind(UtilisateurServiceImpl.class).to(UtilisateurService.class)
                                                                .in(Singleton.class);

                                                bind(JwtService.class).to(JwtService.class).in(Singleton.class);

                                                bind(MatchingService.class).to(MatchingService.class)
                                                                .in(Singleton.class);
                                        }
                                });
        }

        @Provider
        public static class JacksonConfig implements ContextResolver<ObjectMapper> {
                private final ObjectMapper mapper;

                public JacksonConfig() {
                        this.mapper = new ObjectMapper()
                                        .registerModule(new JavaTimeModule())
                                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                }

                @Override
                public ObjectMapper getContext(final Class<?> type) {
                        return mapper;
                }
        }
}
