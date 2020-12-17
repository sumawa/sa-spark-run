package steps

import com.sa.sparkrun.SparkRunMain
//import cucumber.api.Scenario
//import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.scala.{EN, ScalaDsl}

class SparkRunMainSteps extends ScalaDsl with EN {
  var srm: cats.effect.IO[cats.effect.ExitCode] = _
  var result: cats.effect.ExitCode = _

  Given("""^SparkRunMain is launched$"""){ () =>
    val srm = com.sa.sparkrun.SparkRunMain.run(List(java.util.UUID.randomUUID().toString))
  }

  When("""^I count args $"""){ (firstNum:Int, secondNum:Int) =>
    result = srm.unsafeRunSync()
  }
//  Then("""^result should ExitCode$"""){ (expectedResult:Int) =>
//    assert(result == expectedResult, "Incorrect result of calculator computation")
//  }

//  When("""^I add (\d+) and (\d+)$"""){ (firstNum:Int, secondNum:Int) =>
//    result = calc.add(firstNum, secondNum) + 100
//  }
//  Then("""^result should be equal to (\d+)$"""){ (expectedResult:Int) =>
//    assert(result == expectedResult, "Incorrect result of calculator computation")
//  }
//  When("""^I subtract (\d+) and (\d+)$"""){ (firstNum:Int, secondNum:Int) =>
//    result = calc.sub(firstNum, secondNum)
//  }
}