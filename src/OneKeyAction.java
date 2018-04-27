import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.io.File;
import java.util.Objects;

public class OneKeyAction
        extends AnAction {
    public static Project project;
    public static String methodName;
    public static String first;
    public static String rest;
    public static String temp;
    public static String MethodName;
    public static String ServiceName;
    public static String serviceName;
    public static String projectPath;
    public static String testBaseFolder;
    public static String scriptsFolder;
    public static String csvFolder;
    public static int num;
    public static String[] ParameterNames = null;
    public static String[] ParameterTypes = null;
    public static String ReturnTypeName;
    public static boolean findlocation = false;
    public static PsiElement psiElement;

    public void update(AnActionEvent event) {
        psiElement = (PsiElement) event.getData(PlatformDataKeys.PSI_ELEMENT);
        if ((psiElement != null) && ((psiElement instanceof PsiMethod))) {
            event.getPresentation().setEnabledAndVisible(true);
        } else {
            event.getPresentation().setEnabledAndVisible(false);
        }
    }

    public void actionPerformed(AnActionEvent event) {
        project = (Project) event.getData(PlatformDataKeys.PROJECT);
        if (psiElement == null) {
            Toast.make(project, MessageType.ERROR, "所选的元素不是方法,请选择一个方法");
            return;
        }
        if ((psiElement instanceof PsiMethod)) {
            try {
                PsiMethod psiMethod = (PsiMethod) psiElement;
                getMethodInfo(psiMethod);
                opendialog();
                if (findlocation) {
                    mkDir();
                    generaterScriptsAndCsv();
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.make(project, MessageType.INFO, e.toString());
            }
        } else {
            Toast.make(project, MessageType.ERROR, "所选的元素不是方法,请选择一个方法");
            return;
        }
    }

    public void getMethodInfo(PsiMethod psiMethod) {
        methodName = psiMethod.getName();
        temp = methodName + "Test";
        first = temp.substring(0, 1).toUpperCase();
        rest = temp.substring(1, temp.length());
        MethodName = first + rest;

        PsiClass parent = (PsiClass) psiMethod.getParent();
        ServiceName = parent.getName();
        first = ServiceName.substring(0, 1).toUpperCase();
        rest = ServiceName.substring(1, ServiceName.length());
        if (first.equals("I")) {
            serviceName = rest.substring(0, 1).toLowerCase() + rest.substring(1, rest.length());
        } else {
            serviceName = first.toLowerCase() + rest;
        }
        num = psiMethod.getParameterList().getParametersCount();
        try {
            PsiParameter[] psiParameter = psiMethod.getParameterList().getParameters();
            String[] str_name = new String[num];
            String[] str_type = new String[num];
            for (int i = 0; i < psiParameter.length; i++) {
                str_type[i] = psiParameter[i].getTypeElement().getText();
                str_name[i] = psiParameter[i].getName().toString();
            }
            ParameterTypes = str_type;
            ParameterNames = str_name;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.make(project, MessageType.ERROR, "发生未知错误");
            return;
        }
        ReturnTypeName = psiMethod.getReturnTypeElement().getText();
    }

    public void opendialog() {
        String title = "请选择你的测试工程目录";
        String description = "请选择对应的测试工程,按ok确认";
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, false, false, false, false);

        descriptor.setTitle(title);
        descriptor.setDescription(description);

        VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
        if (!Objects.isNull(file)) {
            projectPath = file.getPath();
            findlocation = true;
        }
    }

    public void mkDir()
            throws Exception {
        testBaseFolder = projectPath + "/src/test/java/com/dfire/testBase";
        scriptsFolder = projectPath + "/src/test/java/com/dfire/test/" + ServiceName;
        csvFolder = projectPath + "/src/test/resources/testcase/" + ServiceName;

        File b = new File(testBaseFolder);
        File f = new File(scriptsFolder);
        File c = new File(csvFolder);
        if (!b.exists()) {
            b.mkdirs();
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        if (!c.exists()) {
            c.mkdirs();
        }
    }

    public void generaterScriptsAndCsv()
            throws Exception {
        File testbase = new File(testBaseFolder + "/TestBase.java");
        File testscript = new File(scriptsFolder + "/" + MethodName + ".java");
        File csv = new File(csvFolder + "/" + MethodName + ".csv");

        String src = null;
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            src = "/src";
        } else {
            src = "\\src";
        }
        if ((testbase.exists()) && (testscript.exists()) && (csv.exists())) {
            Toast.make(project, MessageType.ERROR, "测试脚本" + ServiceName + "." + MethodName + "已存在,请不要重复生成");
            return;
        }
        FreemarkerUtil fmu = new FreemarkerUtil();
        if (!testbase.exists()) {
            testbase.createNewFile();
            GetContent.writeToJavaFile(testbase.getAbsolutePath(), fmu.getTemplate("TestBaseTemp.ftl"));
        }
        if (!csv.exists()) {
            csv.createNewFile();
            GetContent.writeToCsvFile(csv.getAbsolutePath(), fmu.getTemplate("CsvTemp.ftl"));
        }
        if (!testscript.exists()) {
            testscript.createNewFile();
            GetContent.writeToJavaFile(testscript.getAbsolutePath(), fmu.getTemplate("ScriptTemp.ftl"));
        }
        Toast.make(project, MessageType.INFO, "测试脚本生成完毕,请尽情享用!");
    }
}
