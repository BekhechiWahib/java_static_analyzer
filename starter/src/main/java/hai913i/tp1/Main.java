package hai913i.tp1;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.compiler.IProblem;

import hai913i.tp1.graph.CallGraph;
import hai913i.tp1.metrics.Metrics;
import hai913i.tp1.models.FieldInfo;
import hai913i.tp1.models.MethodInfo;
import hai913i.tp1.models.MethodInvocationInfo;
import hai913i.tp1.models.TypeInfo;
import hai913i.tp1.parse.JdtParser;
import hai913i.tp1.parse.JdtParser.ParsedFile;
import hai913i.tp1.parse.ProjectSources;
import hai913i.tp1.visitor.MyMethodInvocation;
import hai913i.tp1.visitor.ModelCollector;
import hai913i.tp1.visitor.MyASTVisitor;
import hai913i.tp1.visitor.TypeCellector;

/**
 * Point d'entrée en ligne de commande de l'analyseur (version de départ).
 *
 * Le squelette ne vérifie que l'environnement (point de contrôle A0) : il analyse le projet et affiche
 * le nombre d'unités de compilation et d'erreurs. Tout le reste est à concevoir : extraction de la
 * structure, appels, métriques, graphe d'appel, options comme le seuil X.
 *
 * Usage : java -jar target/hai913i-tp1-analyzer.jar DOSSIER_DU_PROJET
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage : java -jar target/hai913i-tp1-analyzer.jar DOSSIER_DU_PROJET");
            System.exit(2);
        }
        Path project = Path.of(args[0]);
        ProjectSources sources;
        try {
            sources = ProjectSources.of(project);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage());
            System.exit(3);
            return;
        }

        List<ParsedFile> files = JdtParser.parse(sources, List.of());
        int errors = 0;
        for (ParsedFile file : files) {
            for (IProblem problem : file.unit().getProblems()) {
                if (problem.isError()) {
                    errors++;
                    System.err.println(file.path().getFileName() + ":" + problem.getSourceLineNumber() + " "
                            + problem.getMessage());
                }
            }
        }
        System.out.println("Racine des sources     : " + sources.sourceRoot());
        System.out.println("Unites de compilation  : " + files.size());
        System.out.println("Erreurs de compilation : " + errors);

        // À FAIRE (A1 et suite) : parcourir les AST avec vos visiteurs, construire votre modèle de faits,
        // puis calculer les métriques et le graphe d'appel. Gardez cette classe courte : elle lit les
        // arguments et délègue.

    
        // A0 pour un test rapide : java -jar target/hai913i-tp1-analyzer.jar DOSSIER --astvisitor Dvd.java
        if (args.length >= 3 && args[1].equals("--astvisitor")) {
            for (ParsedFile file : files) {
                if (file.path().getFileName().toString().equals(args[2])) {
                    System.out.println("=== " + file.path().getFileName() + " ===");
                    file.unit().accept(new MyASTVisitor());
                }
            }
        }


// Mode détails des visiteurs de l'étape A :  DOSSIER --ast Fichier.java   ou   DOSSIER --ast all
        if (args.length >= 3 && args[1].equals("--ast")) {
            showAstDetails(files, args[2]);
            return;
        }
        
        int x = 5;   // valeur par défaut pour tester
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--threshold")) {
                try {
                    x = Integer.parseInt(args[i + 1]);
                    if (x < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    System.err.println("Erreur : le seuil X doit être un entier positif ou nul (reçu : " + args[i + 1] + ")");
                    System.exit(2);
                }
            }
        }

        ModelCollector collector = new ModelCollector();
        for (ParsedFile file : files){
             file.unit().accept(collector);
              collector.totalLines += Files.readAllLines(file.path()).size();
            }
        List<TypeInfo> model = collector.types;
        model.sort(Comparator.comparing(TypeInfo::getQualifiedName)); 

        printStructure(model);
        printCalls(model);
        printTotals(model, collector.totalLines);



        Metrics metrics = new Metrics(model, collector.totalLines);

        System.out.println("\n================ B2 : MÉTRIQUES ================");
        System.out.println("Q1  classes                    : " + metrics.classCount());
        System.out.println("Q2  lignes de l'application    : " + metrics.totalLines());
        System.out.println("Q3  méthodes                   : " + metrics.methodCount());
        System.out.println("Q4  paquetages                 : " + metrics.packageCount());
        System.out.printf ("Q5  méthodes / classe          : %.2f%n", metrics.avgMethodsPerClass());
        System.out.printf ("Q6  lignes / méthode           : %.2f%n", metrics.avgLinesPerMethod());
        System.out.printf ("Q7  attributs / classe         : %.2f%n", metrics.avgFieldsPerClass());
        System.out.println("Q8  10% par méthodes           : " + names(metrics.topClassesByMethods()));
        System.out.println("Q9  10% par attributs          : " + names(metrics.topClassesByFields()));
        System.out.println("Q10 dans les deux              : " + names(metrics.topInBoth()));
        System.out.println("Q11 plus de " + x + " méthodes        : " + names(metrics.classesWithMoreMethodsThan(x)));
        System.out.println("Q12 méthodes les plus longues, par classe :");
        for (TypeInfo t : model) {
            List<MethodInfo> top = metrics.longestMethods(t);
            if (!top.isEmpty()) {
                System.out.println("   " + t.getQualifiedName() + " : " + top.stream()
                    .map(m -> m.getName() + " (" + m.getLineCount() + ")").collect(Collectors.joining(", ")));
            }
        }
        System.out.println("Q13 max de paramètres          : " + metrics.maxParams() + " → "
            + metrics.methodsWithMaxParams().stream().map(m -> m.id != null ? m.id : m.getName())
                .collect(Collectors.joining(", ")));
    
    
    
    
    CallGraph graph = new CallGraph(model);

    System.out.println("\n================ B3 : GRAPHE D'APPEL ================");
    System.out.println("noeuds " + graph.nodeCount() + ", arcs " + graph.edgeCount()
        + ", sites d'appel internes " + graph.internalSites()
        + ", appels externes " + graph.externalCalls()
        + ", non résolus " + graph.unresolvedCalls());
    System.out.println("(attendu : 59 noeuds, 44 arcs, 46 sites internes, 48 externes, 0 non résolu)\n");
    graph.printEdges();
    Files.writeString(Path.of("callgraph.dot"), graph.toDot());

        
    }




    private static String names(List<TypeInfo> list) {
        return list.stream().map(TypeInfo::getQualifiedName).collect(Collectors.joining(", "));
    }




    // --------------- détails des visiteurs (A1 à A3)
    private static void showAstDetails(List<ParsedFile> files, String target) {
        for (ParsedFile file : files) {
            String name = file.path().getFileName().toString();
            if (!target.equals("all") && !name.equals(target)) continue;

            System.out.println("\n=== Détails de " + name + " ===");
            file.unit().accept(new TypeCellector());
            MyMethodInvocation mi = new MyMethodInvocation();
            file.unit().accept(mi);
        }
    }

    // --------------- A2 : structure
    private static void printStructure(List<TypeInfo> model) {
        System.out.println("\n================ A2 : STRUCTURE ================");
        for (TypeInfo t : model) {
            System.out.println("\n" + t.getQualifiedName() + " (" + t.getKind() + "), paquetage " + t.getPackageName());
            if (!t.getSuperclasses().isEmpty()) System.out.println("  superclasses : " + t.getSuperclasses());
            if (!t.getInterfaces().isEmpty())   System.out.println("  interfaces   : " + t.getInterfaces());
            for (FieldInfo f : t.getFields()) {
                System.out.println("  attribut : " + f.getVisibility() + " " + f.getType() + " " + f.getName());
            }
            for (MethodInfo m : t.getMethods()) {
                System.out.println("  méthode  : " + m.getName() + " (" + m.getParamCount() + " param.)"
                        + (m.getConstructor() ? " [constructeur]" : "") + ", " + m.getLineCount() + " lignes");
            }
        }
    }

    // --------- A3 : appels, rangés par méthode
    private static void printCalls(List<TypeInfo> model) {
        System.out.println("\n================ A3 : APPELS ================");
        for (TypeInfo t : model) {
            for (MethodInfo m : t.getMethods()) {
                if (m.getCalls().isEmpty()) continue;
                System.out.println("\n" + t.getQualifiedName() + "." + m.getName());
                for (MethodInvocationInfo c : m.getCalls()) {
                    String cible = (c.getTargetMethodSignature() == null) ? "NON RÉSOLUE" : c.getTargetMethodSignature();
                    System.out.println("  ligne " + c.getLineNumber() + " : " + c.getMethodName()
                            + "  | receveur : " + c.getReceiverStaticType() + "  | cible : " + cible);
                }
            }
        }
    }

    // --------- totaux, à comparer aux points de contrôle
    private static void printTotals(List<TypeInfo> model, int totalLines) {
        Set<String> projectTypes = new HashSet<>();
        int classes = 0, interfaces = 0, enums = 0, records = 0;
        int methods = 0, constructors = 0, fields = 0;
        int internal = 0, external = 0, unresolved = 0;

        for (TypeInfo t : model) {
            projectTypes.add(t.getQualifiedName());
            switch (t.getKind()) {
                case "class" -> classes++;
                case "interface" -> interfaces++;
                case "enum" -> enums++;
                default -> records++;
            }
            fields += t.getFields().size();
            for (MethodInfo m : t.getMethods()) {
                methods++;
                if (m.getConstructor()) constructors++;
            }
        }
        for (TypeInfo t : model) {
            for (MethodInfo m : t.getMethods()) {
                for (MethodInvocationInfo c : m.getCalls()) {
                    String target = c.getTargetMethodSignature();
                    if (target == null || target.equals("non résolue")) {
                        unresolved++;
                    } else {
                        int i = target.indexOf('#');          // id de la forme  Type#méthode(params)
                        if (i > 0 && projectTypes.contains(target.substring(0, i))) internal++;
                        else external++;
                    }
                }
            }
        }

        System.out.println("\n================ TOTAUX (points de contrôle) ================");
        System.out.println("types      : " + model.size() + "   (attendu 15 : 12 classes, 2 interfaces, 1 enum)");
        System.out.println("  classes " + classes + ", interfaces " + interfaces + ", enums " + enums + ", records " + records);
        System.out.println("méthodes   : " + methods + "   (attendu 59, dont 9 constructeurs : obtenu " + constructors + ")");
        System.out.println("attributs  : " + fields + "   (attendu 21)");
        System.out.println("appels     : " + (internal + external + unresolved) + "   (attendu 94 : 46 internes, 48 externes, 0 non résolu)");
        System.out.println("  internes " + internal + ", externes " + external + ", non résolus " + unresolved);
        System.out.println("lignes     : " + totalLines + "   (attendu 493)");
    }
}

