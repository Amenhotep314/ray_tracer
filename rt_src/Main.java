package rt_src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) {
        Scene sampleScene = new Scene(
            new Camera(new Vector3(0, -2, 0), 1920, 1080, 2, 2, new Vector3(0, 1, 0), new Vector3(0, 0, 1)),
            new VisibleObject[] {
                new Sphere(new Vector3(1, 0, 1.5), new Vector3(8, 8, 8), 1, 1, 1, true, 0.5),
                new Sphere(new Vector3(0, 0, 0), new Vector3(1, 0, 0), 1, 1, 1.5, false, 0.35),
                new Sphere(new Vector3(-0.35, -0.25, -0.15), new Vector3(0, 0.6, 1), 0, 1, 1.5, false, 0.15),
                new Sphere(new Vector3(0.45, 0.25, 0.15), new Vector3(0, 1, 0), 1, 1, 1.5, false, 0.2),
                new Sphere(new Vector3(0.25, -0.4, 0), new Vector3(0.8, 0.8, 0.8), 1, 0, 1.5, false, 0.1),
                new Sphere(new Vector3(0, 0, -7.5), new Vector3(0.15, 0.15, 0.15), 1, 1, 1.5, false, 7.15),
            }
        );

        // render(sampleScene, 50, 10, false, new Vector3(0.5, 0.6, 0.7), "test.png");


        // PARALLEL RENDERING EXAMPLE
        // ForkJoinPool customPool = new ForkJoinPool(7);

        // try {
        //     customPool.submit(() -> IntStream.range(0, 120).parallel().forEach(i -> {

        //         // double angle = (Math.PI * i / 180.0) - (Math.PI / 2);
        //         // Vector3 cameraPosition = new Vector3(4 * Math.cos(angle), 4 * Math.sin(angle), 0);
        //         // Vector3 cameraDirection = cameraPosition.scalarMultiply(-1).normalize();

        //         Vector3 cameraPosition = new Vector3(0, -2, 0);
        //         Vector3 cameraDirection = new Vector3(0, 1, 0);
        //         double blur = (1/12) * i;

        //         Scene frameScene = new Scene(
        //             new Camera(cameraPosition, 1920, 1080, 2, blur, cameraDirection, new Vector3(0, 0, 1)),
        //             new VisibleObject[] {
        //                 new Sphere(new Vector3(1, 0, 1.5), new Vector3(8, 8, 8), 1, 1, 1, true, 0.5),
        //                 new Sphere(new Vector3(0, 0, 0), new Vector3(1, 0, 0), 1, 1, 1.5, false, 0.35),
        //                 new Sphere(new Vector3(-0.35, -0.25, -0.15), new Vector3(0, 0.6, 1), 0, 1, 1.5, false, 0.15),
        //                 new Sphere(new Vector3(0.45, 0.25, 0.15), new Vector3(0, 1, 0), 1, 1, 1.5, false, 0.2),
        //                 new Sphere(new Vector3(0.25, -0.4, 0), new Vector3(0.8, 0.8, 0.8), 1, 0, 1.5, false, 0.1),
        //                 new Sphere(new Vector3(0, 0, -7.5), new Vector3(0.15, 0.15, 0.15), 1, 1, 1.5, false, 7.15),
        //             }
        //         );
        //         String filename = String.format("frame_%03d.png", i);
        //         render(frameScene, 10, 10, false, new Vector3(0.7, 0.8, 1), filename);
        //     })).get();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        // SYNCHRONOUS RENDERING EXAMPLE
        for (int i = 0; i < 360; i++) {

            double angle = (2 * Math.PI * i / 360.0) - (Math.PI / 2);

            Vector3 cameraPosition = new Vector3(2 * Math.cos(angle), 2 * Math.sin(angle), 0);
            Vector3 cameraDirection = cameraPosition.scalarMultiply(-1).normalize();

            Scene frameScene = new Scene(
                new Camera(cameraPosition, 1920, 1080, 2, 2, cameraDirection, new Vector3(0, 0, 1)),
                new VisibleObject[] {
                    new Sphere(new Vector3(1, 0, 1.5), new Vector3(3, 3, 3), 1, 1, 1, true, 0.5),
                    new Sphere(new Vector3(0, 0, 0), new Vector3(1, 0, 0), 1, 1, 1.5, false, 0.35),
                    new Sphere(new Vector3(-0.35, -0.25, -0.15), new Vector3(0, 0.6, 1), 0, 1, 1.5, false, 0.15),
                    new Sphere(new Vector3(0.45, 0.25, 0.15), new Vector3(0, 1, 0), 1, 1, 1.5, false, 0.2),
                    new Sphere(new Vector3(0.25, -0.4, 0), new Vector3(0.8, 0.8, 0.8), 1, 0, 1.5, false, 0.1),
                    new Sphere(new Vector3(0, 0, -7.5), new Vector3(0.15, 0.15, 0.15), 1, 1, 1.5, false, 7.15),
                }
            );
            String filename = String.format("frame_%03d.png", i);
            render(frameScene, 50, 100, false, new Vector3(0.7, 0.8, 1), filename);
        }
    }

    public static void render(Scene scene, int maxDepth, int trials, boolean ortho, Vector3 backgroundColor, String filename) {

        int width = scene.camera().width();
        int height = scene.camera().height();
        double[][][] image = new double[width][height][3];

        Vector3 color;
        Vector3[] attempts = new Vector3[trials];
        Ray ray;

        double percent = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for(int i = 0; i < trials; i++) {
                    if (ortho) {
                        ray = scene.camera().pixelOrthoRay(x, y);
                    } else {
                        ray = scene.camera().pixelPerspectiveRay(x, y);
                    }

                    attempts[i] = castRay(ray, scene, maxDepth, backgroundColor, 0);
                }
                color = Vector3.average(attempts);
                image[x][y] = new double[] {color.x()*255, color.y()*255, color.z()*255};

                percent = (x * height + y) / (width * height) * 100;
                System.out.print(String.valueOf(percent) + "\r");
            }
        }

        saveImage(image, width, height, "out.png");
    }

    public static Vector3 castRay(Ray ray, Scene scene, int maxDepth, Vector3 backgroundColor, int depth) {

        if (depth > maxDepth) {
            return backgroundColor;
        }
        RayHit hit = scene.rayIntersection(ray);

        if (!hit.didHit()) {
            return backgroundColor;
        }

        if (hit.target().isLight()) {
            return hit.target().color();
        }

        Ray newRay = hit.target().bounceRay(ray, hit.point());
        Vector3 color = castRay(newRay, scene, maxDepth, backgroundColor, depth+1);
        return hit.target().colorSeen(color, newRay);
    }

    public static void saveImage(double[][][] image, int width, int height, String filePath) {

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int r = (int) image[x][y][0];
                int g = (int) image[x][y][1];
                int b = (int) image[x][y][2];
                if (r > 255) { r = 255; }
                if (g > 255) { g = 255; }
                if (b > 255) { b = 255; }
                int rgb = (r << 16) | (g << 8) | b;
                bufferedImage.setRGB(x, y, rgb);
            }
        }

        try {
            File outputFile = new File(filePath);
            ImageIO.write(bufferedImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}