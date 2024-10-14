package rt_src;


public abstract class SceneObject {

    protected Vector3 position;

    public SceneObject(Vector3 position) {
        this.position = position;
    }

    public void changeToPixelCoordinates(double worldUnit) {
        this.position = this.position.scalarMultiply(worldUnit);
    }

    public Vector3 position() {
        return position;
    }
}