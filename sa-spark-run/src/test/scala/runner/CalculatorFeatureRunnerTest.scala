package runner

//import cucumber.api.CucumberOptions
//import cucumber.api.junit.Cucumber
import io.cucumber.junit.{Cucumber, CucumberOptions}
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("classpath:features/calculator.feature"),
  glue = Array("classpath:steps"),
  tags = "@wip",
  monochrome = true,
  plugin = Array("pretty"
    , "html:target/cucumber/report.html"
    )
//  plugin = Array("pretty",
//    "html:target/cucumber",
//    "json:target/cucumber/test-report.json",
//    "junit:target/cucumber/test-report.xml")
)
class CalculatorFeatureRunnerTest {}

//import io.cucumber.junit.{Cucumber, CucumberOptions}
//import org.junit.runner.RunWith

//@RunWith(classOf[Cucumber])
//@CucumberOptions(
//  features = Array("classpath:features/Example.feature"),
//  tags = "not @Wip",
//  glue = Array("classpath:steps"),
//  plugin = Array("pretty", "html:target/cucumber/html"))
//class ExampleFeatureRunnerTest