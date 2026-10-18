package hai913i.tp1.models;

public class MethodInfo {
    public final String name;
    public final int paramCount;
    public final boolean constructor;
    
    public MethodInfo(String name, int paramCount, boolean constructor) {
        this.name = name;
        this.paramCount = paramCount;
        this.constructor = constructor;
    }
}
