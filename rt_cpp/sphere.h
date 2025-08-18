#ifndef SPHERE_H
#define SPHERE_H

#include "vector3.h"


class sphere : public visibleobject {

    public:
        sphere() : visibleobject(), radius(1.0) {}
        sphere(const vector3& position, const vector3& color, double roughness, double opacity, double refractiveIndex, double radius) : visibleobject(position, color, roughness, opacity, refractiveIndex), radius(radius) {}

        rayhit intersect(const ray& ray) const override {

            double D = std::pow(dot(ray.getDirection(), ray.getOrigin() - position), 2);
            if (D < 0) {
                return rayhit();
            }

            double offset = -dot(ray.getDirection(), ray.getOrigin() - position));
            double t = offset - std::sqrt(D);

            if (t < 0) {
                t = t - (2 * offset);
                if (t < 0) {
                    // TODO : implement refraction out of the body
                    return rayhit();
                }
                return rayhit();
            }

            vector3 intersect = ray.getOrigin() + (ray.getDirection() * t);
            return rayhit(*this, intersect);
        }

        vector3 normal(const vector3& point) const override {
            return (point - position).normalized();
        }

    private:
        double radius;
};

#endif