package rt_src;

import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;


public class Camera extends SceneObject {

    private int width;
    private int height;
    private double worldWidth;
    private double worldHeight = 1.0;
    private double depth;
    private double focalRadius;
    private Vector3 direction;
    private Vector3 vertical;
    private Vector3 horizontal;
    private Vector3 center;

    public Camera(Vector3 position, int width, int height, double depth, double defocusAngle, Vector3 direction, Vector3 vertical) {

        super(position);

        this.width = width;
        this.height = height;
        this.worldWidth = (double) width / height;
        this.depth = depth;

        this.focalRadius = depth * Math.tan(Math.toRadians(defocusAngle / 2));

        this.direction = direction.normalize();
        this.vertical = vertical.normalize();
        this.horizontal = this.direction.cross(this.vertical);
        this.center = this.direction.scalarMultiply(depth).plus(position);
    }

    public int height() { return height; }
    public int width() { return width; }

    public void moveTo(double x, double y, double z) {

        this.position = new Vector3(x, y, z);
    }

    public Ray pixelPerspectiveRay(int x, int y) {

        double jitterX = 2 * (ThreadLocalRandom.current().nextDouble() - 0.5);
        double jitterY = 2 * (ThreadLocalRandom.current().nextDouble() - 0.5);

        double worldX = ((x + jitterX) / (double) width - 0.5) * worldWidth;
        double worldY = (0.5 - (y + jitterY) / (double) height) * worldHeight;

        Vector3 lens = randomDiscPosition();

        Vector3 pixel = center.plus(horizontal.scalarMultiply(worldX)).plus(vertical.scalarMultiply(worldY));
        Vector3 netDirection = pixel.minus(lens);
        return new Ray(lens, netDirection);
    }

    public Ray pixelOrthoRay(int x, int y) {

        double jitterX = 2 * (ThreadLocalRandom.current().nextDouble() - 0.5);
        double jitterY = 2 * (ThreadLocalRandom.current().nextDouble() - 0.5);

        double worldX = ((x + jitterX) / (double) width - 0.5) * worldWidth;
        double worldY = (0.5 - (y + jitterY) / (double) height) * worldHeight;

        Vector3 pixel = center.plus(horizontal.scalarMultiply(worldX)).plus(vertical.scalarMultiply(worldY));
        return new Ray(pixel, direction);
    }

    private Vector3 randomDiscPosition() {

        while (true) {
            double x = ThreadLocalRandom.current().nextDouble() - 0.5;
            double y = ThreadLocalRandom.current().nextDouble() - 0.5;
            if (x * x + y * y <= 1) {
                x = x * focalRadius;
                y = y * focalRadius;
                return position.plus(horizontal.scalarMultiply(x)).plus(vertical.scalarMultiply(y));
            }
        }
    }
}