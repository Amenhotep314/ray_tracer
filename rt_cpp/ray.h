#ifndef RAY_H
#define RAY_H

#include "vector3.h"


class ray {

    public:
        ray() {}
        ray(const vector3& origin, const vector3& direction) : origin(origin), direction(direction.normalized()) {}

        const vector3& getOrigin() const { return origin; }
        const vector3& getDirection() const { return direction; }

    private:
        vector3 origin;
        vector3 direction;
};

#endif