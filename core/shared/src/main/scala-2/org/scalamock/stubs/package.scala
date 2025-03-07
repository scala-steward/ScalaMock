package org.scalamock

package object stubs {
  /** Indicates that object of type T was generated */
  type Stub[+T] <: T
}
