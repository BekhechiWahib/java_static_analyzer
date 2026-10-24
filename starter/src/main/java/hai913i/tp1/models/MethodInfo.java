package hai913i.tp1.models;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
    public String id;                       
    public final String name;
    public final int paramCount;
    public final boolean constructor;
    public int lineCount;
    public List<MethodInvocationInfo> calls = new ArrayList<>();

    public MethodInfo(String name, int paramCount, boolean constructor, int lineCount) {
        this.name = name;
        this.paramCount = paramCount;
        this.constructor = constructor;
        this.lineCount = lineCount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getParamCount() { return paramCount; }
    public boolean getConstructor() { return constructor; }
    public int getLineCount() { return lineCount; }
    public List<MethodInvocationInfo> getCalls() { return calls; }
}