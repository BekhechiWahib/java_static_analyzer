package hai913i.tp1.visitor;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.RecordDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeCounter extends ASTVisitor {
    
    public int classes = 0, interfaces = 0, enums = 0, records = 0;
    public int fields = 0, methods = 0, constructors = 0;


   @Override
    public boolean visit(TypeDeclaration node) {
        if (node.isLocalTypeDeclaration()) return true;   // classe locale : non comptée
        if (node.isInterface()) interfaces++; else classes++;
        countMembers(node.bodyDeclarations());
        return true;                                      // true : atteindre les classes imbriquées
    }

     @Override
    public boolean visit(EnumDeclaration node) {
        enums++;
        countMembers(node.bodyDeclarations());
        return true;
    }

    @Override
    public boolean visit(RecordDeclaration node) {
        records++;
        countMembers(node.bodyDeclarations());
        return true;
    }
  

   @Override
    public boolean visit(AnonymousClassDeclaration node) {
        return false;                                    
    }

    
    private void countMembers(List<?> members) {
        for (Object o : members) {
            if (o instanceof FieldDeclaration f) {
                fields += f.fragments().size();           // int a, b; = 2 attributs
            } else if (o instanceof MethodDeclaration m) {
                methods++;
                if (m.isConstructor()) constructors++;
            }
        }
    }
}
