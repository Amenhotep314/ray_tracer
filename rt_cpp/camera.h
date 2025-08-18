#ifndef CAMERA_H
#define CAMERA_H

#include <cmath>
#include <random>

#include "vector3.h"
#include "ray.h"
#include "sceneobject.h"

std::random_device cam_rd;
std::mt19937 gen(cam_rd());
std::uniform_real_distribution<> camDist(0.0, 1.0);


class camera : public sceneobject {

    public:
        camera() : sceneobject(), width(1920), height(1080), depth(0.75), focalRadius(0.01), direction(0, 1, 0), vertical(0, 0, 1) {}

        camera(const vector3& position, double width, double height, double depth, double defocusAngle, const vector3& direction, const vector3& vertical) {

            sceneobject(position);

            this->width = width;
            this->height = height;
            this->depth = depth;
            this->worldWidth = width / height;
            this->focalRadius = depth * std::tan(defocusAngle * M_PI / 360.0);

            this->direction = direction.normalized();
            this->vertical = vertical.normalized();
            this->horizontal = cross(this->direction, this->vertical).normalized();
            this->center = position + (this->direction * depth);
        }

        double getHeight() const { return height; }
        double getWidth() const { return width; }

        ray pixelPerspectiveRay(double x, double y) const {

            double jitterX = 2 * (camDist(gen) - 0.5);
            double jitterY = 2 * (camDist(gen) - 0.5);

            double worldX = ((x + jitterX) / width - 0.5) * worldWidth;
            double worldY = (0.5 - (y + jitterY) / height);

            vector3 lens = randomDiscPosition();

            vector3 pixel = center + (horizontal * worldX) + (vertical * worldY);
            vector3 netDirection = pixel - lens;
            return ray(lens, netDirection);
        }

        ray pixelOrthoRay(double x, double y) const {

            double jitterX = 2 * (camDist(gen) - 0.5);
            double jitterY = 2 * (camDist(gen) - 0.5);

            double worldX = ((x + jitterX) / width - 0.5) * worldWidth;
            double worldY = (0.5 - (y + jitterY) / height);

            vector3 pixel = center + (horizontal * worldX) + (vertical * worldY);
            return ray(pixel, direction);
        }

    private:
        double width;
        double height;
        double worldWidth;
        double depth;
        double focalRadius;
        vector3 direction;
        vector3 vertical;
        vector3 horizontal;
        vector3 center;

        vector3 randomDiscPosition() const {

            while (true) {
                double x = camDist(gen) - 0.5;
                double y = camDist(gen) - 0.5;

                if(x * x + y * y <= 1) {
                    x *= focalRadius;
                    y *=focalRadius;
                    return position + (horizontal * x) + (vertical * y)
                }
            }
        }

}

#endif