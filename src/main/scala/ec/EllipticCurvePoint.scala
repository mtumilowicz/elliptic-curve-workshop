package ec

import scala.collection.mutable.ListBuffer

case class EllipticCurvePoint(x: BigInt, y: BigInt, config: EllipticCurveConfig) {

    val a = config.a
    val b = config.b
    val p = config.p

    if (y.pow(2).mod(p) != (x.pow(3) + a * x + b).mod(p)) throw new Exception("The point is not on the curve")

    def isEqual(point: EllipticCurvePoint): Boolean = x == point.x && y == point.y

    def add(point: EllipticCurvePoint): EllipticCurvePoint = {
      val slope = if (isEqual(point)) {
        ((3 * point.x.pow(2)) * (2 * point.y).modInverse(p)).mod(p)
      } else {
        ((point.y - y) * (point.x - x).modInverse(p)).mod(p)
      }

      val newX = (slope.pow(2) - point.x - x).mod(p)
      val newY = (slope * (x - newX) - y).mod(p)

      EllipticCurvePoint(newX, newY, config)
    }

    def multiply(times: BigInt): EllipticCurvePoint = {
      var currentPoint = this
      var currentCoefficient = BigInt(1)
      val previousPoints = ListBuffer.empty[(BigInt, EllipticCurvePoint)]
      while (currentCoefficient < times) {
        previousPoints += ((currentCoefficient, currentPoint))
        if (2*currentCoefficient <= times) {
          currentPoint = currentPoint.add(currentPoint)
          currentCoefficient *= 2
        }
        else {
          var nextPoint = this
          var nextCoefficient = BigInt(1)
          for ((previousCoefficient, previousPoint) <- previousPoints) {
            if (previousCoefficient + currentCoefficient <= times) {
              if (previousPoint.x != currentPoint.x) {
                nextCoefficient = previousCoefficient
                nextPoint = previousPoint
              }
            }
          }
          currentPoint = currentPoint.add(nextPoint)
          currentCoefficient += nextCoefficient
        }
      }
      currentPoint
    }
  }