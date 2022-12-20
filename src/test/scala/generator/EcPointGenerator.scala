package generator

import ec.{EllipticCurveConfig, EllipticCurvePoint}
import zio.test.Gen

object EcPointGenerator {

  val secp256k1CurveConfig = EllipticCurveConfig(0, 7, BigInt("115792089237316195423570985008687907853269984665640564039457584007908834671663"))

  val x = BigInt("55066263022277343669578718895168534326250603453777594175500187360389116729240")
  val y = BigInt("32670510020758816978083085130507043184471273380659243275938904335757337482424")
  val n = BigInt("115792089237316195423570985008687907852837564279074904382605163141518161494337")
  val g_point = EllipticCurvePoint(x, y, secp256k1CurveConfig)

  val secp256k1 = Gen.int.map(g_point.multiply(_))

}