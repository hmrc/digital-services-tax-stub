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

package uk.gov.hmrc.digitalservicestaxstub.models

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue}

import java.time.LocalDate

case class RosmRegisterRequest(
  regime: String,
  requiresNameMatch: Boolean,
  isAnAgent: Boolean,
  individual: Option[Individual] = None,
  organisation: Option[OrganisationRequest] = None
)

case class Individual(
  firstName: String,
  middleName: Option[String],
  lastName: String,
  dateOfBirth: Option[LocalDate]
)

case class OrganisationRequest(
  organisationName: String,
  organisationType: RosmOrganisationType
)

enum RosmOrganisationType(val value: String) {
  case Partnership extends RosmOrganisationType("Partnership")
  case LLP extends RosmOrganisationType("LLP")
  case CorporateBody extends RosmOrganisationType("Corporate body")
  case UnincorporatedBody extends RosmOrganisationType("Unincorporated body")
  case Unknown extends RosmOrganisationType("Not Specified")

  override def toString: String = value
}

object RosmOrganisationType {
  def parse(value: String): Option[RosmOrganisationType] =
    RosmOrganisationType.values.find(_.value == value)

  implicit val format: Format[RosmOrganisationType] = new Format[RosmOrganisationType] {
    def reads(json: JsValue): JsResult[RosmOrganisationType] = json match {
      case JsString(s) =>
        parse(s) match {
          case Some(obj) => JsSuccess(obj)
          case None      => JsError(s"Unknown RosmOrganisationType: $s")
        }
      case _           => JsError("Expected JsString for RosmOrganisationType")
    }

    def writes(obj: RosmOrganisationType): JsValue = JsString(obj.value)
  }
}
