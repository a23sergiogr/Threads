package Ex;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ex5ThreadPoolDownloads {
    public static final String urls = "src\\main\\resources\\ex5img\\imgURLs.txt";
    public static final String folder = "src\\main\\resources\\ex5img\\results";

    public static void main(String[] args) {
        Path folderPath = Paths.get(folder);

        try {
            Files.createDirectory(folderPath);
        } catch (IOException e) {
            System.err.println("Problema creando Carpeta");
        }

        Path urlPath = Paths.get(urls);
        try(Scanner scanner = new Scanner(new FileInputStream(urlPath.toFile()))){
            int i = 0;
            while (scanner.hasNextLine()){
                try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
                    String url = scanner.nextLine();
                    String imgPath= folder + "\\animal" + String.valueOf(i) + ".jpg";
                    Runnable taskRunnable=new DownloadThread(url, imgPath);
                    pool.execute(taskRunnable);
                }
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

class DownloadThread implements Runnable {
    String urlStr;
    String result;

    public DownloadThread(String urlStr, String result) {
        this.urlStr = urlStr;
        this.result = result;
    }

    @Override
    public void run() {
        URI uri;
        try {
            uri = new URI(urlStr);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try (InputStream in = new BufferedInputStream(uri.toURL().openStream());
             FileOutputStream out = new FileOutputStream(result)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }

            System.out.println("Image downloaded successfully: " + result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

