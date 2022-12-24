package ec

import generator.EcPointGenerator
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, check}

object EllipticCurvePointSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("")(
    test("A+A = 2*A") {
      check(EcPointGenerator.secp256k1) { point =>
        assertTrue(point.add(point) == point.multiply(2))
      }
    },
    test("-(x, y) = (x, -y)") {
      check(EcPointGenerator.secp256k1) { point =>
        val mirror = point.multiply(-1)
        assertTrue(point.x == mirror.x && point.y == mirror.y)
      }
    }
  )
}
