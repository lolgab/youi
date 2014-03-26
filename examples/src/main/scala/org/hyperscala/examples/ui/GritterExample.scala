package org.hyperscala.examples.ui

import org.hyperscala.html._
import org.hyperscala.web._
import org.hyperscala.jquery.Gritter
import org.hyperscala.examples.Example
import language.reflectiveCalls
import org.hyperscala.realtime.RealtimeEvent

/**
 * @author Matt Hicks <matt@outr.com>
 */
class GritterExample extends Example {
  this.require(Gritter)

  contents += new tag.Button(content = "Show Message") {
    clickEvent := RealtimeEvent()

    clickEvent.on {
      case evt => {
        Gritter.add(this.webpage, "Test Title", "Example message that will disappear after a few seconds.", "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7c/Go-home.svg/48px-Go-home.svg.png", time = 3000)
        Gritter.add(this.webpage, "Test Title", "Example message that will not disappear after a few seconds.", sticky = true)
      }
    }
  }
}
