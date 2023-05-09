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

import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.digitalservicestaxstub.models.{Enrolment, GroupEnrolmentsResponseModel, Identifier}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnrolmentStoreProxyController @Inject() (
  cc: ControllerComponents
)(implicit
  executionContext: ExecutionContext
) extends BackendController(cc) {

  def getGroupEnrolments(groupId: String, service: Option[String]): Action[AnyContent] = Action.async { request =>
    groupId match {
      case "12345" if service.isDefined && service.get == "HMRC-DST-ORG" =>
        Future.successful(
          Ok(
            Json.toJson(
              GroupEnrolmentsResponseModel(
                1,
                Seq(
                  Enrolment(
                    service.get,
                    "",
                    "Activated",
                    Seq(Identifier("DSTRefNumber", "AMDST0799721562")),
                    Some("2023-05-05 12:19:26.798"),
                    Some("2023-05-05 12:19:26.798"),
                    Some(0)
                  )
                ),
                1
              )
            )
          )
        )
      case _                                                             => Future.successful(NoContent)
    }
  }

}
