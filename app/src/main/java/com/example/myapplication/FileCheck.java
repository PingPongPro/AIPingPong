package com.example.myapplication;

import java.io.File;

/**
 * Created by SKY on 2017/8/18.
 */

public class FileCheck {
    public boolean isValidFileName(String fileName){
        if (fileName == null || fileName.length() > 255)
            return false;
        else
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
    }
    public void renameFileName(String oldPath, String newPath){
        File file = new File(oldPath);
        file.renameTo(new File(newPath));
    }
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        return dirFile.delete();
    }
    public boolean moveFile(String fileName, String finDirName) {

        File file = new File(fileName);
        if(!file.exists() || !file.isFile())
            return false;
        File dir = new File(finDirName);
        if (!dir.exists())
            dir.mkdirs();
        return file.renameTo(new File(finDirName + File.separator + file.getName()));
    }
}
