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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.digitalservicestaxstub.config.AppConfig
import uk.gov.hmrc.digitalservicestaxstub.models.{DSTRegistration, DSTRegistrationResponse, RosmRegisterRequest, RosmRegisterResponse, RosmRegisterWithoutIDRequest, RosmRegisterWitoutIDResponse, RosmResponseAddress, RosmResponseContactDetails}
import uk.gov.hmrc.digitalservicestaxstub.services.DesService
import uk.gov.hmrc.digitalservicestaxstub.util.{DSTRegistrationFactory, RosmRegistrationFactory}

import scala.concurrent.Future

class DESControllerSpec extends AnyFreeSpec with GuiceOneServerPerSuite with Matchers {
  val desService: DesService                       = mock[DesService]
  val appConfig: AppConfig                         = app.injector.instanceOf[AppConfig]
  val authAndEnvAction: AuthAndEnvAction           = app.injector.instanceOf[AuthAndEnvAction]
  lazy val cc: ControllerComponents                = stubControllerComponents()
  val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withHeaders(HeaderNames.AUTHORIZATION -> "token", "Environment" -> "live")

  // Create Serialized Jsons
  val dstRegistrationJsonPayload: JsValue = new DSTRegistrationFactory().getDstRegistrationData
  val rosmDSTWithoutIdJsonPayload: JsValue = new RosmRegistrationFactory().getDSTRosmLookupWithoutIDRequest
  val rosmNonDSTWithoutIdJsonPayload: JsValue = new RosmRegistrationFactory().getNonDSTRosmLookupWithoutIDRequest
  val rosmDSTWithIdJsonPayload: JsValue = new RosmRegistrationFactory().getDSTRosmLookupWithIDRequest
  val rosmNonDSTWithIdJsonPayload: JsValue = new RosmRegistrationFactory().getNonDSTRosmLookupWithIDRequest


  // Create Fake Requests
  val registrationRequest: FakeRequest[JsValue] =
    FakeRequest("", "")
      .withHeaders(HeaderNames.AUTHORIZATION -> "token", "Environment" -> "live").withBody(dstRegistrationJsonPayload)

  val DSTRosmWithoutIDRequest: FakeRequest[JsValue] =
    FakeRequest("", "")
      .withHeaders(HeaderNames.AUTHORIZATION -> "token", "Environment" -> "live").withBody(rosmDSTWithoutIdJsonPayload)

  val nonDSTRosmWithoutIDRequest: FakeRequest[JsValue] =
    FakeRequest("", "")
      .withHeaders(HeaderNames.AUTHORIZATION -> "token", "Environment" -> "live").withBody(rosmNonDSTWithoutIdJsonPayload)

  val DSTRosmWithIDRequest: FakeRequest[JsValue] =
    FakeRequest("", "")
      .withHeaders(HeaderNames.AUTHORIZATION -> "token", "Environment" -> "live").withBody(rosmDSTWithIdJsonPayload)

  val nonDSTRosmWithIDRequest: FakeRequest[JsValue] =
    FakeRequest("", "")
      .withHeaders(HeaderNames.AUTHORIZATION -> "token", "Environment" -> "live").withBody(rosmNonDSTWithIdJsonPayload)

  def controller = new DESController(appConfig = appConfig, cc = cc, authAndEnvAction, desService)

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


    "dstRegistration" - {
      s"must return OK for DST regime" in {
        when(desService.handleDstRegistration(any[String], any[String], any[DSTRegistration])).thenReturn(Some(DSTRegistrationResponse("", "")))
        val result: Future[Result] = controller.dstRegistration("DST", "ID_TYPE", "001")(registrationRequest)
        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
      }

      s"must return NOT_FOUND for DST regime when the desService does not return valid data" in {
        when(desService.handleDstRegistration(any[String], any[String], any[DSTRegistration])).thenReturn(None)
        val result: Future[Result] = controller.dstRegistration("DST", "ID_TYPE", "009")(registrationRequest)
        status(result) mustBe NOT_FOUND
        contentType(result) mustBe Some("application/json")
      }

      s"must return BAD_REQUEST for NON_DST regime" in {
        val result: Future[Result] = controller.dstRegistration("NON_DST", "ID_TYPE", "001")(registrationRequest)
        status(result) mustBe BAD_REQUEST
        contentType(result) mustBe Some("application/json")
      }
    }


    "rosmLookupWithoutID" - {
      s"must return OK for DST regime" in {
        when(desService.handleRosmLookupWithoutIdRequest(any[RosmRegisterWithoutIDRequest]))
          .thenReturn(Some(
            RosmRegisterWitoutIDResponse("", "", "", None)))
        val result: Future[Result] = controller.rosmLookupWithoutID()(DSTRosmWithoutIDRequest)
        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
      }

      s"must return NOT_FOUND for DST regime when the desService does not return valid data" in {
        when(desService.handleRosmLookupWithoutIdRequest(any[RosmRegisterWithoutIDRequest]))
          .thenReturn(None)
        val result: Future[Result] = controller.rosmLookupWithoutID()(DSTRosmWithoutIDRequest)
        status(result) mustBe NOT_FOUND
        contentType(result) mustBe Some("application/json")
      }

      s"must return BAD_REQUEST for NON_DST regime" in {
        val result: Future[Result] = controller.rosmLookupWithoutID()(nonDSTRosmWithoutIDRequest)
        status(result) mustBe BAD_REQUEST
        contentType(result) mustBe Some("application/json")
      }
    }


    "rosmLookupWithID" - {
      s"must return OK for DST regime" in {
        when(desService.handleRosmLookupWithIdRequest(any[RosmRegisterRequest], any[String]))
          .thenReturn(Some(
            RosmRegisterResponse("", None, isEditable = false, isAnAgent = false, isAnIndividual = false, None, None,
              RosmResponseAddress("", None, None, None, "", ""), RosmResponseContactDetails(None, None, None, None))))
        val result: Future[Result] = controller.rosmLookupWithId("12345678")(DSTRosmWithIDRequest)
        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
      }

      s"must return NOT_FOUND for DST regime when the desService does not return valid data" in {
        when(desService.handleRosmLookupWithIdRequest(any[RosmRegisterRequest], any[String]))
          .thenReturn(None)
        val result: Future[Result] = controller.rosmLookupWithId("12345678")(DSTRosmWithIDRequest)
        status(result) mustBe NOT_FOUND
        contentType(result) mustBe Some("application/json")
      }

      s"must return BAD_REQUEST for NON_DST regime" in {
        val result: Future[Result] = controller.rosmLookupWithId("12345678")(nonDSTRosmWithIDRequest)
        status(result) mustBe BAD_REQUEST
        contentType(result) mustBe Some("application/json")
      }
    }


    "getPeriod" - {
      s"must return OK" in {
        val result: Future[Result] = controller.getPeriods("001")(request)
        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
      }
    }
  }
}
