package ecdsa

import ec._

import scala.util.Random

case class Notary(ecConstants: EllipticCurveConstants) {

  val n = ecConstants.n
  val g = ecConstants.g

  def signMessage(message: BigInt, privateKey: BigInt): (BigInt, BigInt) = {
    val k = BigInt(n.bitLength, new Random())
    val r_point = g.multiply(k)
    val r = r_point.x.mod(n)
    if (r == 0) signMessage(message, privateKey)
    val k_inverse = k.modInverse(n)
    val s = (k_inverse * (message + r * privateKey)).mod(n)
    (r, s)
  }

  def verify_signature(signature: (BigInt, BigInt), message: BigInt, public_key: EllipticCurvePoint): Boolean = {
    val (r, s) = signature
    val s_inverse = s.modInverse(n)
    val u = (message * s_inverse).mod(n)
    val v = (r * s_inverse).mod(n)
    val c_point = g.multiply(u).add(public_key.multiply(v))
    c_point.x == r
  }

}
