package hai913i.tp1.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import hai913i.tp1.models.MethodInvocationInfo;

public class MyMethodInvocation extends ASTVisitor {
    public List<MethodInvocationInfo> listMethods = new ArrayList<>();

    @Override
    public boolean visit(SuperMethodInvocation node) {
        CompilationUnit cu = (CompilationUnit) node.getRoot();
        int line = cu.getLineNumber(node.getStartPosition());

        IMethodBinding b = node.resolveMethodBinding();
        
        String receiverType = (b != null) ? b.getDeclaringClass().getQualifiedName() : "non résolu";
        
        String target = (b == null) ? "non résolue"
            : receiverType + "." + b.getName()
            + "(" + Arrays.stream(b.getParameterTypes())
                    .map(ITypeBinding::getName).collect(Collectors.joining(", ")) + ")";

        MethodInvocationInfo methodInfo = new MethodInvocationInfo(
            node.resolveMethodBinding().getName(),
            receiverType,
            target,
            line
        );


        System.out.println("Method Name : " + methodInfo.getMethodName()
                        + ", Static Receiver : " + methodInfo.getReceiverStaticType()
                        + ", Target Method : " + methodInfo.getTargetMethodSignature()
                        + ", In Line : " + methodInfo.getLineNumber()
                        );

        listMethods.add(methodInfo);
        return true;
    }

    @Override 
    public boolean visit(MethodInvocation node) {

        CompilationUnit cu = (CompilationUnit) node.getRoot();
        int pos = node.getStartPosition();
        int line = cu.getLineNumber(pos);
        // type statique du receveur
        Expression recv = node.getExpression();
        String receiverType;
        if (recv == null) {
            receiverType = "implicite (même classe)";
        } else {
            ITypeBinding t = recv.resolveTypeBinding();
            receiverType = (t != null) ? t.getQualifiedName() : "non résolu";
        }

        // méthode cible
        IMethodBinding b = node.resolveMethodBinding();
        String target = (b == null) ? null : methodId(b);
        
        MethodInvocationInfo methodInfo = new MethodInvocationInfo(
            node.getName().getIdentifier(),  
            receiverType,
            target,
            line
        );

        
        System.out.println("Method Name : " + methodInfo.getMethodName()
                        + ", Static Receiver : " + methodInfo.getReceiverStaticType()
                        + ", Target Method : " + methodInfo.getTargetMethodSignature()
                        + ", In Line : " + methodInfo.getLineNumber()
                        );

        listMethods.add(methodInfo);
        return true; 
    }

    public static String methodId(IMethodBinding b) {
    b = b.getMethodDeclaration();
    String params = Arrays.stream(b.getParameterTypes())
            .map(p -> p.getErasure().getQualifiedName()).collect(Collectors.joining(","));
    return b.getDeclaringClass().getErasure().getQualifiedName() + "#" + b.getName() + "(" + params + ")";
}
}
