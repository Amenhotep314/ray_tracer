#ifndef VISIBLEOBJECT_H
#define VISIBLEOBJECT_H

#include <random>

#include "vector3.h"
#include "ray.h"
#include "rayhit.h"
#include "sceneobject.h"

std::random_device rd;
std::mt19937 gen(rd());
std::uniform_real_distribution<> dist(0.0, 1.0);


class visibleobject : public sceneobject {

    public:
        visibleobject() : sceneobject(), color(1, 1, 1), roughness(0), opacity(0), refractiveIndex(1) {}
        visibleobject(const vector3& position, const vector3& color, double roughness, double opacity, double refractiveIndex) : sceneobject(position), color(color), roughness(roughness), opacity(opacity), refractiveIndex(refractiveIndex) {}

        const vector3& getColor() const { return color; }
        double getRoughness() const { return roughness; }
        double getOpacity() const { return opacity; }
        double getRefractiveIndex() const { return refractiveIndex; }

        virtual rayhit intersect(const ray& ray) const = 0;
        virtual vector3 normal(const vector3& point) const = 0;

        ray bounceRay(const ray& ray, const vector3& point) {

            vector3 normal = this->normal(point);

            // The ray passes into the object
            double opaqueProbability = dist(gen);

            if (opaqueProbability > opacity) {
                double incomingAngle = std::acos(dot(ray.getDirection(), normal));
                double outgoingAngle = std::asin(std::sin(incomingAngle) / refractiveIndex);
                double length = (1.0 / refractiveIndex) * std::cos(incomingAngle) + std::sqrt(1.0 - std::pow(std::sin(outgoingAngle), 2));
                vector3 newDirection = (ray.getDirection() / refractiveIndex) - (normal * length);

                return ray(point, newDirection);
            }

            // The ray is reflected
            double specularProbability = dist(gen);
            if (specularProbability > roughness) {
                vector3 newDirection = ray.getDirection() - (normal * (2 * dot(ray.getDirection(), normal)));
                return ray(point, newDirection);
            }

            // The ray is scattered
            vector3 newDirection = randomDirection();
            if (dot(normal, newDirection) < 0) {
                newDirection = -newDirection;
            }

            return ray(point, newDirection);
        }

        vector3 colorSeen(const vector3& outgoingColor, const ray& outgoingRay) {

            return color * outgoingColor;
        }

        bool isLight() const {
            return (color.x() > 1 || color.y() > 1 || color.z() > 1);
        }

    private:
        vector3 color;
        double roughness;
        double opacity;
        double refractiveIndex;
};

#endif