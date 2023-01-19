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

package uk.gov.hmrc.digitalservicestaxstub
package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import config.AppConfig
import models._
import services.{DesGenerator, DesService}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.Future

@Singleton()
class DESController @Inject() (
  appConfig: AppConfig,
  cc: ControllerComponents,
  AuthAndEnvAction: AuthAndEnvAction,
  desService: DesService
) extends BackendController(cc) {

  def rosmLookupWithoutID: Action[JsValue] = AuthAndEnvAction.async(parse.json) { implicit request =>
    withJsonBody[RosmRegisterWithoutIDRequest] { rosmRequest =>
      if (rosmRequest.regime.matches("DST"))
        desService.handleRosmLookupWithoutIdRequest(rosmRequest) match {
          case Some(data) => Future successful Ok(Json.toJson(data))
          case _          =>
            Future successful NotFound(
              Json.toJson(FailureMessage("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
            )
        }
      else
        Future successful BadRequest(
          Json.toJson(FailureMessage("INVALID_PAYLOAD", "Submission has not passed validation. Invalid Payload"))
        )
    }
  }

  def rosmLookupWithId(utr: String): Action[JsValue] = AuthAndEnvAction.async(parse.json) { implicit request =>
    withJsonBody[RosmRegisterRequest](rosmRequest =>
      if (rosmRequest.regime.matches("DST"))
        desService.handleRosmLookupWithIdRequest(rosmRequest, utr) match {
          case Some(data) => Future successful Ok(Json.toJson(data))
          case _          =>
            Future successful NotFound(
              Json.toJson(FailureMessage("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
            )
        }
      else
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
        if (regime.matches("DST"))
          desService.handleDstRegistration(idType, idNumber, regData) match {
            case Some(data) => Future successful Ok(Json.toJson(data))
            case _          =>
              Future successful NotFound(
                Json.toJson(FailureMessage("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
              )
          }
        else
          Future successful BadRequest(
            Json.toJson(FailureMessage("INVALID_PAYLOAD", "Submission has not passed validation. Invalid Payload."))
          )
      }
    }
  }

  def dstReturn(regNo: String): Action[AnyContent] = AuthAndEnvAction(parse.json) {
    val r: DSTRegistrationResponse = DesGenerator.genDstRegisterResponse.map(_.response).sample.get
    Ok(Json.toJson(r))
  }

  lazy val cannedPeriodResponse: String =
    scala.io.Source
      .fromInputStream(
        getClass.getResourceAsStream(
          "/dst/1330-get-obligation.response.example1.json"
        )
      )
      .getLines
      .mkString("\n")

  def getPeriods(dstRegNo: String): Action[AnyContent] = Action {
    Ok(Json.parse(cannedPeriodResponse))
  }

}
