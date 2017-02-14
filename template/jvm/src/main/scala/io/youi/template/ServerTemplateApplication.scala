package io.youi.template

import java.io.File

import io.youi.Priority
import io.youi.app.ServerApplication
import io.youi.http._
import io.youi.net.ContentType
import io.youi.server.UndertowServer
import io.youi.server.handler.{CachingManager, SenderHandler}
import io.youi.stream.{ByTag, Delta, HTMLParser, Selector}

class ServerTemplateApplication(compiler: TemplateCompiler) extends UndertowServer with TemplateApplication with ServerApplication {
  handler.caching(CachingManager.NotCached).file(compiler.destinationDirectory)
  handler.matcher(path.startsWith("/app")).caching(CachingManager.LastModified()).classLoader()

  if (compiler.removeDotHTML) {
    // Exclude pages .html
    handler.priority(Priority.Low).handle { httpConnection =>
      httpConnection.response.content match {
        case Some(content) => content match {
          case FileContent(file, contentType) => if (compiler.pages.contains(file.getName)) {
            httpConnection.update { response =>
              response.copy(content = None)
            }
          }
          case _ =>
        }
        case None =>
      }
    }

    // Server pages without .html
    handler.handle { httpConnection =>
      val url = httpConnection.request.url
      val page = s"${url.path.decoded.substring(1)}.html"
      if (compiler.pages.contains(page)) {
        val stream = HTMLParser.cache(new File(compiler.destinationDirectory, page))
        val deltas = List(
          Delta.InsertLastChild(ByTag("body"),
            s"""
              |<input type="hidden" id="template_pages" value="${compiler.pages.mkString(";")}"/>
              |<script src="/app/youi-template-fastopt.js"></script>
              |<script>
              | application();
              |</script>
            """.stripMargin)
        )
        val selector = url.param("selector").map(Selector.parse)
        val html = stream.stream(deltas, selector)
        SenderHandler.handle(httpConnection, Content.string(html, ContentType.`text/html`), caching = CachingManager.NotCached)
      }
    }
  }
}