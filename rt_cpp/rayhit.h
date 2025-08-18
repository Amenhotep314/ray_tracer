#ifndef RAYHIT_H
#define RAYHIT_H

#include "ray.h"
#include "visibleobject.h"


class rayhit {

    public:
        rayhit() : didhit(false), target(), point() {}
        rayhit(const visibleobject& target, const vector3& point) : didhit(true), target(target), point(point) {}


        bool getDidHit() const { return didhit; }
        const visibleobject& getTarget() const { return target; }
        const vector3& getPoint() const { return point; }

    private:
        bool didhit;
        visibleobject target;
        vector3 point;
};

#endif