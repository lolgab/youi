package io.youi.server

import io.youi.http.{CacheControl, Content, HttpConnection, HttpStatus}
import io.youi.net.ContentType
import io.youi.server.dsl._
import perfolation._

object DefaultErrorHandler extends ErrorHandler {
  lazy val lastModified: Long = System.currentTimeMillis()

  def html(status: HttpStatus): Content = p"""<html>
    <head>
      <title>Error ${status.code}</title>
    </head>
    <body>
      ${status.code} - ${status.message}
    </body>
  </html>""".withContentType(ContentType.`text/html`).withLastModified(lastModified)

  override def handle(connection: HttpConnection, t: Option[Throwable]): Unit = connection.update { response =>
    val status = if (response.status.isError) {
      response.status
    } else {
      HttpStatus.InternalServerError
    }
    response
      .withContent(html(status))
      .withHeader(CacheControl(CacheControl.NoCache))
  }
}
