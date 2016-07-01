import com.winterbe.expekt.expect

fun main(args: Array<String>) {
    val width = 5
    val height = 8
    val array = Array(width) { IntArray(height) { 0 }}

    expect(array.size).equal(5)
    expect(array.first().size).equal(8)
    expect(array.first().first()).equal(0)
}
