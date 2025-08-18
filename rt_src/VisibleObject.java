package rt_src;

import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;


public abstract class VisibleObject extends SceneObject {

    protected Vector3 color;
    protected double roughness;
    protected double opacity;
    protected double refractiveIndex;
    protected boolean isLight;

    public VisibleObject(Vector3 position, Vector3 color, double roughness, double opacity, double refractiveIndex, boolean isLight) {

        super(position);

        this.color = color;
        this.roughness = roughness;
        this.opacity = opacity;
        this.refractiveIndex = refractiveIndex;
        this.isLight = isLight;
    }

    public Vector3 color() { return color; }
    public double roughness() { return roughness; }
    public double opacity() { return opacity; }
    public double refractiveIndex() { return refractiveIndex; }
    public boolean isLight() { return isLight; }

    public RayHit intersect(Ray ray) {

        Vector3 target = position.minus(ray.origin());
        if (target.cross(ray.direction()).magnitude() == 0 && target.dot(ray.direction()) > 0) {
            return new RayHit(this, position);
        }

        return new RayHit(this);
    }

    public Ray bounceRay(Ray ray, Vector3 point) {

        Vector3 normal = this.normal(point);

        // The ray passes into the object
        double opaqueProbability = ThreadLocalRandom.current().nextDouble();
        if (opaqueProbability > opacity) {
            double incomingAngle = Math.acos(ray.direction().dot(normal));
            double outgoingAngle = Math.asin(Math.sin(incomingAngle) / refractiveIndex);
            double length = (1.0/refractiveIndex) * Math.cos(incomingAngle) + Math.sqrt(1 - (Math.pow(Math.sin(outgoingAngle), 2)));
            Vector3 newDirection = ray.direction().scalarMultiply(1.0/refractiveIndex).minus(normal.scalarMultiply(length));

            return new Ray(point, newDirection);
        }

        // The ray is reflected
        double specularProbability = ThreadLocalRandom.current().nextDouble();
        if (specularProbability > roughness) {
            Vector3 newDirection = ray.direction().minus(normal.scalarMultiply(2*ray.direction().dot(normal)));
            return new Ray(point, newDirection);
        }

        // The ray is scattered
        Vector3 newDirection = Vector3.randomDirection();
        if (normal.dot(newDirection) < 0) {
            newDirection = newDirection.scalarMultiply(-1);
        }

        return new Ray(point, newDirection);
    }

    public Vector3 colorSeen(Vector3 outgoingColor, Ray outgoingRay) {

        return color.elementProduct(outgoingColor);
    }

    public abstract Vector3 normal(Vector3 normal);
}
