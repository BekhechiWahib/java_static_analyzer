package hai913i.tp1.utils;

import org.eclipse.jdt.core.dom.Modifier;

public class MyVisibility {
   
    public static String visibility(int mods) {
        if (Modifier.isPublic(mods))    return "public";
        if (Modifier.isProtected(mods)) return "protected";
        if (Modifier.isPrivate(mods))   return "private";
        return "package";
    }
}
