/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.digitalservicestaxstub.controllers

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.digitalservicestaxstub.config.AppConfig
import uk.gov.hmrc.digitalservicestaxstub.services.DesService

import scala.concurrent.Future

class DESControllerSpec extends AnyFreeSpec with GuiceOneServerPerSuite with Matchers {
  val desService: DesService                       = mock[DesService]
  val appConfig: AppConfig                         = app.injector.instanceOf[AppConfig]
  val authAndEnvAction: AuthAndEnvAction           = app.injector.instanceOf[AuthAndEnvAction]
  lazy val cc: ControllerComponents                = stubControllerComponents()
  val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withHeaders(HeaderNames.AUTHORIZATION -> "token", "Environment" -> "live")
  def controller                                   = new DESController(appConfig = appConfig, cc = cc, authAndEnvAction, desService)

  "TaxEnrolmentCallbackController" - {
    "getSubscriptionByGroupId" - {
      "must return BAD_REQUEST for the input DST registration number DUDST2932891441" in {
        val result: Future[Result] = controller.dstReturn("DUDST2932891441")(request)
        status(result) mustBe BAD_REQUEST
      }

      s"must return OK" in {
        val result: Future[Result] = controller.dstReturn("ABCST2932891441")(request)
        status(result) mustBe OK
      }
    }
  }

}
