package io.youi.app
import com.outr.reactify.{Val, Var}
import io.youi.http.{Connection, WebSocketUtil}
import io.youi.net.URL
import org.scalajs.dom._

trait ClientApplication extends YouIApplication {
  override val connection: Connection = new Connection
  override val connections: Val[Set[Connection]] = Val(Set(connection))
  val webSocket: Var[Option[WebSocket]] = Var(None)

  def connect(): Unit = synchronized {
    disconnect()
    val url = URL(s"ws://${window.location.host}$connectionPath")
    webSocket := Some(WebSocketUtil.connect(url, connection))
  }

  def disconnect(): Unit = synchronized {
    webSocket().foreach { ws =>
      if (ws.readyState == WebSocket.OPEN) {
        ws.close()
      }
      webSocket := None
    }
  }

  def close(): Unit = {
    connection.close()
    disconnect()
  }
}