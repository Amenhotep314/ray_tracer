package rt_src;

public class Scene {

    private Camera camera;
    private VisibleObject[] elements;
    private double worldUnit;

    public Scene(Camera camera, VisibleObject[] elements) {

        this.worldUnit = camera.height();
        this.camera = camera;
        this.elements = elements;

        this.camera.changeToPixelCoordinates(worldUnit);
        for (int i = 0; i < elements.length; i++) {
            this.elements[i].changeToPixelCoordinates(worldUnit);
        }
    }

    public Camera camera() { return camera; }

    public RayHit rayIntersection(Ray ray) {

        RayHit[] hits = new RayHit[elements.length];
        double[] distances = new double[elements.length];
        double distance;
        boolean hit = false;

        for (int i = 0; i < elements.length; i++) {
            hits[i] = elements[i].intersect(ray);

            if (hits[i].didHit()) {
                distance = hits[i].point().minus(ray.origin()).magnitude();
                if (distance >= 1) {
                    hit = true;
                    distances[i] = distance;
                } else {
                    distances[i] = Double.MAX_VALUE;
                }
            } else {
                distances[i] = Double.MAX_VALUE;
            }
        }

        if(!hit) {
            return hits[0];
        }

        double smallest = distances[0];
        int smallestIndex = 0;

        for (int i = 1; i < distances.length; i++) {
            if (distances[i] < smallest) {
                smallest = distances[i];
                smallestIndex = i;
            }
        }

        return hits[smallestIndex];
    }


}
