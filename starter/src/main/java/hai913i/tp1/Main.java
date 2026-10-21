package hai913i.tp1;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;

import hai913i.tp1.parse.JdtParser;
import hai913i.tp1.parse.JdtParser.ParsedFile;
import hai913i.tp1.parse.ProjectSources;
import hai913i.tp1.visitor.MyASTVisitor;
import hai913i.tp1.visitor.TypeCellector;
import hai913i.tp1.visitor.TypeCounter;

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


        TypeCounter counter = new TypeCounter();
        for (ParsedFile file : files) {
            file.unit().accept(counter);
        }
        System.out.println("classes      : " + counter.classes);
        System.out.println("interfaces   : " + counter.interfaces);
        System.out.println("enums        : " + counter.enums);
        System.out.println("methodes     : " + counter.methods + " (dont " + counter.constructors + " constructeurs)");
        System.out.println("attributs    : " + counter.fields);
    
        if (args.length >= 3 && args[1].equals("--ast")) {
            for (ParsedFile file : files) {
                if (file.path().getFileName().toString().equals(args[2])) {
                    System.out.println("====================================== Détails de " + file.path().getFileName() + " =======================================");
                    file.unit().accept(new TypeCellector());
                }else if(args[2].equals("all")) {
                    System.out.println("====================================== Détails de " + file.path().getFileName() + " =======================================");
                    file.unit().accept(new TypeCellector());
                }
            }
        }

       /*  if (args.length >= 3 && args[1].equals("--ast")) {
            for (ParsedFile file : files) {
                if (file.path().getFileName().toString().equals(args[2])) {
                    System.out.println("=== " + file.path().getFileName() + " ===");
                    file.unit().accept(new MyASTVisitor());
                }
            }
        }*/
    }
}
