package rt_src;


public class Ray {

    private Vector3 origin;
    private Vector3 direction;

    public Ray(Vector3 origin, Vector3 direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public Vector3 origin() { return this.origin; }
    public Vector3 direction() { return this.direction; }

    public String toString() {
        return origin.toString() + "+ t * " + direction.toString();
    }
}