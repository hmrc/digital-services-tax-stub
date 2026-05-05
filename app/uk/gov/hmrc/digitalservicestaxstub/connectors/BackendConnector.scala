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

import play.api.{Configuration, Environment}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpReads, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import play.api.libs.ws.writeableOf_JsValue

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BackendConnector @Inject() (
  httpClientV2: HttpClientV2,
  environment: Environment,
  configuration: Configuration,
  servicesConfig: ServicesConfig
) {

  private val serviceURL: String = servicesConfig.baseUrl("digital-services-tax")

  def bePost[I, O](url: String, body: I)(implicit
    wts: Writes[I],
    rds: HttpReads[O],
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[O] =
    httpClientV2
      .post(url"$serviceURL$url")(using addHeaders)
      .withBody(Json.toJson(body))
      .execute[O]

  private def addHeaders(implicit hc: HeaderCarrier): HeaderCarrier =
    hc.copy(authorization =
      Some(Authorization(s"Bearer ${servicesConfig.getConfString("digital-services-tax.token", "")}"))
    )
}
