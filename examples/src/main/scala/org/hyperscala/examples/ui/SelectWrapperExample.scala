package org.hyperscala.examples.ui

import org.hyperscala.html._
import org.hyperscala.examples.Example
import org.hyperscala.ui.wrapped.SelectWrapper
import org.powerscala.Country

import scala.language.implicitConversions
import org.hyperscala.event.JavaScriptEvent

/**
 * @author Matt Hicks <matt@outr.com>
 */
class SelectWrapperExample extends Example {
  implicit def s2Country(s: String) = if (s.nonEmpty) Country(s) else null
  implicit def country2S(c: Country) = if (c != null) c.name else ""

  val select = new tag.Select(id = "select")

  contents += select

  val wrapper = new SelectWrapper[Country](select) {
    def value2T(value: String) = if (value != null && value.nonEmpty) Country(value) else null

    def t2Value(t: Country) = if (t != null) t.name else ""

    def t2Content(t: Country) = if (t != null) t.fullName else "--- Select one ---"
  }
  wrapper.values += null
  wrapper.values ++= Country.values
  wrapper.selected.change.on {
    case evt => println(s"Old: ${evt.oldValue}, New: ${evt.newValue}")
  }

  contents += new tag.Br

  contents += new tag.Button(content = "Select USA") {
    clickEvent := JavaScriptEvent()
    clickEvent.on {
      case evt => wrapper.selected := Country.US
    }
  }
}
