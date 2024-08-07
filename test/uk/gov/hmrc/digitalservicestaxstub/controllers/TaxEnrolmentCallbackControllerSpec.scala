/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.http.Status.BAD_REQUEST
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}
import uk.gov.hmrc.digitalservicestaxstub.config.AppConfig
import uk.gov.hmrc.digitalservicestaxstub.connectors.BackendConnector
import uk.gov.hmrc.digitalservicestaxstub.models.TaxEnrolmentsSubscription

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaxEnrolmentCallbackControllerSpec extends AnyFreeSpec with GuiceOneServerPerSuite with Matchers {

  val connector: BackendConnector                  = mock[BackendConnector]
  val appConfig: AppConfig                         = app.injector.instanceOf[AppConfig]
  lazy val cc: ControllerComponents                = stubControllerComponents()
  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
  def controller                                   = new TaxEnrolmentCallbackController(appConfig = appConfig, cc = cc, backendConnector = connector)

  "TaxEnrolmentCallbackController" - {
    "getSubscriptionByGroupId" - {
      Seq("12345", "67890", "33333") foreach { groupId =>
        s"must return OK and state as 'SUCCEEDED' for the input groupId is $groupId" in {
          val result: Future[Result]    = controller.getSubscriptionByGroupId(groupId)(request)
          status(result) mustBe OK
          val taxEnrolmentsSubscription = contentAsJson(result).as[Seq[TaxEnrolmentsSubscription]].head
          taxEnrolmentsSubscription.state mustBe "SUCCEEDED"
          taxEnrolmentsSubscription.identifiers.isDefined mustBe true
        }
      }

      "must return OK and state as 'PENDING' for the input groupId" in {
        val result: Future[Result]    = controller.getSubscriptionByGroupId("11111")(request)
        status(result) mustBe OK
        val taxEnrolmentsSubscription = contentAsJson(result).as[Seq[TaxEnrolmentsSubscription]].head
        taxEnrolmentsSubscription.state mustBe "PENDING"
        taxEnrolmentsSubscription.identifiers.isDefined mustBe true
      }

      "must return OK and state as 'ERROR' for the input groupId" in {
        val result: Future[Result]    = controller.getSubscriptionByGroupId("22222")(request)
        status(result) mustBe OK
        val taxEnrolmentsSubscription = contentAsJson(result).as[Seq[TaxEnrolmentsSubscription]].head
        taxEnrolmentsSubscription.state mustBe "ERROR"
        taxEnrolmentsSubscription.identifiers.isDefined mustBe false
      }

      "must return BadRequest for the input groupId 888888" in {
        val result = controller.getSubscriptionByGroupId("888888")(request)
        status(result) mustBe BAD_REQUEST
      }
    }
  }

}
