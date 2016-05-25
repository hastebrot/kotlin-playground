import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph

fun main(args: Array<String>) {
    val graph = TinkerFactory.createModern()
    println(graph)
    println(graph.vertices().asSequence().count())

    val t = graph.traversal()
    println(t.V().has("name", "vadas").valueMap<Any>().toList())

    val c = graph.traversal().withComputer()
    println(c.withPath())

    val customGraph = TinkerGraph.open()
    val tom = customGraph.addVertex(T.id, 1)
    val jerry = customGraph.addVertex(T.id, 2)
    customGraph.addEdge(tom, jerry, "hates", T.id, 3)

    println(customGraph.traversal().V().id().toList())
}

fun TinkerGraph.addEdge(inVertex: Vertex,
                        outVertex: Vertex,
                        label: String,
                        vararg keyValues: Any): Edge {
    return inVertex.addEdge(label, outVertex, *keyValues)
}
