# Version 3.3.0 (2016-?-?)

* [brk] `storage` configuration lookup has been removed.

# Version 2.3.0 (2016-04-25)

* [new] Full compatibility with Java 8.
* [new] Interface `LifecycleListener` provides the ability to execute code upon application startup and shutdown.
* [new] Global facade (class `Seed`) for kernel creation and disposal.
* [new] Auto-configuration of Logback when it is in use and no `logback.xml` file exists.
* [new] Best-effort to detect console color output in various runtime environments.
* [new] Ability to print a custom banner upon startup by providing a `banner.txt` file in the default package.
* [chg] Update to official Guice 4.0 (not using Sisu-Guice anymore).
* [chg] Improve log traces on startup errors.
* [chg] Better default log format.
* [chg] Update parent pom to [2.4.0](https://github.com/seedstack/poms/releases/tag/v2.4.0)
* [chg] Update `commons-configuration` to 1.10.
* [chg] Update `commons-cli` to 1.3.1.
* [chg] Update `shiro` to 1.2.4.
* [chg] Update `undertow` to 1.3.19.Final.
* [chg] Update `jodd` to 3.6.6.
* [chg] Update `metrics` to 3.1.2.
* [chg] Update `jersey1` to 1.19.1.
* [chg] Update `jersey2` to 2.22.2.
* [fix] Correctly injects `@Logging`-annotated inherited loggers.

## Web

* [new] Fully-injectable and interceptable servlets, filters and listeners.
* [new] Full compatibility with asynchronous servlets and filters.
* [new] Ability to programatically register servlets, filters and listeners.
* [chg] WebSocket support, previously in `seed-web-websocket` module is merged into `seed-web-core` module.
* [brk] Compatibility with Servlet 2.5 is dropped.
* [brk] Custom Servlet annotations (`@WebServlet`, `@WebFilter` and `@WebListener`) are dropped in favor or standard ones.

## Rest

* [new] Full support for JAX-RS 2 asynchronous resources.
* [new] Detection of BeanParam classes in HAL scanner.
* [chg] Automatically prepends the servlet context path to generated HAL links.

## EL

* [new] Add support for Expression Language 3

## Testing

* [fix] Correctly take inheritance into account in expected IT exceptions
* [chg] Update Tomcat version to 8.0.32 for Arquillian tests.

# Version 2.2.1 (2016-03-22)

## Rest

* [new] Support configuration of Jersey 2 features. Automatically enable multipart and JSP features if detected on the classpath.
* [new] Add multipart feature as a dependency of Jersey 2 module, enabling it by default.

# Version 2.2.0 (2016-01-28)

* [fix] Fix the `@Ignore` annotation which was not working anymore in version 2.1.0.

## Rest

* [chg] `RelRegistry` automatically prepends the servlet context path to generated HAL links.

## Web

* [new] JAX-RS 2 support through Jersey 2.
* [new] Applications can launch in a Servlet 3+ environment without web.xml file.
* [brk] Remove the `org.seedstack.seed.web.DelegateServletContextListener` interface which can be replaced by native servlet listeners.

## Security

* [chg] Disable storage of security sessions by default (can be re-enabled by setting `org.seedstack.seed.security.sessions.enabled` to true)
* [chg] Sets the default security session timeout to 15 minutes (instead of 30 minutes before) when sessions are enabled
* [new] Security session timeout can be changed with the `org.seedstack.seed.security.sessions.timeout` property (in seconds).
* [new] Add an anti-XSRF security filter (named `xsrf`) which can be used in Web security filter chains to prevent XSRF attacks.

# Version 2.1.0 (2015-11-26)

* [brk] Merged dedicated test modules into with their core implementation.
* [brk] Merged multiple testing modules into a unique one named `seed-testing`.
* [brk] Simplified the naming convention of all modules by getting rid of the `support` word.
* [brk] Simplified the framework by factoring-out numerous modules as SeedStack add-ons (http://seedstack.org/addons/).
* [new] Final version of cryptography support in the `seed-crypto` module.

## Testing

* [brk] Changed the SPI of integration testing plugins.

## Web

* [new] Added Undertow embedded-server support in `seed-web-undertow` module. 
* [brk] Moved Jersey 1 implementation in its own module `seed-rest-jersey1`.

# Version 2.0.0 (2015-07-30)

* [new] Initial Open-Source release.
