package hastebrot.playground.jgraph

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

fun main(args: Array<String>) {
    val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)

    graph.addVertex("v1")
    graph.addVertex("v2")
    graph.addVertex("v3")
    graph.addVertex("v4")

    graph.addEdge("v1", "v2")
    graph.addEdge("v2", "v3")
    graph.addEdge("v3", "v4")
    graph.addEdge("v4", "v1")

    println(graph)
}
