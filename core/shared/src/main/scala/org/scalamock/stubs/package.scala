package org.scalamock

import scala.language.higherKinds

package object stubs {
  /** Indicates that object of type T was generated */
  type Stub[+T] <: T
}
