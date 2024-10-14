package rt_src;

public class RayHit {

    private boolean didHit;
    private VisibleObject target;
    private Vector3 point;

    public RayHit(VisibleObject target, Vector3 point) {

        this.didHit = true;
        this.target = target;
        this.point = point;
    }

    public RayHit(VisibleObject target) {

        this.didHit = false;
        this.target = target;
    }

    public boolean didHit() { return didHit; }
    public VisibleObject target() { return target; }

    public Vector3 point() {
        if (didHit) {
            return point;
        } else {
            return new Vector3(0, 0, 0);
        }
    }
}
