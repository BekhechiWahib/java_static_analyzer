package hai913i.tp1.models;

public class FieldInfo {
    public final String name, type, visibility;
    
    public FieldInfo(String name, String type, String visibility) {
        this.name = name;
        this.type = type;
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getVisibility() {
        return visibility;
    }
}
