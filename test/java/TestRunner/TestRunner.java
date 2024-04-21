package TestRunner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

// Test runner to run the API scenario
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/test.feature"
        ,glue={"Pages"},
        tags= "@test"
)
public class TestRunner {

    }