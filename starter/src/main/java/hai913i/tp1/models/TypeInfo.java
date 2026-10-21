package hai913i.tp1.models;

import java.util.ArrayList;
import java.util.List;

public class TypeInfo {
    public String qualifiedName, kind, packageName;   
    public List<String> superclasses = new ArrayList<>();
    public List<String> interfaces = new ArrayList<>();
    public List<FieldInfo> fields = new ArrayList<>();
    public List<MethodInfo> methods = new ArrayList<>();
   
   public String getQualifiedName() {
       return qualifiedName;
   }
    public String getKind() {
         return kind;
    }
    public String getPackageName() {
        return packageName;
    }
    
public List<FieldInfo> getFields() {
    return fields;
}
public List<MethodInfo> getMethods() {
    return methods;
}
public List<String> getInterfaces() {
    return interfaces; 
}
public List<String> getSuperclasses() {
        return superclasses;
    }



    public void setKind(String kind) {
        this.kind = kind;
    }
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setSuperclass(Object superclass) {
        if (superclass != null) {
            this.superclasses.add(superclass.toString());
        }
    }
    public void addInterface(String interfaceName) {
        this.interfaces.add(interfaceName);
    }
    public void addField(FieldInfo field) {
        this.fields.add(field);
    }
    public void addMethod(MethodInfo method) {
        this.methods.add(method);
    }
}