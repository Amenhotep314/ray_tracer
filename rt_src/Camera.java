package rt_src;

import java.lang.Math;


public class Camera extends SceneObject {

    private int width;
    private int height;
    private double depth;
    private Vector3 direction;
    private Vector3 vertical;
    private Vector3 horizontal;
    private Vector3 center;

    public Camera(Vector3 position, int width, int height, double fov, Vector3 direction, Vector3 vertical) {

        super(position);

        this.width = width;
        this.height = height;
        this.depth = height / (2 * Math.tan(Math.toRadians(0.5 * fov)));

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

        double x_space = x - (width / 2.0) + 2 * (Math.random() - 0.5);
        double y_space = (height / 2.0) - y + 2 * (Math.random() - 0.5);
        Vector3 pixel = center.plus(horizontal.scalarMultiply(x_space).plus(vertical.scalarMultiply(y_space)));
        Vector3 netDirection = pixel.minus(position);
        return new Ray(position, netDirection);
    }

    public Ray pixelOrthoRay(int x, int y) {

        double x_space = x - (this.width / 2.0) + 2 * (Math.random() - 0.5);
        double y_space = (this.height / 2.0) - y + 2 * (Math.random() - 0.5);
        Vector3 pixel = center.plus(horizontal.scalarMultiply(x_space).plus(vertical.scalarMultiply(y_space)));
        return new Ray(pixel, direction);
    }
}