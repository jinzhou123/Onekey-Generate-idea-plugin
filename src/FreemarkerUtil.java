import com.intellij.openapi.ui.MessageType;
import freemarker.template.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class FreemarkerUtil
        extends OneKeyAction {
    private static String workPath;

    public static void fprint(String outFile, Map<String, Object> root, Template tp) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            tp.process(root, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public Template getTemplate(String name) {
        try {
            Configuration cfg = new Configuration();

            cfg.setObjectWrapper(new DefaultObjectWrapper());

            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
            try {
                cfg.setClassForTemplateLoading(getClass(), "/ftl");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.make(project, MessageType.ERROR, e.toString());
            }
            return cfg.getTemplate(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
