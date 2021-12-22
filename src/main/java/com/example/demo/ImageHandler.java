package com.example.demo;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageHandler {
    public static String fileUpload(MultipartFile file, String promo) {
        InputStream inputStream;
        OutputStream outputStream;
        System.out.println(promo);
        File newFile = new File("./build/classes/java/main/com/example/demo/" + promo.toLowerCase(Locale.ROOT) + ".png");
        try {
            inputStream = file.getInputStream();
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
         } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile.getAbsolutePath();
    }
    static List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();
        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }
        return filenames;
    }
    private static InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? ImageHandler.class.getResourceAsStream(resource) : in;
    }
    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
