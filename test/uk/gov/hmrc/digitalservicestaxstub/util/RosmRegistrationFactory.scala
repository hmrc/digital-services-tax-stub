/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.digitalservicestaxstub.util

import org.apache.pekko.http.scaladsl.model.headers.SameSite
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.digitalservicestaxstub.models.{Address, RosmRegisterRequest, RosmRegisterWithoutIDRequest, RosmRequestWithoutIDContactDetails}

class RosmRegistrationFactory {

  private val dstWithoutIdRequestObj: JsValue =
    Json.toJson(RosmRegisterWithoutIDRequest("DST", "", isAnAgent = false, isAGroup = false, None,
    Address("", None, None, None, None, "GB"), RosmRequestWithoutIDContactDetails(None, None, None, None)))

  private val nonDstWithoutIdRequestObj: JsValue =
    Json.toJson(RosmRegisterWithoutIDRequest("NON_DST", "", isAnAgent = false, isAGroup = false, None,
    Address("", None, None, None, None, "GB"), RosmRequestWithoutIDContactDetails(None, None, None, None)))

  private val dstRequestObj: JsValue =
    Json.toJson(RosmRegisterRequest("DST", requiresNameMatch = false, isAnAgent = false, None, None))

  private val nonDstRequestObj: JsValue =
    Json.toJson(RosmRegisterRequest("NON_DST", requiresNameMatch = false, isAnAgent = false, None))



  def getDSTRosmLookupWithoutIDRequest: JsValue =  dstWithoutIdRequestObj
  def getNonDSTRosmLookupWithoutIDRequest: JsValue = nonDstWithoutIdRequestObj
  def getDSTRosmLookupWithIDRequest: JsValue =  dstRequestObj
  def getNonDSTRosmLookupWithIDRequest: JsValue = nonDstRequestObj
}
