package Ex;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class EX4ThreadPoolImageProcessing {
    public static final String originalFolder = "src\\main\\resources\\img\\originals";
    public static final String grayscaleFolder = "src\\main\\resources\\img\\results";

    public static void main(String[] args) {

        Path originalFolderPath = Paths.get(originalFolder);
        Path grayscaleFolderPath = Paths.get(grayscaleFolder);

        try {
            Files.createDirectory(grayscaleFolderPath);
        } catch (IOException e) {
            System.err.println("Problema creando Carpeta");
        }

        try(ExecutorService pool = Executors.newFixedThreadPool(5)) {
            try(Stream<Path> files = Files.list(originalFolderPath)){
                files.forEach( p -> {
                    Runnable taskRunnable=new Grayscale(String.valueOf(p), String.valueOf(p).replaceAll("originals","results"));
                    pool.execute(taskRunnable);
                });
            }
        } catch (IOException e) {
            System.err.println("Error con pool");
        }
    }
}

class Grayscale implements Runnable {
    String fileOriginalPath;
    String fileGrayscalePath;
    public Grayscale(String fileOriginalPath, String fileGrayscalePath){
        this.fileOriginalPath = fileOriginalPath;
        this.fileGrayscalePath = fileGrayscalePath;
    }
    @Override
    public void run() {
        BufferedImage img = null;
        File f = null;

        // read image
        try {
            f = new File(fileOriginalPath);
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }

        // get image's width and height
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);
        // convert to grayscale
        for (int i = 0; i < pixels.length; i++) {

            // Here i denotes the index of array of pixels
            // for modifying the pixel value.
            int p = pixels[i];

            int a = (p >> 24) & 0xff;
            int r = (p >> 16) & 0xff;
            int g = (p >> 8) & 0xff;
            int b = p & 0xff;

            // calculate average
            int avg = (r + g + b) / 3;

            // replace RGB value with avg
            p = (a << 24) | (avg << 16) | (avg << 8) | avg;

            pixels[i] = p;
        }
        img.setRGB(0, 0, width, height, pixels, 0, width);
        // write image
        try {
            f = new File(fileGrayscalePath);
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            System.err.println("Error escribiendo imagen");
        }
    }
}
