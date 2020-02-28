package uk.gov.hmrc.digitalservicestaxstub.controllers

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class DesControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {

  "GET /" should {
    "return 200" in {
      true shouldBe true
    }
  }

}

