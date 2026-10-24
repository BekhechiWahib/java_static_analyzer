package hai913i.tp1.graph;

import java.util.*;

import hai913i.tp1.models.MethodInfo;
import hai913i.tp1.models.MethodInvocationInfo;
import hai913i.tp1.models.TypeInfo;

public class CallGraph {

    private final Set<String> nodes = new TreeSet<>();
    private final Map<String, Map<String, Integer>> out = new TreeMap<>();   // f -> (g -> poids)
    private final Map<String, Map<String, Integer>> in = new TreeMap<>();    // g -> (f -> poids), graphe inversé

    private int internalSites = 0, externalCalls = 0, unresolvedCalls = 0;

    public CallGraph(List<TypeInfo> model) {
        // 1. les nœuds : toutes les méthodes du projet
        for (TypeInfo t : model)
            for (MethodInfo m : t.getMethods())
                if (m.getId() != null) nodes.add(m.getId());

        // 2. les arcs
        for (TypeInfo t : model) {
            for (MethodInfo m : t.getMethods()) {
                for (MethodInvocationInfo c : m.getCalls()) {
                    String target = c.getTargetMethodSignature();
                    if (target == null) {
                        unresolvedCalls++;                       // non résolu : compté, pas d'arc
                    } else if (nodes.contains(target)) {
                        internalSites++;
                        out.computeIfAbsent(m.getId(), k -> new TreeMap<>()).merge(target, 1, Integer::sum);
                        in.computeIfAbsent(target, k -> new TreeMap<>()).merge(m.getId(), 1, Integer::sum);
                    } else {
                        externalCalls++;                         // externe : compté, pas de nœud
                    }
                }
            }
        }
    }

    // ---------- chiffres ----------
    public int nodeCount()      { return nodes.size(); }
    public int edgeCount()      { return out.values().stream().mapToInt(Map::size).sum(); }
    public int internalSites()  { return internalSites; }
    public int externalCalls()  { return externalCalls; }
    public int unresolvedCalls(){ return unresolvedCalls; }

    // ---------- interrogation dans les deux sens ----------
    public Map<String, Integer> callees(String id) { return out.getOrDefault(id, Map.of()); }  // appelées
    public Map<String, Integer> callers(String id) { return in.getOrDefault(id, Map.of()); }   // appelantes
    public boolean contains(String id) { return nodes.contains(id); }

    /** Retrouve un nœud par son id exact, sinon par morceau de nom (ex. "Loanable#checkOut"). */
    public List<String> find(String query) {
        if (nodes.contains(query)) return List.of(query);
        List<String> res = new ArrayList<>();
        for (String n : nodes) if (n.contains(query)) res.add(n);
        return res;
    }

    public void printEdges() {
        for (var e : out.entrySet())
            for (var g : e.getValue().entrySet())
                System.out.println(e.getKey() + " -> " + g.getKey() + "  (poids " + g.getValue() + ")");
    }

    public String toDot() {
        StringBuilder sb = new StringBuilder("digraph callgraph {\n  rankdir=LR;\n  node [shape=box, fontsize=10];\n");
        for (String n : nodes) sb.append("  \"").append(n).append("\";\n");
        for (var e : out.entrySet())
            for (var g : e.getValue().entrySet())
                sb.append("  \"").append(e.getKey()).append("\" -> \"").append(g.getKey())
                  .append("\" [label=\"").append(g.getValue()).append("\"];\n");
        return sb.append("}\n").toString();
    }
}