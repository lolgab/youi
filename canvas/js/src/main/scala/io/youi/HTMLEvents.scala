package io.youi

import io.youi.event.KeyEvent
import org.scalajs.dom._
import org.scalajs.dom.raw.Event
import reactify.Channel

import scala.scalajs.js

class HTMLEvents(element: html.Element) {
  def hasPointerSupport: Boolean = HTMLEvents.hasPointerSupport

  lazy val change: Channel[Event] = events("change")
  lazy val click: Channel[MouseEvent] = events("click")
  lazy val doubleClick: Channel[MouseEvent] = events("dblclick")
  lazy val contextMenu: Channel[MouseEvent] = events("contextmenu")
  lazy val focus: Channel[FocusEvent] = events("focus")
  lazy val blur: Channel[FocusEvent] = events("blur")
  object key {
    lazy val down: Channel[KeyEvent] = keyEvents("keydown", KeyEvent.Type.Down)
    lazy val press: Channel[KeyEvent] = keyEvents("keypress", KeyEvent.Type.Press)
    lazy val up: Channel[KeyEvent] = keyEvents("keyup", KeyEvent.Type.Up)
  }
  object mouse {
    lazy val enter: Channel[MouseEvent] = mouseEvents("enter")
    lazy val over: Channel[MouseEvent] = mouseEvents("over")
    lazy val move: Channel[MouseEvent] = mouseEvents("move")
    lazy val down: Channel[MouseEvent] = mouseEvents("down")
    lazy val up: Channel[MouseEvent] = mouseEvents("up")
    lazy val leave: Channel[MouseEvent] = mouseEvents("leave")
    lazy val out: Channel[MouseEvent] = mouseEvents("out")
    lazy val cancel: Channel[MouseEvent] = mouseEvents("cancel")
    lazy val wheel: Channel[WheelEvent] = events("wheel")
  }

  protected def keyEvents(eventType: String, `type`: KeyEvent.Type): Channel[KeyEvent] = {
    val originalEvents = events[KeyboardEvent](eventType)
    originalEvents.map(ui.keyboardEvent2KeyEvent(_, `type`))
  }

  protected def events[E <: Event](eventType: String, stopPropagation: Boolean = false): Channel[E] = {
    val channel = Channel[E]
    element.addEventListener(eventType, (evt: E) => {
      if (stopPropagation) {
        evt.preventDefault()
        evt.stopPropagation()
      }
      channel := evt
    })
    channel
  }

  protected def mouseEvents(eventType: String, stopPropagation: Boolean = false): Channel[MouseEvent] = {
    val eventName = if (hasPointerSupport) {
      s"pointer$eventType"
    } else {
      s"mouse$eventType"
    }
    val channel = Channel[MouseEvent]
    element.addEventListener(eventName, (evt: MouseEvent) => {
      if (stopPropagation) {
        evt.preventDefault()
        evt.stopPropagation()
      }
      channel := evt
    })
    channel
  }
}

object HTMLEvents {
  lazy val hasPointerSupport: Boolean = js.typeOf(js.Dynamic.global.PointerEvent) != "undefined"
}