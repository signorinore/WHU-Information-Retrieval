package utils;

import java.io.*;

public class FileUtils {
    public static long getFileSize(File f) {
        if (f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            long size = 0;
            for (File file1 : files) {
                if (file1.isFile()) {
                    size += file1.length();
                } else {
                    size += getFileSize(file1);
                }
            }
            return size;
        } else if (f.exists()) {
            long size = 0;
            size += f.length();
            return size;
        } else {
            return -1;  // 查询文件或文件夹有误
        }
    }

    public static String getProjectPath() {
        File f = new File("");
        String projectPath = "";
        try {
            projectPath = f.getCanonicalPath();
        } catch (Exception e) {
            System.out.println(e);
        }
        return projectPath;
    }

    public static String getFileContent(File file) throws IOException {
        String path = file.getPath();
        FileInputStream fis = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String fileContent = "";
        String line = null;
        while ((line = br.readLine()) != null) {
            fileContent += line;
        }
        return fileContent;
    }
}
