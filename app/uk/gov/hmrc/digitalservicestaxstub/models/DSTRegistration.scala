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

import play.api.libs.json.Json

case class LegalEntity(
  dateOfApplication: String, // YYYY-MM-DD
  taxStartDate: String
)

case class BusinessContactDetails(
  addressLine1: String,
  addressLine2: Option[String],
  addressLine3: Option[String],
  addressLine4: Option[String],
  postCode: Option[String],
  email: String,
  addressNotInUK: String,
  addressInputModeIndicator: String
)

case class CustomerIdentificationNumber(
  noIdentifier: String,
  custFirstName: String,
  custLastName: String
)

case class CommonDetails(
  legalEntity: LegalEntity,
  customerIdentificationNumber: CustomerIdentificationNumber,
  businessContactDetails: BusinessContactDetails
)

case class Params(
  paramSequence: String,
  paramName: String,
  paramValue: String
)

case class DSTRegistration(
  isrScenario: String,
  commonDetails: CommonDetails,
  regimeSpecificDetails: List[Params]
)

case object DSTRegistration {
  implicit val bcdformat = Json.format[BusinessContactDetails]
  implicit val leformat  = Json.format[LegalEntity]
  implicit val cinformat = Json.format[CustomerIdentificationNumber]
  implicit val cdformat  = Json.format[CommonDetails]
  implicit val pformat   = Json.format[Params]
  implicit val format    = Json.format[DSTRegistration]
}

case class EeittSubscribe(
  registrationDetails: DSTRegistration // ,
//  siteDetails: SiteDetails // if needed?
)

object EeittSubscribe {
  implicit val format = Json.format[EeittSubscribe]
}
