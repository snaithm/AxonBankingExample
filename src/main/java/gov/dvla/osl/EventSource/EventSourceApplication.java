package gov.dvla.osl.EventSource;

import gov.dvla.osl.EventSource.resources.AccountResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class EventSourceApplication extends Application<EventSourceConfiguration> {

    public static void main(final String[] args) throws Exception {
        new EventSourceApplication().run(args);
    }

    @Override
    public String getName() {
        return "Axon Banking Example";
    }

    @Override
    public void initialize(final Bootstrap<EventSourceConfiguration> bootstrap) {

        bootstrap.addBundle(new SwaggerBundle<EventSourceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(EventSourceConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(final EventSourceConfiguration configuration,
                    final Environment environment) {

        environment.jersey().register(new AccountResource());
    }
}