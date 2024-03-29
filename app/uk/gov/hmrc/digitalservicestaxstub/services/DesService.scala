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

package uk.gov.hmrc.digitalservicestaxstub.services

import javax.inject.Singleton
import uk.gov.hmrc.digitalservicestaxstub.models.EnumUtils.idEnum
import uk.gov.hmrc.digitalservicestaxstub.models.{DSTRegistration, DSTRegistrationResponse, RosmRegisterRequest, RosmRegisterResponse, RosmRegisterWithoutIDRequest, RosmRegisterWitoutIDResponse}
import uk.gov.hmrc.smartstub._
import cats.implicits._

@Singleton
class DesService {

  def handleDstRegistration(
    idType: String,
    idNumber: String,
    regData: DSTRegistration
  ): Option[DSTRegistrationResponse] =
    DesGenerator.genDstRegisterResponse
      .seeded(idNumber)
      .map(_.response)

  def handleRosmLookupWithoutIdRequest(data: RosmRegisterWithoutIDRequest): Option[RosmRegisterWitoutIDResponse] =
    DesGenerator
      .genRosmRegisterWithoutIDResponse(data)
      .seeded(
        data.organisation
          .fold("1")(_.organisationName)
          .map(_.toInt)
          .sum
          .toLong
      )
      .get
      .some

  def handleRosmLookupWithIdRequest(data: RosmRegisterRequest, utr: String): Option[RosmRegisterResponse] =
    DesGenerator.genRosmRegisterResponse(data, utr).seeded(utr).get

}
