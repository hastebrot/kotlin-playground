package hastebrot.playground.jgraph

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

fun main(args: Array<String>) {
    val graph0 = buildGraph()
    println(graph0)

    val graph1 = buildGraphFromAdjacencyLists()
    println(graph1)
}

private fun buildGraph(): SimpleGraph<*, *> {
    val nodes = setOf("a", "b", "c", "d", "e")
    val edges = setOf(
        "a" to "b",
        "a" to "d",
        "a" to "d",
        "a" to "e",
        "b" to "c",
        "c" to "d"
    )

    val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)
    nodes.forEach { graph.addVertex(it) }
    edges.forEach { graph.addEdge(it.first, it.second) }
    return graph
}

private fun buildGraphFromAdjacencyLists(): SimpleGraph<*, *> {
    val adjacencyLists: Map<String, List<String>> = mapOf(
        "a" to listOf("b", "d", "d", "e"),
        "b" to listOf("c", "a"),
        "c" to listOf("b", "d"),
        "d" to listOf("a", "a", "c"),
        "e" to listOf("a")
    )

    val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)
    adjacencyLists.forEach {
        graph.addVertex(it.key)
    }
    adjacencyLists.forEach {
        val (source, targets) = it
        targets.forEach { target ->
            graph.addEdge(source, target)
        }
    }
    return graph
}
