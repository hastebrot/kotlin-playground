fun main(args: Array<String>) {
    for (i in -40..40) {
        for (r in -40..40) {
            val int = mandelbrot(Complex(r - 25.0, i.toDouble()) / 35.0, 256)
            print(int?.let { 'a' + (it % 26) } ?: ' ')
        }
        println()
    }
}

tailrec fun mandelbrot(c: Complex,
                       maxIterations: Int,
                       z: Complex = Complex.zero,
                       iterations: Int = 0): Int? = when {
    iterations == maxIterations -> null
    z.abs > 2.0                 -> iterations
    else                        -> mandelbrot(c, maxIterations, (z * z) + c, iterations + 1)
}

data class Complex(val real: Double,
                   val imaginary: Double) {

    companion object {
        val zero = Complex(0.0, 0.0)
    }

    val reciprocal: Complex by lazy {
        val scale = (real * real) + (imaginary * imaginary)
        Complex(real / scale, -imaginary / scale)
    }

    val abs: Double by lazy {
        Math.hypot(real, imaginary)
    }

    operator fun unaryMinus(): Complex = Complex(-real, -imaginary)
    operator fun plus(other: Double): Complex = Complex(real + other, imaginary)
    operator fun minus(other: Double): Complex = Complex(real - other, imaginary)
    operator fun times(other: Double): Complex = Complex(real * other, imaginary * other)
    operator fun div(other: Double): Complex = Complex(real / other, imaginary / other)

    operator fun plus(other: Complex): Complex =
            Complex(real + other.real, imaginary + other.imaginary)

    operator fun minus(other: Complex): Complex =
            Complex(real - other.real, imaginary - other.imaginary)

    operator fun times(other: Complex): Complex =
            Complex((real * other.real) - (imaginary * other.imaginary),
                    (real * other.imaginary) + (imaginary * other.real))

    operator fun div(other: Complex): Complex = this * other.reciprocal

}
