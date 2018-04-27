import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.List;

public class QuickSwitch
        extends AnAction {
    public static Project project;
    public static VirtualFile file;
    public static String select_path;

    public static String getTestFilePath(String name)
            throws Exception {
        String findPath = null;
        if (select_path.contains(".java")) {
            findPath = project.getBasePath() + "/src/test/resources";
        } else if (select_path.contains(".csv")) {
            findPath = project.getBasePath() + "/src/test/java";
        }
        String path = getFilePathInResource(findPath, name);

        File file = new File(path);
        if ((!file.exists()) || (file.length() < 1L)) {
            return "";
        }
        return path;
    }

    /**
     * 获取 resources 资源文件夹下特定的文件路径, 如果存在同名文件那么返回首次匹配的文件路径<br/>
     */
    public static String getFilePathInResource(String basePath, String targetName) throws Exception {
        File file = new File(basePath);
        String[] names = file.list();
        if (names != null && names.length > 0) {
            for (String element : names) {
                if (element.contains(".") && element.contains(targetName)) {
                    return basePath + "/" + element;
                }
            }
        } else {
            return "";
        }

        File[] files = file.listFiles();
        for (File element : files) {
            if (element.isDirectory()) {
                /**
                 * 忽略 com 文件夹<br/>
                 */
//                if(element.getName().equalsIgnoreCase("com"))
//                    continue;
                String path = getFilePathInResource(element.getAbsolutePath(), targetName);
                if (null != path && !path.isEmpty())
                    return path;
            }
        }

        return "";
    }

    public static void openEditor() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        String method_name = select_path.substring(select_path.lastIndexOf("/") + 1, select_path.lastIndexOf("."));
        String file_path = null;
        try {
            file_path = getTestFilePath(method_name);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(file_path);
        if (file.exists()) {
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file, 0);
            List localList = fileEditorManager.openEditor(descriptor, true);
        }
    }

    public void actionPerformed(AnActionEvent e) {

        file = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (file == null) {
            return;
        }
        project = e.getData(PlatformDataKeys.PROJECT);

        select_path = file.getPath();
        if ((select_path.contains("Test.java")) || (select_path.contains("Test.csv"))) {
            openEditor();
        }
    }
}
