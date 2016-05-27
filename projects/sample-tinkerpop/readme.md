Apache TinkerPop is a graph computing framework for both graph databases (OLTP; On-line
Transaction Processing) and graph analytic systems (OLAP; On-line Analytical Processing).

TinkerPop2 and below made a sharp distinction between the various TinkerPop projects:
**Blueprints** (a property graph model interface), **Pipes** (a lazy, data flow framework),
**Gremlin** (a graph traversal language), **Frames** (an object-to-graph mapper),
**Furnace** (a graph algorithms package), and **Rexster** (a graph server).

With TinkerPop3, all of these projects have been merged and are generally known as Gremlin.
**Blueprints** → Gremlin Structure API, **Pipes** → `GraphTraversal`, **Frames** → `Traversal`,
**Furnace** → `GraphComputer` and `VertexProgram`, **Rexster** → GremlinServer.

### Packages

- `org.apache.tinkerpop.gremlin.structure`
  - `Graph`, `Element`, `Vertex`, `Edge`, `Property<V>`, `VertexProperty<V>`

- `org.apache.tinkerpop.gremlin.process.traversal`
  - `TraversalSource`, `Traversal<S, E>`, `GraphTraversal`

- `org.apache.tinkerpop.gremlin.process.computer`
  - `GraphComputer`, `VertexProgram`, `MapReduce`
