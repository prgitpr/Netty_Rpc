package rpc.protocol;

import java.io.Serializable;

public class InvokerProtocol implements Serializable {
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private String className;
    private String methodName;

    public void setParams(Class<?>[] params) {
        this.params = params;
    }

    public Class<?>[] getParams() {
        return params;
    }

    private Class<?>[] params;
    private Object[] values;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }



    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}
