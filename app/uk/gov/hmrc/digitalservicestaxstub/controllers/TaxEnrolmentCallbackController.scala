/*
 * Copyright 2020 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc._
import play.api.Logger
import uk.gov.hmrc.digitalservicestaxstub.config.AppConfig
import uk.gov.hmrc.digitalservicestaxstub.connectors.BackendConnector
import uk.gov.hmrc.digitalservicestaxstub.models.EnumUtils.idEnum
import uk.gov.hmrc.digitalservicestaxstub.services.DesGenerator
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.smartstub._
import cats.implicits._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime
object TaxEnrolmentCallbackController {

  private[controllers] case class CallbackWrapper(
    internalId: String, 
    formBundle: String,
    timestamp: LocalDateTime = LocalDateTime.now
  )
  implicit val formatWrapper = Json.format[CallbackWrapper]
}

@Singleton
class TaxEnrolmentCallbackController @Inject()(
  appConfig: AppConfig,
  cc: ControllerComponents,
  backendConnector: BackendConnector,
  mongo: play.modules.reactivemongo.ReactiveMongoApi
)(
  implicit executionContext: ExecutionContext
) extends BackendController(cc) {

  case class CallbackNotification(url: String, state: String, errorResponse: Option[String] = None)

  case object CallbackNotification {

    implicit def optFormatter[A](implicit innerFormatter: Format[A]): Format[Option[A]] =
      new Format[Option[A]] {
        def reads(json: JsValue): JsResult[Option[A]] = json match {
          case JsNull => JsSuccess(none[A])
          case a      => innerFormatter.reads(a).map{_.some}
        }
        def writes(o: Option[A]): JsValue =
          o.map{innerFormatter.writes}.getOrElse(JsNull)
      }

    implicit val format: Format[CallbackNotification] = Json.format[CallbackNotification]
  }


  implicit val w = new HttpReads[Result] {
    override def read(method: String, url: String, response: HttpResponse): Result = NoContent
  }

  private def send(notification: CallbackNotification)(implicit request: Request[AnyContent]): Future[Result] =
    backendConnector
      .bePost[CallbackNotification, Result](s"/digital-services-tax/tax-enrolment-callback/${notification.url}", notification).map { _ =>
      Ok("Tax enrolments callback triggered")
    }

  def trigger(seed: String): Action[AnyContent] = Action.async { implicit request =>
    DesGenerator.genDstRegisterResponse.seeded(seed).map { x =>
      CallbackNotification(x.response.formBundleNumber, "SUCCEEDED")
    }.fold(throw new Exception("bad seed"))(send)
  }

  def triggerAllRegCallbacks(): Action[AnyContent] = Action.async { implicit request => 

    import reactivemongo.api.Cursor
    import reactivemongo.api.indexes.{Index, IndexType}
    import reactivemongo.play.json._, collection._
    import play.modules.reactivemongo._
    import mongo.database
    import TaxEnrolmentCallbackController._
    lazy val c: Future[JSONCollection] = {
      database.map(_.collection[JSONCollection]("pending-callbacks")).flatMap { c =>

        val sessionIndex = Index(
          key = Seq("formBundle" -> IndexType.Ascending),
          unique = true
        )
        
        c.indexesManager.ensure(sessionIndex).map { case _ => c }
      }
    }

    val selector = Json.obj()

    c.flatMap(
      _.find(selector)
        .cursor[CallbackWrapper]()
        .collect[List](        
          maxDocs = 30,
          err = Cursor.FailOnError[List[CallbackWrapper]]()
        ).flatMap { records =>
          Future.sequence(records.map{
            case CallbackWrapper(intId, fbNo, time) =>
              Logger.info(s"Triggering callback for Form bundle: $fbNo")
              val notification = CallbackNotification(fbNo, "SUCCEEDED")
              val url = s"/digital-services-tax/tax-enrolment-callback/${notification.url}"
              backendConnector.bePost[CallbackNotification, Result](url, notification)
          })
        }
    ) >> Future.successful( Ok("Tax enrolments callbacks triggered") )
  }

  def getDstRegNo(seed: String) : Action[AnyContent] = Action.async { implicit request =>
    DesGenerator.genDstRegisterResponse.seeded(seed).map { x =>
      x.dstRegNo
    }.fold(throw new Exception("bad seed")) { y =>
      Future(Ok(Json.toJson(DstRegNoWrapper(y))))
    }
  }

}
