package org.hyperscala.jquery

import org.powerscala.Version
import org.hyperscala.module.{Module, InterfaceWithDefault}
import org.hyperscala.web.{Webpage, Website}
import org.hyperscala.html.tag
import org.hyperscala.javascript.JavaScriptString
import org.hyperscala.realtime.Realtime
import com.outr.net.http.session.Session

/**
 * @author Matt Hicks <matt@outr.com>
 */
object Gritter extends Module with JavaScriptCaller {
  def name = "gritter"

  def version = Version(1, 7, 4)

  override def dependencies = List(InterfaceWithDefault(jQuery, jQuery.Latest), Realtime)

  override def init[S <: Session](website: Website[S]) = {
    website.addClassPath("/gritter/", "gritter/")
  }

  override def load[S <: Session](webpage: Webpage[S]) = {
    webpage.head.contents += new tag.Link(href = "/gritter/css/jquery.gritter.css", rel = "stylesheet")
    webpage.head.contents += new tag.Script(mimeType = "text/javascript", src = "/gritter/js/jquery.gritter.min.js")
  }

  def add[S <: Session](webpage: Webpage[S],
                        title: String,
                        text: String,
                        image: String = null,
                        sticky: Boolean = false,
                        time: Int = 8000,
                        className: String = null) = {
    webpage.require(this)
    webpage.body.contents += new tag.Script(content = new JavaScriptString(
      """
        |$.gritter.add({
        |   'title': %s,
        |   'text': %s,
        |   'image': %s,
        |   'sticky': %s,
        |   'time': %s,
        |   'class_name': %s
        |});
      """.stripMargin.format(value2String(title),       // Title
                             value2String(text),        // Text
                             value2String(image),       // Image
                             sticky,                    // Sticky
                             time,                      // Time
                             value2String(className)    // Class Name
          )
    ))
  }

}