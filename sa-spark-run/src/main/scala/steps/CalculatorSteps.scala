package steps

import cats.effect.{ContextShift, ExitCode, IO, Timer}
import com.sa.sparkrun.{Calculator, SparkRunMain}
//import cucumber.api.Scenario
//import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.scala.{ScalaDsl,EN}
import org.slf4j.LoggerFactory

class CalculatorSteps extends ScalaDsl with EN {
  var calc: Calculator = _
  var result: Int = _
  var resIO: IO[ExitCode] = _

  Given("""^my calculator is running$"""){ () =>
    calc = new Calculator
  }
  When("""^I add (\d+) and (\d+)$"""){ (firstNum:Int, secondNum:Int) =>
    result = calc.add(firstNum, secondNum) + 100
  }
  Then("""^result should be equal to (\d+)$"""){ (expectedResult:Int) =>
    assert(result == expectedResult, "Incorrect result of calculator computation")
  }
  When("""^I subtract (\d+) and (\d+)$"""){ (firstNum:Int, secondNum:Int) =>
    result = calc.sub(firstNum, secondNum)
  }

  Given("""^servers are running$"""){ () =>
//    calc = new Calculator
  }

  When("""SOMETHING SOMETHING""") { () =>
    // Write code here that turns the phrase above into concrete actions
    //    throw new io.cucumber.scala.PendingException()

    implicit val glo = scala.concurrent.ExecutionContext.Implicits.global
    implicit val cs: ContextShift[IO] = IO.contextShift(glo)
    implicit val timer: Timer[IO] = IO.timer(glo)

    System.setProperty("SPARKRUN_ENV","test")
    System.setProperty("SPARKRUN_TYPE","yarn")

    resIO = SparkRunMain.run(List())


  }
  Then("""^resIO should be equal to ExitCode$"""){ (expectedResult:Int) =>
    assert(resIO.unsafeRunSync() == ExitCode.Success, "Incorrect result of calculator computation")
  }

}