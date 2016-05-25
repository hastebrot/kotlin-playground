import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

fun main(args: Array<String>) {
    sampleGraphSets()
    sampleGraphMapList()
}

private fun sampleGraphSets() {
    val nodes = setOf("a", "b", "c", "d", "e")
    val edges = setOf(
        "a" to "b",
        "a" to "d",
        "a" to "d",
        "a" to "e",
        "b" to "c",
        "c" to "d"
    )

    val graph = buildGraph(nodes, edges)
    println(graph)
}

private fun sampleGraphMapList() {
    val adjacencyList: Map<String, List<String>> = mapOf(
        "a" to listOf("b", "d", "d", "e"),
        "b" to listOf("c", "a"),
        "c" to listOf("b", "d"),
        "d" to listOf("a", "a", "c"),
        "e" to listOf("a")
    )
    val graph = buildGraph(adjacencyList)
    println(graph)
}

private fun <T> buildGraph(nodes: Set<T>,
                           edges: Set<Pair<T, T>>): SimpleGraph<T, DefaultEdge> {

    val graph = SimpleGraph<T, DefaultEdge>(DefaultEdge::class.java)
    nodes.forEach { graph.addVertex(it) }
    edges.forEach { graph.addEdge(it.first, it.second) }
    return graph
}

private fun <T> buildGraph(adjacencyList: Map<T, List<T>>): SimpleGraph<T, DefaultEdge> {
    val graph = SimpleGraph<T, DefaultEdge>(DefaultEdge::class.java)

//    for ((source, targets) in adjacencyList) {
//        graph.addVertex(source)
//    }
//    for ((source, targets) in adjacencyList) {
//        for (target in targets) {
//            graph.addEdge(source, target)
//        }
//    }

    adjacencyList.keys.forEach { source ->
        graph.addVertex(source)
    }
    adjacencyList.entries.forEachEntry { source, targets ->
        targets.forEach { target ->
            graph.addEdge(source, target)
        }
    }

//    adjacencyList.keys.forEach { source -> graph.addVertex(source) }
//    adjacencyList.pairs.forEachPair { source, target -> graph.addEdge(source, target) }

    return graph
}

private inline fun <K, V> Iterable<Map.Entry<K, V>>.forEachEntry(action: (K, V) -> Unit) =
    forEach { action(it.key, it.value) }

private inline fun <A, B> Iterable<Pair<A, B>>.forEachPair(action: (A, B) -> Unit) =
    forEach { action(it.first, it.second) }

private val <K, V> Map<K, List<V>>.pairs: Iterable<Pair<K, V>>
    get() = flatMap { entry -> entry.value.map { value -> entry.key to value } }

