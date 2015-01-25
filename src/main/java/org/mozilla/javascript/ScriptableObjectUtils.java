package org.mozilla.javascript;

public class ScriptableObjectUtils {

    public static String[] getMethodArrayFromJsObject(Object obj) {
        if (obj instanceof InterpretedFunction) {
            return InterpretedFunctionPropertiesLoader((InterpretedFunction) obj);
        } else  {
            Scriptable arr = (Scriptable) obj;
            Object[] ids = arr.getIds();
            String[] array = new String[ids.length];
            for (int i = 0; i < ids.length; i++) {
                array[i] = (String) ids[i];
            }
            return array;
        }
    }


    public static String[] InterpretedFunctionPropertiesLoader(InterpretedFunction obj) {
        String[] stringTable = obj.idata.itsStringTable;
        return stringTable;
    }
}
