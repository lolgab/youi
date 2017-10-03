package io.youi.draw

import io.youi.Context
import io.youi.component.Component
import io.youi.spatial.BoundingBox

case class Group(drawables: List[Drawable]) extends Drawable {
  override lazy val boundingBox: BoundingBox = {
    val bounds = drawables.collect {
      case d if d.boundingBox != BoundingBox.zero => d.boundingBox
    }
    if (bounds.nonEmpty) {
      var b = bounds.head
      bounds.tail.foreach { bound =>
        b = b.merge(bound)
      }
      b
    } else {
      BoundingBox.zero
    }
  }

  def withDrawables(drawables: Drawable*): Group = Group(this.drawables ::: drawables.toList)

  override def draw(component: Component, context: Context): Unit = {
    drawables.foreach(_.draw(component, context))
  }
}

object Group {
  def apply(drawables: Drawable*): Group = Group(drawables.toList)
}