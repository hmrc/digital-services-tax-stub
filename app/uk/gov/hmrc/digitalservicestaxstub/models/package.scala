/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.libs.json.{Format, Json, OFormat}

package object models {

  //ROSM register formatters
  implicit val organisationTypeFormat: Format[RosmOrganisationType.Value] = EnumUtils.enumFormat(RosmOrganisationType)
  implicit val individualFormatter: OFormat[Individual] = Json.format[Individual]
  implicit val organisationReqFormatter: OFormat[OrganisationRequest] = Json.format[OrganisationRequest]
  implicit val rosmRequestFormatter: OFormat[RosmRegisterRequest] = Json.format[RosmRegisterRequest]

  //ROSM register response formatters
  implicit val rosmResponseAddress: OFormat[RosmResponseAddress] = Json.format[RosmResponseAddress]
  implicit val rosmResponseOrg: OFormat[OrganisationResponse] = Json.format[OrganisationResponse]
  implicit val rosmResponseContactDetails: OFormat[RosmResponseContactDetails] = Json.format[RosmResponseContactDetails]
  implicit val rosmRegisterResponse: OFormat[RosmRegisterResponse] = Json.format[RosmRegisterResponse]

  //ROSM register response without ID formatters
  implicit val addressFormatter: OFormat[Address] = Json.format[Address]
  implicit val contactDetailsFormatter: OFormat[RosmRequestWithoutIDContactDetails] = Json.format[RosmRequestWithoutIDContactDetails]
  implicit val organisationWithoutIdFormatter: OFormat[RosmRegisterWithoutIDOrganisation] = Json.format[RosmRegisterWithoutIDOrganisation]
  implicit val rosmRegisterWithoutIDRequest: OFormat[RosmRegisterWithoutIDRequest] = Json.format[RosmRegisterWithoutIDRequest]
  implicit val rosmRegisterResponseWithoutID: OFormat[RosmRegisterWitoutIDResponse] = Json.format[RosmRegisterWitoutIDResponse]

  implicit val failureFormat: OFormat[FailureMessage] = Json.format[FailureMessage]

}
