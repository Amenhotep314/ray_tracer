#include <string>

#include "vector3.h"
#include "scene.h"
#include "camera.h"
#include "visibleobject.h"
#include "ray.h"
#include "rayhit.h"
#include "sphere.h"


int main() {

    scene samplescene = samplescene(
        camera(vector3(0, -2, 0), 1920, 1080, 2, 2, vector3(0, 1, 0), vector3(0, 0, 1)),
        std::vector<std::shared_ptr<visibleobject>> {
            std::make_shared<sphere>(vector3(1, 0, 1.5), vector3(8, 8, 8), 1.0, 1.0, 1.0, 0.5),
            std::make_shared<sphere>(vector3(0, 0, 0), vector3(1, 0, 0), 1.0, 1.0, 1.5, 0.35),
            std::make_shared<sphere>(vector3(-0.35, -0.25, -0.15), vector3(0.0, 0.6, 1.0), 0.0, 1.0, 1.5, 0.15),
            std::make_shared<sphere>(vector3(0.45, 0.25, 0.15), vector3(0, 1, 0), 1.0, 1.0, 1.5, 0.2),
            std::make_shared<sphere>(vector3(0.25, -0.4, 0.0), vector3(0.8, 0.8, 0.8), 1.0, 0.0, 1.5, 0.1),
            std::make_shared<sphere>(vector3(0, 0, -7.5), vector3(0.15, 0.15, 0.15), 1.0, 1.0, 1.5, 7.15),

        }
    );
}


void render(const scene& samplescene, int maxDepth, int trials, bool ortho, const vector3& backgroundColor, std::string filename) {

    int width = samplescene.getCam().getWidth();
    int height = samplescene.getCam().getHeight();

    std::vector<std::vector<std::vector<double>>> image(
        width, std::vector<std::vector<double>>(height, std::vector<double>(3))
    );

    std::vector<vector3> attempts(trials);

    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {

            for (int i = 0; i < trials; i++) {
                if (ortho) {
                    ray r = samplescene.getCam().pixelOrthoRay(x, y);
                } else {
                    ray r = samplescene.getCam().pixelPerspectiveRay(x, y);
                }
                attempts[i] = castRay(r, samplescene, maxDepth, backgroundColor, 0);
            }
            vector3 color = average(attempts);
            image[x][y][0] = color.x();
            image[x][y][1] = color.y();
            image[x][y][2] = color.z();
        }
    }

    saveImage(image, width, height, filename);
}


vector3 castRay(const ray& r, const scene& samplescene, int maxDepth, const vector3& backgroundColor, int depth) {

    // We're in too deep
    if (depth > maxDepth) {
        return backgroundColor;
    }

    // Check for nearest collision
    rayhit hit = samplescene.rayIntersection(r);

    // There is no nearest collision
    if(!hit.getDidHit()) {
        return backgroundColor;
    }

    // We hit a light source
    if (hit.getTarget().isLight()) {
        return hit.getTarget().getColor();
    }

    // Otherwise we hit something and the light's path continues
    ray newRay = hit.getTarget().bounceRay(r, hit.getPoint());
    vector3 color = castRay(newRay, samplescene, maxDepth, backgroundColor, depth + 1);
    return hit.getTarget().colorSeen(color, newRay);
}


void saveImage(const std::vector<std::vector<std::vector<double>>>& image, int width, int height, const std::string& filename) {

    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            int r = static_cast<int>(image[x][y][0] * 255);
            int g = static_cast<int>(image[x][y][1] * 255);
            int b = static_cast<int>(image[x][y][2] * 255);

            if (r > 255) { r = 255; }
            if (g > 255) { g = 255; }
            if (b > 255) { b = 255; }

            int rgb = (r << 16) | (g << 8) | b;


        }
    }
}