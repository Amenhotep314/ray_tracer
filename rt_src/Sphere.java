package rt_src;

public class Sphere extends VisibleObject {

    private double radius;

    public Sphere(Vector3 position, Vector3 color, double roughness, double opacity, double refractiveIndex, boolean isLight, double radius) {

        super(position, color, roughness, opacity, refractiveIndex, isLight);
        this.radius = radius;
    }

    public RayHit intersect(Ray ray) {

        double D = Math.pow(ray.direction().dot(ray.origin().minus(position)), 2) - (Math.pow(ray.origin().minus(position).magnitude(), 2) - (radius * radius));
        if (D < 0) {
            return new RayHit(this);
        }
        double t = -ray.direction().dot(ray.origin().minus(position)) - Math.sqrt(D);
        if (t < 0) {
            return new RayHit(this);
        }
        Vector3 intersect = ray.origin().plus(ray.direction().scalarMultiply(t));
        return new RayHit(this, intersect);
    }

    public Vector3 normal(Vector3 point) {
        return point.minus(position).scalarMultiply(1.0/point.minus(position).magnitude());
    }

    public void changeToPixelCoordinates(double worldUnit) {
        position = position.scalarMultiply(worldUnit);
        radius = radius * worldUnit;
    }
}
