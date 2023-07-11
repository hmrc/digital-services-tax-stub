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

import java.time.LocalDateTime
import cats.implicits._
import org.scalacheck.Gen
import org.scalacheck.cats.implicits.genInstances
import uk.gov.hmrc.digitalservicestaxstub.models.EnumUtils.idEnum
import uk.gov.hmrc.smartstub._
import uk.gov.hmrc.digitalservicestaxstub.models._

object DesGenerator {

  private def variableLengthString(min: Int, max: Int) =
    Gen.choose(min, max).flatMap(len => Gen.listOfN(len, Gen.alphaLowerChar)).map(_.mkString)

  private def companyName: Gen[String] = Gen.company.retryUntil(a => a.length < 105 && a.length > 1)

  private def addressLine = variableLengthString(0, 35)

  private def genRosmResponseAddress: Gen[RosmResponseAddress] = (
    Gen.oneOf("The house", "50"), // addressLine1
    Gen.oneOf("The Street", "The Road", "The Lane").almostAlways, // addressLine2
    addressLine.almostAlways, // addressLine3
    addressLine.rarely, // addressLine4
    Gen.const("GB"), // countryCode
    Gen.postcode // postalCode
  ).mapN(RosmResponseAddress.apply)

  private def genEmail =
    for {
      username <- variableLengthString(5, 8)
      at       <- Gen.const("@")
      domain   <- variableLengthString(7, 9)
      ending   <- Gen.const(".com")
    } yield s"$username$at$domain$ending"

  private def genSafeId =
    for {
      a <- Gen.const("X")
      b <- Gen.alphaUpperChar
      c <- Gen.const("000")
      d <- Gen.listOfN(10, Gen.numChar).map(_.mkString)
    } yield s"$a$b$c$d"

  private def genFormBundleNumber = pattern"999999999999"

  private def genRosmResponseContactDetails: Gen[RosmResponseContactDetails] = (
    Gen.ukPhoneNumber.almostAlways, // primaryPhoneNumber
    Gen.ukPhoneNumber.sometimes, // secondaryPhoneNumber
    Gen.ukPhoneNumber.rarely, // faxNumber
    genEmail.almostAlways // emailAddress
  ).mapN(RosmResponseContactDetails.apply)

  private def shouldGenOrg(utr: String): OrganisationResponse = {
    import RosmOrganisationType._
    OrganisationResponse(
      companyName.seeded(utr).get,
      Gen.boolean.seeded(utr).get,
      Gen.oneOf(CorporateBody, LLP, UnincorporatedBody, Unknown).seeded(utr).get
    )
  }

  private def genIndividual(utr: String): Gen[Individual] =
    Individual(
      Gen.forename().seeded(utr).get,
      Gen.forename().rarely.seeded(utr).get,
      Gen.surname.seeded(utr).get,
      Gen.date.seeded(utr)
    )

  private def shouldGenAgentRef(isAnAgent: Boolean, seed: Long): Option[String] =
    if (isAnAgent) Gen.alphaNumStr.seeded(seed) else None

  def genRosmRegisterWithoutIDResponse(data: RosmRegisterWithoutIDRequest) = for {
    safeId              <- genSafeId
    sapNumber           <- pattern"9999999999"
    agentReferenceNumber =
      shouldGenAgentRef(data.isAnAgent, data.organisation.fold("1")(_.organisationName).map(_.toInt).sum)
  } yield RosmRegisterWitoutIDResponse(
    java.time.LocalDateTime.now.toString,
    sapNumber,
    safeId,
    agentReferenceNumber
  )

  def genRosmRegisterResponse(rosmRequest: RosmRegisterRequest, utr: String): Gen[Option[RosmRegisterResponse]] = (for {
    safeId              <- genSafeId
    agentReferenceNumber = shouldGenAgentRef(rosmRequest.isAnAgent, utr.toLong)
    isEditable          <- Gen.boolean
    isAnAgent            = rosmRequest.isAnAgent
    isAnIndividual      <- Gen.const(rosmRequest.individual.isDefined)
    individual          <- genIndividual(utr).sometimes
    organisation        <- Gen.const(shouldGenOrg(utr)).sometimes
    address             <- genRosmResponseAddress
    contactDetails      <- genRosmResponseContactDetails
  } yield RosmRegisterResponse(
    safeId,
    agentReferenceNumber,
    isEditable,
    isAnAgent,
    isAnIndividual,
    individual,
    organisation,
    address,
    contactDetails
  )).usually

  // "^([A-Z]{2}DST[0-9]{10})$"
  def genDstRegNo: Gen[String] = for {
    a <- Gen.alphaUpperChar
    b <- Gen.alphaUpperChar
    c <- Gen.const("DST")
    d <- Gen.listOfN(10, Gen.numChar).map(_.mkString)
  } yield s"$a$b$c$d"

  def genDstRegisterResponse: Gen[RegWrapper] = for {
    formBundleNumber <- genFormBundleNumber
    dstRegNo         <- genDstRegNo
  } yield RegWrapper(
    DSTRegistrationResponse(
      LocalDateTime.now.toString,
      formBundleNumber
    ),
    dstRegNo
  )

  case class RegWrapper(response: DSTRegistrationResponse, dstRegNo: String)

}
