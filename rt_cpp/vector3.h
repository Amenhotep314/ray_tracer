#ifndef VECTOR3_H
#define VECTOR3_H

#include <random>
#include <ostream>


std::random_device vec_rd;
std::mt19937 gen(vec_rd());
std::normal_distribution<> vecDist(0.0, 1.0);


class vector3 {

    public:
        double r[3];

        vector3() : r{0, 0, 0} {}
        vector3(double x, double y, double z) : r{x, y, z} {}

        double x() const { return r[0]; }
        double y() const { return r[1]; }
        double z() const { return r[2]; }

        vector3& operator+=(const vector3& v) {
            r[0] += v.r[0];
            r[1] += v.r[1];
            r[2] += v.r[2];
            return *this;
        }

        vector3& operator-=(const vector3& v) {
            r[0] -= v.r[0];
            r[1] -= v.r[1];
            r[2] -= v.r[2];
            return *this;
        }

        vector3& operator*=(const vector3& v) {
            r[0] *= v.r[0];
            r[1] *= v.r[1];
            r[2] *= v.r[2];
            return *this;
        }

        vector3& operator*=(double t) {
            r[0] *= t;
            r[1] *= t;
            r[2] *= t;
            return *this;
        }

        vector3& operator/=(const vector3& v) {
            r[0] /= v.r[0];
            r[1] /= v.r[1];
            r[2] /= v.r[2];
            return *this;
        }

        vector3& operator/=(double t) {
            r[0] /= t;
            r[1] /= t;
            r[2] /= t;
            return *this;
        }

        vector3& operator-() const {
            return vector3(-r[0], -r[1], -r[2]);
        }

        double magnitude() const {
            return std::sqrt(r[0]*r[0] + r[1]*r[1] + r[2]*r[2]);
        }

        vector3 normalize() {
            double mag = magnitude();
            r[0] /= mag;
            r[1] /= mag;
            r[2] /= mag;
            return *this;
        }

        vector3 normalized() const {
            double mag = magnitude();
            return vector3(r[0]/mag, r[1]/mag, r[2]/mag);
        }
};

inline vector3 operator+(const vector3& a, const vector3& b) {
    return vector3(a.r[0] + b.r[0], a.r[1] + b.r[1], a.r[2] + b.r[2]);
}

inline vector3 operator-(const vector3& a, const vector3& b) {
    return vector3(a.r[0] - b.r[0], a.r[1] - b.r[1], a.r[2] - b.r[2]);
}

inline vector3 operator*(const vector3& a, const vector3& b) {
    return vector3(a.r[0] * b.r[0], a.r[1] * b.r[1], a.r[2] * b.r[2]);
}

inline vector3 operator*(const vector3& v, double t) {
    return vector3(v.r[0] * t, v.r[1] * t, v.r[2] * t);
}

inline vector3 operator*(double t, const vector3& v) {
    return v * t;
}

inline double dot(const vector3& a, const vector3& b) {
    return a.r[0] * b.r[0] + a.r[1] * b.r[1] + a.r[2] * b.r[2];
}

inline vector3 cross(const vector3& a, const vector3& b) {
    return vector3(
        a.r[1] * b.r[2] - a.r[2] * b.r[1],
        a.r[2] * b.r[0] - a.r[0] * b.r[2],
        a.r[0] * b.r[1] - a.r[1] * b.r[0]
    );
}

vector3 average(const std::vector<vector3>& vectors) {

    double xsum = 0;
    double ysum = 0;
    double zsum = 0;

    for (const vector3& v : vectors) {
        xsum += v.x();
        ysum += v.y();
        zsum += v.z();
    }

    int count = vectors.size();
    return vector3(xsum / count, ysum / count, zsum / count);
}

vector3 randomDirection() {
    vector3 ans = vector3(vecDist(gen), vecDist(gen), vecDist(gen));
    return ans.normalized();
}

inline std::ostream& operator<<(std::ostream& out, const vector3& v) {
    return out << "[" << v.x() << ", " << v.y() << ", " << v.z() << "]";
}

#endif