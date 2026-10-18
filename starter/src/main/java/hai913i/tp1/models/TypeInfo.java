package hai913i.tp1.models;

import java.util.ArrayList;
import java.util.List;

public class TypeInfo {
    public String qualifiedName, kind, packageName;   
    public List<String> superclasses = new ArrayList<>();
    public List<String> interfaces = new ArrayList<>();
    public List<FieldInfo> fields = new ArrayList<>();
    public List<MethodInfo> methods = new ArrayList<>();
}