package com.sa.sparkrun

import com.sa.sparkrun.SparkRunTest._
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.handlers.SpSuccess
import org.scalacheck.Gen
import scala.meta.Defn.Given

class SparkRunBootstrapTest extends FeatureBaseSpec {

  import cats.effect.IO
  import fs2.Stream
//  "Spark Run initialisation" should "fail with RuntimeException being thrown if Spark Run end points are down" in {
  feature("Spark Run initialisation") {
    scenario("fail with RuntimeException being thrown if Spark Run end points are down") {
      def sparkResponse(job: Job) = for {
        sparkRunner <- Stream.eval(IO(yarnSparkRunner))
        _ <- Stream.eval(IO(println(s"sparkRunner: ${sparkRunner}")))
      } yield (sparkRunner)
      def handleError(e: String, job: Job) ={
        info(s"%%%%%%%%% HANDLE ERROR: ${e}")
        false;
      }
      def handleSuccess(sp:SpSuccess, job: Job) ={
        info(s"%%%%%%%%%% HANDLE SUCCESS: ${sp}")
        true
      }
      val balance = 100.5
      Given(s"""Spark Server is NOT running $$$balance""")

      val runnerNotRunning = try {(sparkResponse(correctJob).compile.toList.unsafeRunSync())(0); true} catch {
        case ex:
          Exception => println(s"%%%%% SOME CLIENT INITIALIZATION ERROR ${ex.getMessage}")
          false
      }
      When(s"if submit request is made ")
      Then(s"the spark run runnerNotRunning should b FALSE $$$runnerNotRunning")
      assert(runnerNotRunning == false)

    }
  }

//  "div function" should "return valid result for generated random values" in {
//    div(4, 2) shouldBe 2
//
//    // Let's use Gen
//    forAll(arbitraryInts, nonZeroInts) { (n: Int, d: Int) =>
//      div(n, d) shouldBe n / d
//    }
//
//    // Let's use Gen with a condition while consuming
//    forAll(arbitraryInts, arbitraryInts) { (n: Int, d: Int) =>
//      whenever(d != 0) {
//        div(n, d) shouldBe n / d
//      }
//    }
//
//    // Using choose function
//    forAll(Gen.choose(1, 1000), Gen.choose(10, 100)) { (n: Int, d: Int) =>
//      div(n, d) shouldBe n / d
//    }
//  }
//  it should "return 1 given same values as numerator and denominator" in {
//    var iterations = 0
//    Dummy.div(10, 10) shouldBe 1
//
//    // Let's use Gen
//    forAll(nonZeroInts, minSuccessful(50)) { n: Int =>
//      iterations += 1
//      div(n, n) shouldBe 1
//    }
//
//    println(s"Test ran for $iterations iterations")
//  }
//  it should "throw IllegalArgumentException with denominator 0" in {
//    an[IllegalArgumentException] should be thrownBy div(10, 0)
//
//    // Let's use Gen
//    forAll(arbitraryInts) { n: Int =>
//      an[IllegalArgumentException] should be thrownBy div(n, 0)
//    }
//  }
//
//  "append function" should "return only one element when added in empty list" in {
//    val emptyList = List.empty[Int]
//
//    append(emptyList, 1) should have size 1
//
//    // Let's use Gen
//    forAll(arbitraryInts) { n: Int =>
//      append(emptyList, n) should have size 1
//    }
//  }
//  it should "append the element at the end of the list" in {
//    var iterations     = 0
//    val appendingValue = Int.MaxValue
//
//    append(List(1, 2, 3, 4, 4, 6, 3, 2), appendingValue).last shouldBe appendingValue
//
//    // Let's use Gen
//    forAll(arbitraryIntList, minSuccessful(100)) { l: List[Int] =>
//      iterations += 1
//      val appended = append(l, appendingValue)
//      appended.last shouldBe appendingValue
//    }
//    println(s"Test ran for $iterations iterations")
//  }
}

