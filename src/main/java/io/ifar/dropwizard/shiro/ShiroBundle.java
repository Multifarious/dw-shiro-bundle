package io.ifar.dropwizard.shiro;

import com.google.common.base.Optional;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.eclipse.jetty.server.session.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple bundle class to initialze Shiro within Dropwizard.
 */
public abstract class ShiroBundle<T extends Configuration>
        implements ConfiguredBundle<T>, ConfigurationStrategy<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ShiroBundle.class);

    /**
     * This method is a no-op.  All functionality is in the {@link #run(Object, com.yammer.dropwizard.config.Environment)} method.
     * @param bootstrap ignored
     */
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // nothing to see here, all the action takes place from the run() method.
    }

    /**
     * Conditionally configures Dropwizard's environment to enable Shiro elements. The condition being: if there is a ShiroConfiguration present in the provided {@code configuration}, and its {@code enabled} field is set to {@code true}.
     *
     * @param configuration  used to retrieve the (optional) {@link ShiroConfiguration} instance.
     * @param environment  this is what gets configured
     * @throws Exception
     */
    @Override
    public void run(final T configuration, Environment environment) throws Exception {
        final Optional<ShiroConfiguration> shiroConfig = getShiroConfiguration(configuration);
        if (shiroConfig.isPresent()) {
            LOG.debug("Shiro is configured: {}", shiroConfig);
            initializeShiro(shiroConfig.get(), environment);
        } else {
            LOG.debug("Shiro is not configured");
        }
    }

    private void initializeShiro(final ShiroConfiguration config, Environment environment) {
        if (config.isEnabled()) {
            LOG.debug("Shiro is enabled");

            if (config.isDropwizardSessionHandler() && environment.getSessionHandler() == null) {
                LOG.debug("Adding DropWizard SessionHandler to environment.");
                environment.setSessionHandler(new SessionHandler());
            }

            // This line ensure Shiro is configured and its .ini file found in the designated location.
            // e.g., via the shiroConfigLocations ContextParameter with fall-backs to default locations if that parameter isn't specified.
            environment.addServletListeners(new EnvironmentLoaderListener());

            final String filterUrlPattern = config.getSecuredUrlPattern();
            LOG.debug("ShiroFilter will check URLs matching '{}'.", filterUrlPattern);
            environment.addFilter(new ShiroFilter(), filterUrlPattern).setName("shiro-filter");
        } else {
            LOG.debug("Shiro is not enabled");
        }
    }

}
