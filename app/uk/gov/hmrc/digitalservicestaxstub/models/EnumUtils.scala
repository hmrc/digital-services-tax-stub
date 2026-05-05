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

import play.api.libs.json._
import uk.gov.hmrc.smartstub._
import scala.language.implicitConversions

/** Utility class for creating json formatters for enumerations.
  */
object EnumUtils {
  def enumReads[E <: Enumeration](en: E): Reads[en.Value] = new Reads[en.Value] {
    def reads(json: JsValue): JsResult[en.Value] = json match {
      case JsString(s) =>
        try
          JsSuccess(en.withName(s))
        catch {
          case _: NoSuchElementException =>
            JsError(
              s"Enumeration expected of type: '${en.getClass}'," ++
                s" but it does not appear to contain the value: '$s'"
            )
        }
      case _           => JsError("String value expected")
    }
  }

  implicit def enumWrites[E <: Enumeration](en: E): Writes[en.Value] = new Writes[en.Value] {
    def writes(v: en.Value): JsValue = JsString(v.toString)
  }

  implicit def enumFormat[E <: Enumeration](en: E): Format[en.Value] =
    Format(enumReads(en), enumWrites(en))

  implicit val idEnum: Enumerable[String] = pattern"9999999999"

}
