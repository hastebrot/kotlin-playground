import io.kotlintest.properties.TableTesting
import io.kotlintest.specs.StringSpec as _StringSpec

abstract class StringSpec(body: StringSpec.() -> Unit = {}) : _StringSpec() {
    init { body(this) }
}

class StringSpecExample : StringSpec({

    "strings.size should return size of string" {
        "hello".length shouldBe 5
        "hello" should haveLength(5)
    }

    "strings should support config" {
        "hello".length shouldBe 5
    }.config(invocations = 5)

})

class TableTestExample : TableTesting, StringSpec({

    "should add two integers" {
        val table = table(
            headers("a", "b", "result"),
            row(5, 5, 10),
            row(4, 6, 10),
            row(3, 7, 10)
        )
        forAll(table) { a, b, result ->
            a + b shouldEqual result
        }
    }

})

class PropertyTestExample: StringSpec({

    "should add length of strings" {
        forAll { a: String, b: String ->
            (a + b).length == a.length + b.length
        }
    }

})
