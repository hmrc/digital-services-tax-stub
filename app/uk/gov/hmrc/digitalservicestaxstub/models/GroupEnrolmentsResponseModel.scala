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

package uk.gov.hmrc.digitalservicestaxstub.models

import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

case class Enrolment(
  service: String,
  friendlyName: String,
  state: String,
  identifiers: Seq[Identifier],
  enrolmentDate: Option[String] = None,
  activationDate: Option[String] = None,
  failedActivationCount: Option[Int] = None,
  enrolmentTokenExpiryDate: Option[String] = None
)

object Enrolment {
  implicit val formats = Json.format[Enrolment]
}

case class GroupEnrolmentsResponseModel(
  startRecord: Int,
  enrolments: Seq[Enrolment],
  totalRecords: Int
)

object GroupEnrolmentsResponseModel {
  implicit val formats = Json.format[GroupEnrolmentsResponseModel]
}
