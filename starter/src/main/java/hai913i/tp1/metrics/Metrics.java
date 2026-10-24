package hai913i.tp1.metrics;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import hai913i.tp1.models.MethodInfo;
import hai913i.tp1.models.TypeInfo;

public class Metrics {

    private final List<TypeInfo> types;
    private final int totalLines;

    public Metrics(List<TypeInfo> types, int totalLines) {
        this.types = types;
        this.totalLines = totalLines;
    }

    // ---------- Q1 à Q7 : métriques de base ----------
    public int classCount()   { return types.size(); }                       // Q1
    public int totalLines()   { return totalLines; }                         // Q2
    public int methodCount()  {                                              // Q3
        return types.stream().mapToInt(t -> t.getMethods().size()).sum();
    }
    public int packageCount() {                                              // Q4
        return (int) types.stream().map(TypeInfo::getPackageName).distinct().count();
    }
    public double avgMethodsPerClass() {                                     // Q5
        return types.isEmpty() ? 0 : (double) methodCount() / classCount();
    }
    public double avgLinesPerMethod() {                                      // Q6 
        int lines = 0, n = 0;
        for (TypeInfo t : types)
            for (MethodInfo m : t.getMethods())
                if (m.getLineCount() > 0) { lines += m.getLineCount(); n++; }
        return n == 0 ? 0 : (double) lines / n;
    }

    public double avgFieldsPerClass() {                                      // Q7
        int fields = types.stream().mapToInt(t -> t.getFields().size()).sum();
        return types.isEmpty() ? 0 : (double) fields / classCount();
    }

    // ---------- Règle des « 10 % » (§ 4.3), utilisée par Q8, Q9 et Q12 ----------
    private static <T> List<T> topTenPercent(List<T> candidates, ToIntFunction<T> value) {
        int n = candidates.size();
        if (n == 0) return List.of();
        int k = (n + 9) / 10;                                  
        List<T> sorted = new ArrayList<>(candidates);
        sorted.sort((a, b) -> Integer.compare(value.applyAsInt(b), value.applyAsInt(a))); 
        int threshold = value.applyAsInt(sorted.get(k - 1));    
        List<T> result = new ArrayList<>();
        for (T c : sorted) {
            int v = value.applyAsInt(c);
            if (v >= threshold && v > 0) result.add(c);         
        }
        return result;
    }

    // ---------- Q8, Q9, Q10 ----------
    public List<TypeInfo> topClassesByMethods() {                            // Q8
        return topTenPercent(types, t -> t.getMethods().size());
    }
    public List<TypeInfo> topClassesByFields() {                             // Q9
        return topTenPercent(types, t -> t.getFields().size());
    }
    public List<TypeInfo> topInBoth() {                                      // Q10
        List<TypeInfo> fields = topClassesByFields();
        return topClassesByMethods().stream().filter(fields::contains).collect(Collectors.toList());
    }

    // ---------- Q11 : plus de X méthodes ----------
    public List<TypeInfo> classesWithMoreMethodsThan(int x) {
        return types.stream().filter(t -> t.getMethods().size() > x).collect(Collectors.toList());
    }

    // ---------- Q12 : par classe, les 10 % de méthodes les plus longues ----------
    public List<MethodInfo> longestMethods(TypeInfo type) {
        return topTenPercent(type.getMethods(), MethodInfo::getLineCount);
    }

    // ---------- Q13 : nombre maximal de paramètres ----------
    public int maxParams() {
        return types.stream().flatMap(t -> t.getMethods().stream())
                .mapToInt(MethodInfo::getParamCount).max().orElse(0);
    }
    public List<MethodInfo> methodsWithMaxParams() {
        int max = maxParams();
        return types.stream().flatMap(t -> t.getMethods().stream())
                .filter(m -> m.getParamCount() == max).collect(Collectors.toList());
    }
}