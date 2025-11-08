package com.xingzhi.shortvideosharingplatform.utils;

import java.io.File;

public class FileUtils {
    public static void deleteFolder(File folder) {
        if (folder == null || !folder.exists()) {
            return;
        }

        // 删除文件夹中的所有文件
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file); // 递归删除子文件夹
                } else {
                    file.delete(); // 删除文件
                }
            }
        }

        // 删除空文件夹
        folder.delete();
    }
}
