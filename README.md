## kotlin-playground

### sample-tinkerpop

Apache TinkerPop is a graph computing framework for both graph databases (OLTP) and
graph analytic systems (OLAP).

TinkerPop2 and below made a sharp distinction between the various TinkerPop projects:
Blueprints, Pipes, Gremlin, Frames, Furnace, and Rexster.

- Rexster: A graph server
- Furnace: A graph algorithms package
- Frames: An object-to-graph mapper
- Gremlin: A graph traversal language
- Pipes: A lazy, data flow framework
- Blueprints: A property graph model interface

With TinkerPop3, all of these projects have been merged and are generally known as Gremlin.
**Blueprints** → Gremlin Structure API, **Pipes** → `GraphTraversal`, **Frames** → `Traversal`,
**Furnace** → `GraphComputer` and `VertexProgram`, **Rexster** → GremlinServer.
