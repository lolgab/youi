package io.youi.server.handler

import io.youi.http.{CacheControl, CacheControlEntry, Content, Headers, HttpConnection, HttpStatus}

class SenderHandler private(content: Content, length: Option[Long], caching: CachingManager) extends HttpHandler {
  override def handle(connection: HttpConnection): Unit = {
    SenderHandler.handle(connection, content, length, caching)
  }
}

object SenderHandler {
  def apply(content: Content, length: Option[Long] = None, caching: CachingManager = CachingManager.Default): SenderHandler = {
    new SenderHandler(content, length, caching)
  }

  def handle(connection: HttpConnection,
             content: Content,
             length: Option[Long] = None,
             caching: CachingManager = CachingManager.Default,
             replace: Boolean = false): Unit = {
    if (connection.response.content.nonEmpty && !replace) {
      throw new RuntimeException(s"Content already set (${connection.response.content.get}) for HttpResponse in ${connection.request.url} when attempting to set $content.")
    }
    val contentLength = length.getOrElse(content.length)
    connection.update(_.withContent(content).withHeader(Headers.`Content-Length`(contentLength)))
    caching.handle(connection)
  }
}

sealed trait CachingManager extends HttpHandler

object CachingManager {
  case object Default extends CachingManager {
    override def handle(connection: HttpConnection): Unit = {}
  }
  case object NotCached extends CachingManager {
    override def handle(connection: HttpConnection): Unit = {
      connection.update(_.withHeader(Headers.`Cache-Control`(CacheControl.NoCache, CacheControl.NoStore)))
    }
  }
  case class LastModified(publicCache: Boolean = true) extends CachingManager {
    val visibility: CacheControlEntry = if (publicCache) CacheControl.Public else CacheControl.Private

    override def handle(connection: HttpConnection): Unit = connection.update { response =>
      response.content match {
        case Some(content) => {
          val ifModifiedSince = Headers.Request.`If-Modified-Since`.value(connection.request.headers).getOrElse(0L)
          if (ifModifiedSince == content.lastModified) {
            response.copy(status = HttpStatus.NotModified, headers = Headers.empty, content = None)
          } else {
            response
              .withHeader(Headers.`Cache-Control`(visibility))
              .withHeader(Headers.Response.`Last-Modified`(content.lastModified))
          }
        }
        case None => response
      }
    }
  }
  case class MaxAge(seconds: Long) extends CachingManager {
    override def handle(connection: HttpConnection): Unit = {
      connection.update(_.withHeader(Headers.`Cache-Control`(CacheControl.MaxAge(seconds))))
    }
  }
}