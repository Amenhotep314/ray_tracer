package rt_src;

public class Plane extends VisibleObject {

    private Vector3 normalVector;

    public Plane(Vector3 position, Vector3 color, double roughness, double opacity, double refractiveIndex, boolean isLight, Vector3 normalVector) {

        super(position, color, roughness, opacity, refractiveIndex, isLight);
        this.normalVector = normalVector.normalize();
    }

    public RayHit intersect(Ray ray) {

        if (ray.direction().dot(normalVector) == 0) {
            return new RayHit(this);
        }
        double t = position.minus(ray.origin()).dot(normalVector) / ray.direction().dot(normalVector);
        if (t < 0) {
            return new RayHit(this);
        }

        Vector3 point = ray.origin().plus(ray.direction().scalarMultiply(t));
        return new RayHit(this, point);
    }

    public Vector3 normal(Vector3 point) {
        return normalVector;
    }

    public Vector3 colorSeen(Vector3 outgoingColor, Ray outgoingRay) {

        Vector3 ans = color.elementProduct(outgoingColor);
        int x = (int) (outgoingRay.origin().x() / 1000.0);
        int y = (int) (outgoingRay.origin().y() / 1000.0);

        if ((x + y) % 2 == 0) {
            ans = ans.scalarMultiply(2);
        }

        return ans;
    }
}
