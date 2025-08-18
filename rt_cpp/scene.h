#ifndef SCENE_H
#define SCENE_H

#include <limits>

#include "camera.h"
#include "visibleobject.h"
#include "vector3.h"
#include "ray.h"
#include "rayhit.h"


class scene {

    public:
        scene() : cam(), elements(), worldUnit(1) {}
        scene(const camera& cam, const std::vector<std::shared_ptr<visibleobject>>& elements) : cam(cam), elements(elements), worldUnit(cam.getHeight()) {}

        camera& getCam() const {return cam; };

        rayhit rayIntersection(const ray& ray) const {

            std::vector<std::shared_ptr<rayhit>> hits(elements.size());
            std::vector<double> distances(elements.size());
            double distance = std::numeric_limits<double>::max();
            bool hit = false;

            for (int i = 0; i < elements.size(); i++) {
                hits[i] = elements[i]->intersect(ray);

                if (hits[i].getDidHit()) {
                    distance = hits[i].getPoint() - ray.getOrigin().magnitude();
                    if (distance >= 1) {
                        hit = true;
                        distances[i] = distance;
                    } else {
                        distances[i] = std::numeric_limits<double>::max();
                    }
                } else {
                    distances[i] = std::numeric_limits<double>::max();
                }
            }

            if (!hit) {
                return hits[0];
            }

            double smallest = distances[0];
            int smallestIndex = 0;

            for (int i = 1; i < distances.size(); i++) {
                if (distances[i] < smallest) {
                    smallest = distances[i];
                    smallestIndex = i;
                }
            }

            return hits[smallestIndex];
        }

    private:
        camera cam;
        std::vector<std::shared_ptr<visibleobject>> elements;

}

#endif