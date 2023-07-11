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
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status.NO_CONTENT
import play.api.libs.json.Json
import play.api.mvc.{ControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnrolmentStoreProxyControllerSpec extends AnyFreeSpec with GuiceOneServerPerSuite with Matchers {

  lazy val cc: ControllerComponents = stubControllerComponents()
  val request                       = FakeRequest("", "")
  def controller                    = new EnrolmentStoreProxyController(cc)

  "EnrolmentStoreProxyController" - {
    "must return OK and expected Json for the input groupId 12345" in {
      val result: Future[Result] = controller.getGroupEnrolments("12345", Some("HMRC-DST-ORG"))(request)
      status(result) mustBe OK
      val groupEnrolmentResponse = """{
                                     |    "startRecord":1,
                                     |    "totalRecords":1,
                                     |    "enrolments":[
                                     |                {
                                     |                    "service":"HMRC-DST-ORG",
                                     |                    "state":"Activated",
                                     |                    "friendlyName":"",
                                     |                     "enrolmentDate":"2023-05-05 12:19:26.798",
                                     |                     "failedActivationCount":0,
                                     |                     "activationDate":"2023-05-05 12:19:26.798",
                                     |
                                     |                    "identifiers": [
                                     |
                                     |                            {
                                     |                                "key":"DSTRefNumber",
                                     |                                "value":"AMDST0799721562"
                                     |                            }
                                     |
                                     |                    ]
                                     |                }
                                     |
                                     |    ]
                                     |}""".stripMargin
      contentAsJson(result) mustBe Json.parse(groupEnrolmentResponse)
    }

    "must return NoContent for the input groupId 888888" in {
      val result = controller.getGroupEnrolments("888888", Some("HMRC-DST-ORG"))(request)
      status(result) mustBe NO_CONTENT
    }

    "must return BadRequest for the input non DST service" in {
      val result = controller.getGroupEnrolments("12345", Some("IR-SA"))(request)
      status(result) mustBe NO_CONTENT
    }
  }

}
