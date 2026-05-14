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

  private val dstFormBundleNumbers: Map[(String, String), String] = Map(
    ("utr", "1111111000")       -> "504820876213",
    ("utr", "2222222001")       -> "827391643960",
    ("safe", "XZ0006262719690") -> "915276940738",
    ("safe", "XH0007597348369") -> "949495540658"
  )

  def rosmLookupWithoutID: Action[JsValue] = AuthAndEnvAction.async(parse.json) { implicit request =>
    withJsonBody[RosmRegisterWithoutIDRequest] { rosmRequest =>
      if (rosmRequest.regime.matches("DST")) {
        val response =
          scala.io.Source
            .fromInputStream(getClass.getResourceAsStream("/dst/rosmLookupWithoutID.example.json"))
            .getLines()
            .mkString("\n")
            .replace("[now]", LocalDateTime.now.toString)
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
        val response =
          scala.io.Source
            .fromInputStream(getClass.getResourceAsStream("/dst/rosmLookupWithId.example.json"))
            .getLines()
            .mkString("\n")
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
    withJsonBody[EeittSubscribe] { _ =>
      if (appConfig.etmpNotReady) {
        Future.successful(Status(appConfig.etmpNotReadyStatus))
      } else if (regime != "DST") {
        Future.successful(
          BadRequest(
            Json.toJson(FailureMessage("INVALID_PAYLOAD", "Submission has not passed validation. Invalid Payload."))
          )
        )
      } else {
        dstFormBundleNumbers.get((idType, idNumber)) match {
          case Some(formBundleNumber) =>
            Future.successful(
              Ok(Json.obj("processingDate" -> LocalDateTime.now.toString, "formBundleNumber" -> formBundleNumber))
            )
          case None                   =>
            Future.successful(
              NotFound(
                Json.toJson(FailureMessage("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
              )
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
