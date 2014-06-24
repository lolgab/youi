package org.hyperscala.ui

import org.hyperscala.realtime.Realtime
import org.hyperscala.html._
import org.powerscala.event.Listenable
import org.powerscala.{StorageComponent, Version}
import org.hyperscala.module._
import org.hyperscala.jquery.{jQueryComponent, JavaScriptCaller, jQuery}
import com.outr.net.http.session.Session
import org.hyperscala.web._
import scala.language.implicitConversions

/**
 * Wrapper module around this jQuery project:
 *
 * https://github.com/miromannino/Justified-Gallery
 *
 * @author Matt Hicks <mhicks@outr.com>
 */
object JustifiedGallery extends Module with JavaScriptCaller with StorageComponent[JustifiedGallery, tag.Div] {
  def name = "justified-gallery"
  def version = Version(3, 2, 0)

  implicit def tag2Gallery(t: tag.Div) = apply(t)

  override def apply(t: tag.Div) = {
    t.require(this)
    super.apply(t)
  }

  protected def create(t: tag.Div) = new JustifiedGallery(t)

  override def dependencies = List(InterfaceWithDefault(jQuery, jQuery.Latest), Realtime)

  override def init[S <: Session](website: Website[S]) = {
    website.addClassPath("/justified-gallery", "justified-gallery")
  }

  override def load[S <: Session](webpage: Webpage[S]) = {
    webpage.head.contents += new tag.Link(href = "/justified-gallery/justifiedGallery.min.css")
    webpage.head.contents += new tag.Script(mimeType = "text/javascript", src = "/justified-gallery/jquery.justifiedGallery.min.js")
  }
}

class JustifiedGallery private(val wrapped: tag.Div, val autoInit: Boolean = true) extends jQueryComponent {
  def functionName = "justifiedGallery"

  implicit def listenable: Listenable = wrapped

  val rowHeight = property("rowHeight", 120)
  val maxRowHeight = property("maxRowHeight", 0)
  val lastRow = property("lastRow", "nojustify")
  val fixedHeight = property("fixedHeight", false)
  val captions = property("captions", true)
  val margins = property("margins", 1)
  val randomize = property("randomize", false)
  val refreshTime = property("refreshTime", 250)
  val rel = property("rel", "")
  val target = property("target", "")
  val justifyThreshold = property("justifyThreshold", 0.35)
  val cssAnimation = property("cssAnimation", false)
  val captionsAnimationDuration = property("captionsAnimationDuration", 500)
  val imagesAnimationDuration = property("imagesAnimationDuration", 300)
  val captionsVisibleOpacity = property("captionsVisibleOpacity", 0.7)

  def refresh() = call()
}