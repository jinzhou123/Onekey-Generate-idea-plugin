import com.intellij.openapi.ui.MessageType;
import freemarker.template.Template;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetContent
        extends OneKeyAction {
    public static void writeToCsvFile(String fileName, Template tp) {
        if (tp == null) {
            Toast.make(project, MessageType.ERROR, "找不到模板,请检查路径");
            return;
        }
        Map<String, Object> root = new HashMap();

        root.put("ParameterNames", ParameterNames);
        Map<String, String> paraMap = new LinkedHashMap();
        try {
            for (int i = 0; i < ParameterTypes.length; i++) {
                paraMap.put(ParameterNames[i], ParameterTypes[i]);
            }
        } catch (IllegalArgumentException e) {
            Toast.make(project, MessageType.ERROR, "莫名其妙的问题" + e.toString());
        }
        root.put("paraMap", paraMap);
        FreemarkerUtil.fprint(fileName, root, tp);
    }

    public static void writeToJavaFile(String fileName, Template tp) {
        if (tp == null) {
            Toast.make(project, MessageType.ERROR, "找不到模板,请检查路径");
            return;
        }
        Map<String, Object> root = new HashMap();
        if (fileName.contains("TestBase.java")) {
            FreemarkerUtil.fprint(fileName, root, tp);
            return;
        }
        root.put("MethodName", MethodName);
        root.put("methodName", methodName);
        root.put("ServiceName", ServiceName);
        root.put("serviceName", serviceName);
        root.put("ReturnTypeName", ReturnTypeName);

        root.put("ParameterNames", ParameterNames);

        Map<String, String> paraMap = new LinkedHashMap();
        try {
            for (int i = 0; i < ParameterTypes.length; i++) {
                paraMap.put(ParameterNames[i], ParameterTypes[i]);
            }
        } catch (IllegalArgumentException e) {
            Toast.make(project, MessageType.ERROR, "莫名其妙的问题" + e.toString());
        }
        root.put("paraMap", paraMap);
        FreemarkerUtil.fprint(fileName, root, tp);
    }
}
