package hai913i.tp1.visitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MyASTVisitor extends ASTVisitor {
     private int depth = 0;


     // Cette méthode est appelée avant de visiter chaque nœud de l'AST
    @Override
    public boolean preVisit2(ASTNode node) {
        System.out.println("  ".repeat(depth) + node.getClass().getSimpleName());
        depth++;
        return true; 
    }

    @Override
    public void postVisit(ASTNode node) {
        depth--; // appelée en sortie de chaque nœud
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        return true;   // on n'entre pas dans les méthodes
    }
}
