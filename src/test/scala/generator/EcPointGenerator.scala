package generator

import ec.{Curve, EllipticCurveConfig, EllipticCurvePoint}
import zio.test.Gen

object EcPointGenerator {

  val secp256k1 = Gen.int.map(Curve.Secp256k1.g_point.multiply(_))

}
