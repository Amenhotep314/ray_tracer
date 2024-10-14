package rt_src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Scene sampleScene = new Scene(
            new Camera(new Vector3(0, 0, 0), 1920, 1080, 68, new Vector3(0, 1, 0), new Vector3(0, 0, 1)),
            new VisibleObject[] {
                new Sphere(new Vector3(0, 2, 3), new Vector3(3, 3, 3), 1, 1, 1, true, 1),
                new Sphere(new Vector3(0, 2, 0), new Vector3(1, 0, 0), 1, 1, 1.5, false, 0.7),
                new Sphere(new Vector3(-0.7, 1.5, -0.3), new Vector3(0, 0.6, 1), 0, 1, 1.5, false, 0.3),
                new Sphere(new Vector3(0.9, 2.5, 0.3), new Vector3(0, 1, 0), 1, 1, 1.5, false, 0.4),
                new Sphere(new Vector3(0.5, 1.2, 0), new Vector3(0.8, 0.8, 0.8), 1, 0, 1.5, false, 0.2),
                new Sphere(new Vector3(0, 2, -15), new Vector3(0.25, 0.25, 0.25), 1, 1, 1.5, false, 14.3),
                // new Plane(new Vector3(0, 0, -2), new Vector3(0.25, 0.25, 0.25), 1, 1, 1.5, false, new Vector3(0, 0, 1))
            }
        );

        render(sampleScene, 10, 100, false, new Vector3(0.7, 0.8, 1));
    }

    public static void render(Scene scene, int maxDepth, int trials, boolean ortho, Vector3 backgroundColor) {

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