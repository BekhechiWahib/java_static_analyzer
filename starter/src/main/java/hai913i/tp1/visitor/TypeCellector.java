package hai913i.tp1.visitor;

import org.eclipse.jdt.core.dom.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;

import hai913i.tp1.models.FieldInfo;
import hai913i.tp1.models.MethodInfo;
import hai913i.tp1.models.TypeInfo;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class TypeCellector extends ASTVisitor {

    
    
    //information of the type 
    TypeInfo typeInfo = new TypeInfo();
    // information of the field and method
    FieldInfo fieldInfo = new FieldInfo("", "", "");
    MethodInfo methodInfo = new MethodInfo("", 0, false);
    List<MethodInvocation> appels = new ArrayList<>();

    @Override
    public boolean visit(PackageDeclaration node) {
       typeInfo.setPackageName(node.getName().getFullyQualifiedName());
        return true;
    }

    @Override 
    public boolean visit(TypeDeclaration node) {

        ITypeBinding binding = node.resolveBinding();
        if (binding == null) return true; 

        typeInfo.setQualifiedName(binding.getQualifiedName());

      
        // chaîne des superclasses, via les bindings
        ITypeBinding sup = binding.getSuperclass();
        while (sup != null && !sup.getQualifiedName().equals("java.lang.Object")) {
            typeInfo.setSuperclass(sup.getQualifiedName());
            sup = sup.getSuperclass();
        }

        // interfaces directes
        for (ITypeBinding itf : binding.getInterfaces()) {
            typeInfo.addInterface(itf.getQualifiedName());
        }
        System.out.println("package: " + typeInfo.getPackageName());
        System.out.println("type: " + typeInfo.getQualifiedName());
        System.out.println("superclass: " + typeInfo.getSuperclasses());
        System.out.println("interfaces: " + (typeInfo.getInterfaces().isEmpty() ? "none" : typeInfo.getInterfaces()));
        return true;
    }



    @Override 
    public boolean visit(FieldDeclaration node) {
       

        fieldInfo = new FieldInfo(node.fragments().get(0).toString(), node.getType().toString(), visibility(node.getModifiers()));
        System.out.println("field: " + fieldInfo.getName() + ", type: " + fieldInfo.getType() + ", visibility: " + fieldInfo.getVisibility());
        return true;
    }
    @Override 
    public boolean visit(MethodDeclaration node) {
        methodInfo = new MethodInfo(node.getName().getIdentifier(), node.parameters().size(), node.isConstructor());
        System.out.println("method: " + methodInfo.getName() + ", paramCount: " + methodInfo.getParamCount() + ", constructor: " + methodInfo.getConstructor());
        return true;
    }
    @Override 
    public boolean visit(MethodInvocation node) {
        appels.add(node);
        return true;
    }

   

   
    static String visibility(int mods) {
    if (Modifier.isPublic(mods))    return "public";
    if (Modifier.isProtected(mods)) return "protected";
    if (Modifier.isPrivate(mods))   return "private";
    return "package";
}
    
}
