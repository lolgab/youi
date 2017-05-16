package io.youi.component

import java.util.concurrent.atomic.AtomicBoolean

import com.outr.pixijs._
import io.youi.Updates
import io.youi.component.event.Events
import io.youi.style.Theme
import reactify.{Dep, Val, Var}

trait Component extends Updates {
  protected[component] def instance: PIXI.Container
  protected def defaultTheme: Theme

  private val dirty = new AtomicBoolean(false)

  lazy val parent: Val[Option[Container]] = Var(None)

  val theme: Var[Theme] = Var(defaultTheme)
  val interactive: Var[Boolean] = Var.bound(theme.interactive, (b: Boolean) => instance.interactive = b, setImmediately = true)
  val visible: Var[Boolean] = Var.bound(theme.visible, (b: Boolean) => instance.visible = b)

  lazy val event: Events = new Events(this)

  object position {
    lazy val x: Var[Double] = Var(instance.x)
    lazy val y: Var[Double] = Var(instance.y)

    lazy val left: Var[Double] = x
    lazy val center: Dep[Double, Double] = Dep(left, size.width / 2.0)
    lazy val right: Dep[Double, Double] = Dep(left, size.width)

    lazy val top: Var[Double] = y
    lazy val middle: Dep[Double, Double] = Dep(top, size.height / 2.0)
    lazy val bottom: Dep[Double, Double] = Dep(top, size.height)
  }

  lazy val rotation: Var[Double] = Var(0.0)

  object scale {
    lazy val x: Var[Double] = Var(1.0)
    lazy val y: Var[Double] = Var(1.0)
  }

  object skew {
    lazy val x: Var[Double] = Var(0.0)
    lazy val y: Var[Double] = Var(0.0)
  }

  object size {
    object measured {
      lazy val width: Var[Double] = Var(0.0)
      lazy val height: Var[Double] = Var(0.0)
    }

    object width extends Var[Double](() => measured.width()) {
      def reset(): Unit = set(measured.width())
    }
    object height extends Var[Double](() => measured.height()) {
      def reset(): Unit = set(measured.height())
    }

    lazy val center: Val[Double] = Val(width / 2.0)
    lazy val middle: Val[Double] = Val(height / 2.0)    // TODO: diagnose why this isn't being updated properly
  }

  object pivot {
    lazy val x: Var[Double] = Var(size.center())
    lazy val y: Var[Double] = Var(size.middle())
  }

  position.x.on(dirty.set(true))
  position.y.on(dirty.set(true))
  size.width.on(dirty.set(true))
  size.height.on(dirty.set(true))
  scale.x.on(dirty.set(true))
  scale.y.on(dirty.set(true))
  skew.x.on(dirty.set(true))
  skew.y.on(dirty.set(true))
  rotation.on(dirty.set(true))

  protected[youi] def prop[T](get: => T, set: T => Unit): Var[T] = {
    val v = Var[T](get)
    v.attach(set)
    v
  }

  override def update(delta: Double): Unit = {
    if (dirty.compareAndSet(true, false)) {
      updateTransform()
    }

    super.update(delta)
  }

  protected def updateTransform(): Unit = {
    instance.width = size.width()
    instance.height = size.height()
    instance.setTransform(
      x = position.x() + pivot.x(),
      y = position.y() + pivot.y(),
      scaleX = scale.x(),
      scaleY = scale.y(),
      rotation = rotation() * (2.0 * math.Pi),
      skewX = skew.x(),
      skewY = skew.y(),
      pivotX = pivot.x() / scale.x(),
      pivotY = pivot.y() / scale.y()
    )
  }
}