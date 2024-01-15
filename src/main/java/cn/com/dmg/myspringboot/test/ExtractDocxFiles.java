package cn.com.dmg.myspringboot.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

public class ExtractDocxFiles {

    public static void main(String[] args) {
        String sourceFolderPath = "C:\\Users\\13117\\Desktop\\简历模板\\"; // 输入包含压缩文件的文件夹路径
        String destinationFolderPath = "C:\\Users\\13117\\Desktop\\简历"; // 输出文件夹路径

        File sourceFolder = new File(sourceFolderPath);
        File destinationFolder = new File(destinationFolderPath);

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        if (sourceFolder.isDirectory()) {
            File[] listOfFiles = sourceFolder.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        String fileExtension = FilenameUtils.getExtension(file.getName());
                        if ("zip".equalsIgnoreCase(fileExtension) || "rar".equalsIgnoreCase(fileExtension)) {
                            try {
                                extractDocxFromArchive(file, destinationFolder);
                            } catch (IOException | ArchiveException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private static void extractDocxFromArchive(File archiveFile, File destinationFolder)
            throws IOException, ArchiveException {
        String fileExtension = FilenameUtils.getExtension(archiveFile.getName());
        if ("zip".equalsIgnoreCase(fileExtension)) {
            extractFromZip(archiveFile, destinationFolder);
        } else if ("rar".equalsIgnoreCase(fileExtension)) {
            extractFromRar(archiveFile, destinationFolder);
        }
    }

    private static void extractFromZip(File zipFile, File destinationFolder) throws IOException {
        try (ZipFile file = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".docx")) {
                    try (InputStream input = file.getInputStream(entry)) {
                        File outputFile = new File(destinationFolder, entry.getName());
                        FileUtils.copyInputStreamToFile(input, outputFile);
                    }
                }
            }
        }
    }

    private static void extractFromRar(File rarFile, File destinationFolder) throws IOException, ArchiveException {
        try (InputStream inputStream = Files.newInputStream(rarFile.toPath());
             ArchiveInputStream archiveInputStream = new ArchiveStreamFactory()
                     .createArchiveInputStream(inputStream)) {

            ArchiveEntry entry;
            while ((entry = archiveInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".docx")) {
                    File outputFile = new File(destinationFolder, entry.getName());
                    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        IOUtils.copy(archiveInputStream, outputStream);
                    }
                }
            }
        }
    }
}
