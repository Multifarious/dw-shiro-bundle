# io.ifar: dw-shiro-bundle

ShiroBundle for DropWizard.

Simplifies Shiro configuration and usage in a DropWizard stack.

## Functionality

The bundle initializes Shiro to work within Dropwizard.  The functionality has three steps:

1. Optionally add a Jetty SessionHandler to the Dropwizard environment.  This is done only if a) the `ShiroConfiguration` `dropwizard_session_handler` property is`true`, and b) the environment doesn't already have a SessionHandler set.
2. Add the Shiro `EnvironmentLoaderListener` to the Dropwizard environment.  This will initialize Shiro.
3. Add a `ShiroFilter` instance to handle URLs matching the configured pattern.

The rest of the functionality and configuration are provided by Shiro and/or Dropwizard directly.

## Usage

Include [the maven dependency](#access-with-maven).

Add an instance of the ShiroBundle to your service. For example,

    ...
    import io.ifar.dropwizard.shiro.ShiroBundle;
    import io.ifar.dropwizard.shiro.ShiroConfiguration;
    ...
    private final ShiroBundle shiroBundle =
            new ShiroBundle<MyConfiguration>() {
                @Override public Optional<ShiroConfiguration> getShiroConfiguration(final MyConfiguration configuration)
                { return Optional.<ShiroConfiguration>fromNullable(configuration.getShiroConfiguration()); }
            };
    ...
    @Override
    public void initialize(final Bootstrap<MyConfiguration> bootstrap) {
        ...
        bootstrap.addBundle(this.shiroBundle);
        ...
    }
    ...

Expose the `ShiroConfiguration` via you application's root `Configuration` class.  For example,

    ...
    import com.yammer.dropwizard.config.Configuration;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import ifar.dropwizard.shiro.ShiroConfiguration;

    import javax.validation.Valid;
    ...

    public class MyConfiguration extends Configuration {

        @Valid
        @JsonProperty("shiro_configuration")
        private ShiroConfiguration shiroConfiguration = new ShiroConfiguration();

        ...
        public ShiroConfiguration getShiroConfiguration() {
            return shiroConfiguration;
        }
        ....
    }


## Configuration

See the `ShiroConfiguration` class.  The minimal configuration, via your Dropwizard application's `.yml` file is:

    shiro_configuration:
        enabled: true

The bundle has three configurable properties:

* `enabled` (`boolean`) : turns on/off this bundle.  If not enabled, the bundle will write some log lines but will not perform configuration.
* `filter_url_pattern` (`String`) : URLs matching this pattern will be intercepted by the `ShiroFilter.`  The default value is `/*`.
* `dropwizard_session_handler` (`boolean`) : whether or not to enable the Jetty `SessionHandler`.  Defaults to `false.`  (See, [Session state required](#session-state-required), below.)

### Related Shiro and Dropwizard Configuration

As part of its initialization, Shiro will load an `.ini` file from the location specified in the `shiroConfigLocations` context parameter.  You can set this via your applications' `.yml` file.  For example:

    http:
        ...
        contextParameters:
            shiroConfigLocations: file:./shiro.ini

If the parameter is unset, Shiro will look for an `.ini` file in the default locations: inside the `WEB-INF` folder and at the root of the classpath.  So, a `shiro.ini` file at the root of your classpath (e.g., in your `resources` folder if using Maven) is all you need.


### Session state required

Note that most of Shiro's services require server-side state be stored.  There are multiple routes available.  One is to set

    dropwizard_session_handler: true

via the ShiroConfiguration of this bundle (or otherwise add a DropWizard SessionHandler to the DropWizard Environment) and Shiro will piggy-back on the HttpServlet session.  

Alternatively, configure Shiro's SecurityManager to use Shiro's built-in session management, for example in the `shiro.ini` via lines like these:

    sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager
    securityManager.sessionManager = $sessionManager

## Access with Maven

### Coordinates

Include the following in your `pom.xml`:

    <dependency>
      <groupId>io.ifar</groupId>
      <artifactId>dw-shiro-bundle</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>

### Snapshots

Snapshots are available from the following Maven repository:

    <repository>
      <id>multifarious-snapshots</id>
      <name>Multifarious, Inc. Snapshot Repository</name>
      <url>http://repository-multifarious.forge.cloudbees.com/snapshot/</url>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
      <releases>
        <enabled>false</enabled>
      </releases>
    </repository>

### Releases

None as yet, but when there are, they will be published via Maven Central.

## License

The license is [BSD 2-clause](http://opensource.org/licenses/BSD-2-Clause).  This information is also present in the `LICENSE.txt` file and in the `pom.xml`.