package com.sa.sparkrun

import SparkRunTest._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import com.sa.sparkrun.db.domain.job.Job
import com.sa.sparkrun.handlers.SpSuccess
import org.scalacheck.Gen

class SparkRunTest extends FeatureBaseSpec {

  import fs2.Stream
  import cats.effect.{IO}

  def handleError(e: String, job: Job) ={
    info(s"%%%%%%%%% HANDLE ERROR: ${e}")
    false;
  }
  def handleSuccess(sp:SpSuccess, job: Job) ={
    info(s"%%%%%%%%%% HANDLE SUCCESS: ${sp}")
    true
  }

  import cats.effect.IO
  import fs2.Stream
  //  "Spark Run initialisation" should "fail with RuntimeException being thrown if Spark Run end points are down" in {
  feature("Yarn submission test") {
    scenario("Everything including yarn configuration settings, paths to resources is correct") {
      Given(s"""Spark YARN Server is running """)
      def sparkResponse(job: Job) = for {
        sparkRunner <- Stream.eval(IO(yarnSparkRunner))
        res = sparkRunner.submit(job)
      } yield (res)
      When(s"if submit request is made ")
      val isSubmitted = ((sparkResponse(correctJob).compile.toList.unsafeRunSync())(0))
        .fold( e => handleError(e, correctJob), (sp) => handleSuccess(sp, correctJob)).unsafeRunSync()
      Then(s"the spark run isSubmitted should b true: $$$isSubmitted")
      assert(isSubmitted == true)
    }
    scenario(("Some internal incorrect path (Wrong local resource path) causes failure to submit and return specific error.")){
      Given(s"""Spark YARN Server is running """)
      def sparkResponse(job: Job) = for {
        sparkRunner <- Stream.eval(IO(yarnSparkRunner))
        res = sparkRunner.submit(job)
      } yield (res)
      When(s"if submission request with wrong local resource path(s) is made ")
      val isSubmitted = ((sparkResponse(wrongPathJob).compile.toList.unsafeRunSync())(0))
        .fold( e => handleError(e, wrongPathJob), (sp) => handleSuccess(sp, wrongPathJob)).unsafeRunSync()
      Then(s"the spark run isSubmitted should b false: $$$isSubmitted")
      assert(isSubmitted == false)
    }
  }

//  "yarn submit function" should "submit with correct data " in {
//    def sparkResponse(job: Job) = for {
//      sparkRunner <- Stream.eval(IO(yarnSparkRunner))
//      _ <- Stream.eval(IO(println(s"sparkRunner: ${sparkRunner}")))
//      res = sparkRunner.submit(job)
//    } yield (res)
//    ((sparkResponse(correctJob).compile.toList.unsafeRunSync())(0))
//      .fold( e => handleError(e, correctJob), (sp) => handleSuccess(sp, correctJob)).unsafeRunSync() shouldBe true
//  }
//
//  "yarn submit function" should "fail to submit with wrong data (incorrect local resource path) " in {
//    def sparkResponse(job: Job) = for {
//      sparkRunner <- Stream.eval(IO(yarnSparkRunner))
//      _ <- Stream.eval(IO(println(s"sparkRunner: ${sparkRunner}")))
//      res = sparkRunner.submit(job)
//    } yield (res)
//    ((sparkResponse(wrongPathJob).compile.toList.unsafeRunSync())(0))
//      .fold( e => handleError(e, wrongPathJob), (sp) => handleSuccess(sp, wrongPathJob)).unsafeRunSync() shouldBe false
//  }
//
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
object SparkRunTest {
  val arbitraryInts: Gen[Int]          = arbitrary[Int]
  val nonZeroInts: Gen[Int]            = arbitrary[Int] suchThat (_ != 0)
  val arbitraryIntList: Gen[List[Int]] = arbitrary[List[Int]]
}
