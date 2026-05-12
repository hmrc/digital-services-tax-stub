/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.digitalservicestaxstub
package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import config.AppConfig
import models.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDateTime
import scala.concurrent.Future

@Singleton()
class DESController @Inject() (
  appConfig: AppConfig,
  cc: ControllerComponents,
  AuthAndEnvAction: AuthAndEnvAction
) extends BackendController(cc) {

  def rosmLookupWithoutID: Action[JsValue] = AuthAndEnvAction.async(parse.json) { implicit request =>
    withJsonBody[RosmRegisterWithoutIDRequest] { rosmRequest =>
      if (rosmRequest.regime.matches("DST")) {
        val response = s"""
            |{
            |  "processingDate": "${LocalDateTime.now.toString}",
            |  "sapNumber": "4140347545",
            |  "safeId": "XZ0006262719690"
            |}
            |""".stripMargin
        Future.successful(Ok(Json.parse(response)))
      } else
        Future successful BadRequest(
          Json.toJson(FailureMessage("INVALID_PAYLOAD", "Submission has not passed validation. Invalid Payload"))
        )
    }
  }

  def rosmLookupWithId(utr: String): Action[JsValue] = AuthAndEnvAction.async(parse.json) { implicit request =>
    withJsonBody[RosmRegisterRequest](rosmRequest =>
      if (rosmRequest.regime.matches("DST")) {
        val response = """
            |{
            |  "safeId": "XJ0008010817305",
            |  "isEditable": true,
            |  "isAnAgent": false,
            |  "isAnIndividual": false,
            |  "individual": {
            |    "firstName": "Aaliyah",
            |    "middleName": "Chloe",
            |    "lastName": "Rodriguez",
            |    "dateOfBirth": "1974-02-28"
            |  },
            |  "organisation": {
            |    "organisationName": "Delhaizer",
            |    "isAGroup": false,
            |    "organisationType": "LLP"
            |  },
            |  "address": {
            |    "addressLine1": "The house",
            |    "addressLine2": "The Road",
            |    "addressLine3": "aymcuaflybgknvwtedhgrkqxutgpdbtrdjv",
            |    "addressLine4": "mvmkirbrnlijfwvidduzsrnrinn",
            |    "countryCode": "GB",
            |    "postalCode": "HG18 3RE"
            |  },
            |  "contactDetails": {
            |    "primaryPhoneNumber": "01589 919577",
            |    "faxNumber": "06294 190689",
            |    "emailAddress": "iiepddgh@aflvbspg.com"
            |  }
            |}
            |""".stripMargin
        Future.successful(Ok(Json.parse(response)))
      } else
        Future successful BadRequest(
          Json.toJson(FailureMessage("INVALID_PAYLOAD", "Submission has not passed validation. Invalid Payload."))
        )
    )
  }

  def dstRegistration(
    regime: String,
    idType: String,
    idNumber: String
  ): Action[JsValue] = AuthAndEnvAction.async(parse.json) { implicit request =>
    withJsonBody[EeittSubscribe] { case EeittSubscribe(regData) =>
      if (appConfig.etmpNotReady) {
        Future.successful(Status(appConfig.etmpNotReadyStatus))
      } else {
        (regime, idType, idNumber) match {
          case ("DST", "utr", "1111111000")       =>
            Future.successful(Ok(Json.parse(s"""
                 |{"processingDate":"${LocalDateTime.now.toString}","formBundleNumber":"504820876213"}
                 |""".stripMargin)))
          case ("DST", "utr", "2222222001")       =>
            Future.successful(Ok(Json.parse(s"""
                 |{"processingDate":"${LocalDateTime.now.toString}","formBundleNumber":"827391643960"}
                 |""".stripMargin)))
          case ("DST", "safe", "XZ0006262719690") =>
            Future.successful(Ok(Json.parse(s"""
                 |{"processingDate":"${LocalDateTime.now.toString}","formBundleNumber":"915276940738"}
                 |""".stripMargin)))
          case ("DST", "safe", "XH0007597348369") =>
            Future.successful(Ok(Json.parse(s"""
                 |{"processingDate":"${LocalDateTime.now.toString}","formBundleNumber":"949495540658"}
                 |""".stripMargin)))
          case ("DST", _, _)                      =>
            Future successful NotFound(
              Json.toJson(FailureMessage("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
            )
          case _                                  =>
            Future successful BadRequest(
              Json.toJson(FailureMessage("INVALID_PAYLOAD", "Submission has not passed validation. Invalid Payload."))
            )
        }
      }
    }
  }

  def dstReturn(regNo: String): Action[AnyContent] = AuthAndEnvAction(parse.json) {
    if (regNo.equalsIgnoreCase("DUDST2932891441")) {
      BadRequest
    } else {
      Ok(Json.parse(s"""
           |{"processingDate":"${LocalDateTime.now.toString}","formBundleNumber":"572037502413"}
           |""".stripMargin))
    }
  }

  lazy val cannedPeriodResponse: String =
    scala.io.Source
      .fromInputStream(
        getClass.getResourceAsStream(
          "/dst/1330-get-obligation.response.example1.json"
        )
      )
      .getLines()
      .mkString("\n")

  def getPeriods(dstRegNo: String): Action[AnyContent] = Action {
    Ok(Json.parse(cannedPeriodResponse))
  }

}
