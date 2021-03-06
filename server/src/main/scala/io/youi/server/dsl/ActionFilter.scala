package io.youi.server.dsl

import io.youi.http.HttpConnection

class ActionFilter(f: HttpConnection => Unit) extends ConnectionFilter {
  override def filter(connection: HttpConnection): Option[HttpConnection] = {
    f(connection)
    Some(connection)
  }
}
