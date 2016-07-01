fun main(args: Array<String>) {
    val query = buildQuery {
        (User.ID equal 123) or
            ((User.NAME startsWith "foo") and
                not(User.NAME endsWith "bar"))
    }

    // Query(text=((id equal 123) or ((name startsWith foo) and (name endsWith bar))))
    println(query)
}

fun buildQuery(builder: QueryBuilder.() -> Query): Query {
    return builder(QueryBuilder())
}

class Attribute(val name: String)
data class Query(val text: String)
interface Model

class QueryBuilder {
    infix fun <A> Attribute.equal(value: A) = Query("($name equal $value)")
    infix fun <A> Attribute.startsWith(value: A) = Query("($name startsWith $value)")
    infix fun <A> Attribute.endsWith(value: A) = Query("($name endsWith $value)")

    infix fun Query.and(query: Query) = Query("($text and ${query.text})")
    infix fun Query.or(query: Query) = Query("($text or ${query.text})")
    fun not(query: Query) = Query("(not ${query.text})")
}

class User : Model {
    companion object {
        val ID: Attribute = Attribute("id")
        val NAME: Attribute = Attribute("name")
    }
}

