package hai913i.tp1.models;

public class MethodInvocationInfo {
    public final String methodName;
    public final String receiverStaticType;
    public final int lineNumber;
    public final String targetMethodSignature;

    public MethodInvocationInfo(String methodName, String receiverStaticType,String targetMethodSignature, int lineNumber ) {
        this.methodName = methodName;
        this.receiverStaticType = receiverStaticType;
        this.targetMethodSignature = targetMethodSignature;
          this.lineNumber = lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }
    public String getReceiverStaticType() {
        return receiverStaticType;
    }
    public int getLineNumber() {
        return lineNumber;
    }
    public String getTargetMethodSignature() {
        return targetMethodSignature;
    }
}
