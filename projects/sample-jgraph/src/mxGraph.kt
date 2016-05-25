import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.view.mxGraph
import javafx.application.Application
import javafx.embed.swing.SwingNode
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(MainApplication::class.java)
}

class MainApplication : Application() {

    override fun start(stage: Stage) {
        val graph = mxGraph()
        val parent = graph.defaultParent

        graph.update {
            val v1 = graph.insertVertex(parent, null, "hello", 20.0, 20.0, 80.0, 30.0)
            val v2 = graph.insertVertex(parent, null, "world", 240.0, 150.0, 80.0, 30.0)
            graph.insertEdge(parent, null, "edge", v1, v2)
        }

        val graphComponent = mxGraphComponent(graph)
        println(graph.model)

        val sceneRoot = StackPane(SwingNode().apply { content = graphComponent })
        stage.scene = Scene(sceneRoot, 400.0, 400.0)
        stage.show()
    }

    private fun mxGraph.update(block: () -> Unit) {
        model.beginUpdate()
        try {
            block()
        }
        finally {
            model.endUpdate()
        }
    }

}
