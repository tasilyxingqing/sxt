package com.ruoyi.isup.linux64.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ZipUtils {

    public static boolean extractZipFromJar(String zipFileInJar, String destinationDirectory) {
        ClassPathResource resource = new ClassPathResource(zipFileInJar);
        Path targetDir = Paths.get(System.getProperty("user.dir") + destinationDirectory); // 目标目录是根目录
        try (InputStream inputStream = resource.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            // 创建目标目录，如果不存在
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path filePath = targetDir.resolve(entry.getName());
                // 如果是目录，则创建目录
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    if (Files.exists(filePath)) {
                        // System.out.println("文件已经存在，跳过解压: " + filePath);
                        continue;
                    }
                    try (OutputStream outputStream = Files.newOutputStream(filePath)) {
                        byte[] buffer = new byte[2048]; // 调整缓冲区大小以提高解压速度
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
            // 解压完成，输出日志
            log.info("ZIP 文件解压完成，已解压到：" + targetDir.toString());
            return true;
        } catch (IOException e) {
            // 记录详细的错误信息
            log.info("解压失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 检查根目录是否存在某个文件夹
     *
     * @param folderName 要检查的文件夹名称
     * @return 如果文件夹存在，返回 true；否则返回 false
     */
    public static boolean isFolderExists(String folderName) {
        Path path = Paths.get(System.getProperty("user.dir"), folderName);  // 获取根目录下的文件夹路径
        return Files.exists(path) && Files.isDirectory(path);  // 检查文件夹是否存在且是目录
    }
}
