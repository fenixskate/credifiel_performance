package runners;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/flujo-extrajudicial.feature")
@ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "true")
@ConfigurationParameter(key = "cucumber.execution.parallel.config.strategy", value = "fixed")
@ConfigurationParameter(key = "cucumber.execution.parallel.config.fixed.parallelism", value = "5")
@ConfigurationParameter(key = "cucumber.execution.parallel.config.fixed.max-pool-size", value = "5")
public class FlujoExtrajudicialRunner {}
