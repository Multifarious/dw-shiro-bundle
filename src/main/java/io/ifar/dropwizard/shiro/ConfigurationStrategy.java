package io.ifar.dropwizard.shiro;

import com.google.common.base.Optional;
import com.yammer.dropwizard.config.Configuration;

/**
 * Interface for accessing the optional ShiroConfiguration section from a DropWizard service configuration.
 */
public interface ConfigurationStrategy<T extends Configuration> {

        /**
         * Returns the Shiro configuration element from the specified
         * service configuration.
         *
         * @param configuration the service configuration
         * @return the embedded ShiroConfiguration instance
         */
        Optional<ShiroConfiguration> getShiroConfiguration(T configuration);

}
