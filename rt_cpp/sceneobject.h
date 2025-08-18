#ifndef SCENEOBJECT_H
#define SCENEOBJECT_H

#include "vector3.h"


class sceneobject {

    public:
        sceneobject() : position(0, 0, 0) {}
        sceneobject(const vector3& position) : position(position) {}

        const vector3& getPosition() const { return position; }

    private:
        vector3 position;
};

#endif