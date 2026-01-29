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

package uk.gov.hmrc.digitalservicestaxstub.controllers

import cats.implicits._
import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.digitalservicestaxstub.config.AppConfig
import uk.gov.hmrc.digitalservicestaxstub.connectors.BackendConnector
import uk.gov.hmrc.digitalservicestaxstub.models.EnumUtils.idEnum
import uk.gov.hmrc.digitalservicestaxstub.models.{Identifier, TaxEnrolmentsSubscription}
import uk.gov.hmrc.digitalservicestaxstub.services.DesGenerator
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.smartstub._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaxEnrolmentCallbackController @Inject() (
  cc: ControllerComponents,
  backendConnector: BackendConnector
)(implicit
  executionContext: ExecutionContext
) extends BackendController(cc) {

  case class CallbackNotification(url: String, state: String, errorResponse: Option[String] = None)

  case object CallbackNotification {

    implicit def optFormatter[A](implicit innerFormatter: Format[A]): Format[Option[A]] =
      new Format[Option[A]] {
        def reads(json: JsValue): JsResult[Option[A]] = json match {
          case JsNull => JsSuccess(none[A])
          case a      => innerFormatter.reads(a).map(_.some)
        }
        def writes(o: Option[A]): JsValue             =
          o.map(innerFormatter.writes).getOrElse(JsNull)
      }

    implicit val format: Format[CallbackNotification] = Json.format[CallbackNotification]
  }

  implicit val w: HttpReads[Result] = new HttpReads[Result] {
    override def read(method: String, url: String, response: HttpResponse): Result = NoContent
  }

  private def send(notification: CallbackNotification)(implicit request: Request[AnyContent]): Future[Result] =
    backendConnector
      .bePost[CallbackNotification, Result](
        s"/digital-services-tax/tax-enrolment-callback/${notification.url}",
        notification
      )
      .map(_ => Ok("Tax enrolments callback triggered"))

  def trigger(seed: String): Action[AnyContent] = Action.async { implicit request =>
    DesGenerator.genDstRegisterResponse
      .seeded(seed)
      .map { x =>
        CallbackNotification(x.response.formBundleNumber, "SUCCEEDED")
      }
      .fold(throw new Exception("bad seed"))(send)
  }

  def getDstRegNo(seed: String): Action[AnyContent] = Action.async { request =>
    DesGenerator.genDstRegisterResponse
      .seeded(seed)
      .map { x =>
        x.dstRegNo
      }
      .fold(throw new Exception("bad seed")) { y =>
        Future(Ok(Json.toJson(DstRegNoWrapper(y))))
      }
  }

  def getSubscriptionByGroupId(groupId: String): Action[AnyContent] = Action.async { request =>
    val groupIdDstRefMap = Map("12345" -> "AMDST0799721562", "67890" -> "QIDST6330779458", "33333" -> "DUDST2932891441")
    groupId match {
      case grpId if groupIdDstRefMap.contains(grpId) =>
        Future.successful(
          Ok(
            Json.toJson(
              Seq(
                TaxEnrolmentsSubscription(
                  Some(Seq(Identifier("DSTRefNumber", groupIdDstRefMap.get(grpId).head))),
                  "SUCCEEDED",
                  None
                )
              )
            )
          )
        )
      case "11111"                                   =>
        Future.successful(
          Ok(
            Json.toJson(
              Seq(TaxEnrolmentsSubscription(Some(Seq(Identifier("DSTRefNumber", "AMDST0799721562"))), "PENDING", None))
            )
          )
        )
      case "22222"                                   =>
        Future.successful(
          Ok(
            Json.toJson(
              Seq(TaxEnrolmentsSubscription(None, "ERROR", Some("It is an error")))
            )
          )
        )
      case _                                         => Future.successful(BadRequest)
    }
  }

}
