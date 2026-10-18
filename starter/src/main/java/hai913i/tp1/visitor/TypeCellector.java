package hai913i.tp1.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class TypeCellector extends ASTVisitor {
    
    
    List<MethodInvocation> appels = new ArrayList<>();

    @Override 
    public boolean visit(TypeDeclaration node) {
        System.out.println("Visiting type: " + node.getName().getIdentifier());
        return true;
    }
    @Override 
    public boolean visit(MethodDeclaration node) {
        System.out.println("Visiting method: " + node.getName().getIdentifier());
        // Implementation for visiting nodes and collecting type information
        return true;
    }
    @Override 
    public boolean visit(MethodInvocation node) {
        appels.add(node);
        return true;
    }
    
}
