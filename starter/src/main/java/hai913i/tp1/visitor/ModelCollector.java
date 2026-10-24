package hai913i.tp1.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

import hai913i.tp1.models.FieldInfo;
import hai913i.tp1.models.MethodInfo;
import hai913i.tp1.models.TypeInfo;
import hai913i.tp1.utils.MyVisibility;

public class ModelCollector extends ASTVisitor {

    public List<TypeInfo> types = new ArrayList<>();   // le modèle : tous les types du projet
    public int totalLines = 0;  
    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.isLocalTypeDeclaration()) return false;
        addType(node.resolveBinding(), node.isInterface() ? "interface" : "class", node.bodyDeclarations());
        return true;   // true : pour atteindre les classes imbriquées
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        addType(node.resolveBinding(), "enum", node.bodyDeclarations());
        return true;
    }

    @Override
    public boolean visit(RecordDeclaration node) {
        addType(node.resolveBinding(), "record", node.bodyDeclarations());
        return true;
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        return false;   // ses méthodes ne comptent pas
    }

    private void addType(ITypeBinding binding, String kind, List<?> members) {
        if (binding == null) return;

        // 1. un NOUVEAU TypeInfo à chaque type
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.setQualifiedName(binding.getQualifiedName());
        typeInfo.setKind(kind);
        String pkg = binding.getPackage().getName();
        typeInfo.setPackageName(pkg.isEmpty() ? "(défaut)" : pkg);

        // 2. superclasses et interfaces (bindings)
        ITypeBinding sup = binding.getSuperclass();
        while (sup != null && !sup.getQualifiedName().equals("java.lang.Object")) {
            typeInfo.setSuperclass(sup.getQualifiedName());
            sup = sup.getSuperclass();
        }
        for (ITypeBinding itf : binding.getInterfaces()) {
            typeInfo.addInterface(itf.getQualifiedName());
        }

        // 3. attributs et méthodes : membres DIRECTS du type
        for (Object o : members) {
            if (o instanceof FieldDeclaration field) {
                for (Object frag : field.fragments()) {
                    String name = ((VariableDeclarationFragment) frag).getName().getIdentifier();
                    typeInfo.addField(new FieldInfo(name, field.getType().toString(),
                            MyVisibility.visibility(field.getModifiers())));
                }
            } else if (o instanceof MethodDeclaration method) {
                typeInfo.addMethod(buildMethod(method));
            }
        }

        // 4. on range le type dans la liste
        types.add(typeInfo);
    }

    private MethodInfo buildMethod(MethodDeclaration node) {
        MethodInfo methodInfo = new MethodInfo(node.getName().getIdentifier(),
                node.parameters().size(), node.isConstructor(), 0);

        IMethodBinding mb = node.resolveBinding();
        if (mb != null) methodInfo.id = MyMethodInvocation.methodId(mb);

        Block body = node.getBody();
        if (body != null) {
            // lignes du corps : de { à } inclus
            CompilationUnit cu = (CompilationUnit) node.getRoot();
            int start = cu.getLineNumber(body.getStartPosition());
            int end = cu.getLineNumber(body.getStartPosition() + body.getLength() - 1);
            methodInfo.lineCount = end - start + 1;

            // appels : ton visiteur MyMethodInvocation, lancé sur le corps de CETTE méthode
            MyMethodInvocation calls = new MyMethodInvocation();
            body.accept(calls);
            methodInfo.calls.addAll(calls.listMethods);
        }
        return methodInfo;
    }
}