## kotlin-playground

### `projects/sample-tinkerpop`

Apache TinkerPop is a graph computing framework for both graph databases (OLTP) and
graph analytic systems (OLAP).

TinkerPop2 and below made a sharp distinction between the various TinkerPop projects:
**Blueprints** (a property graph model interface), **Pipes** (a lazy, data flow framework),
**Gremlin** (a graph traversal language), **Frames** (an object-to-graph mapper),
**Furnace** (a graph algorithms package), and **Rexster** (a graph server).

With TinkerPop3, all of these projects have been merged and are generally known as Gremlin.
**Blueprints** → Gremlin Structure API, **Pipes** → `GraphTraversal`, **Frames** → `Traversal`,
**Furnace** → `GraphComputer` and `VertexProgram`, **Rexster** → GremlinServer.
