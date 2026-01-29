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

package uk.gov.hmrc.digitalservicestaxstub.connectors

import play.api.libs.json.{Json, Writes}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URI
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BackendConnector @Inject() (http: HttpClientV2, servicesConfig: ServicesConfig) {

  private val serviceURL: String = servicesConfig.baseUrl("digital-services-tax")

  def bePost[I, O](url: String, body: I)(implicit
    wts: Writes[I],
    rds: HttpReads[O],
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[O] = {

    implicit val updatedHeaders: HeaderCarrier = hc.copy(authorization =
      Some(Authorization(s"Bearer ${servicesConfig.getConfString("digital-services-tax.token", "")}"))
    )

    http
      .post(new URI(s"$serviceURL$url").toURL)(using updatedHeaders)
      .withBody(Json.toJson(body))
      .execute
  }
}
