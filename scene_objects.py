import numpy as np
from functools import cache

class SceneObject:

    def __init__(self, position):

        self.position = position
        self.ignore = False

    def intersect(self, ray):

        target = self.position - ray.origin
        if np.linalg.norm(np.cross(target, ray.direction)) == 0 and np.dot(target, ray.direction) > 0:
            return self.position
        return None

    def normal(self, point):

        raise NotImplementedError

    def change_to_pixel_coordinates(self, world_unit):

        self.position = self.position * world_unit


class Camera(SceneObject):

    def __init__(self, position=np.array([0, 0, 0]), width=1920, height=1080, depth=2000, direction=np.array([0, 1, 0]), vertical=np.array([0, 0, 1])):

        super().__init__(position)

        self.direction = direction / np.linalg.norm(direction)
        self.vertical = vertical / np.linalg.norm(vertical)
        assert np.dot(self.direction, self.vertical) == 0
        self.horizontal = np.cross(self.direction, self.vertical)
        self.center = self.direction * depth + position

        self.depth = depth
        self.width = width
        self.height = height

    def pixel_perspective_ray(self, x, y):

        x_space = x - (self.width / 2) + 2 * (np.random.rand() - 0.5)
        y_space = (self.height / 2) - y + 2 * (np.random.rand() - 0.5)
        pixel = self.center + x_space * self.horizontal + y_space * self.vertical
        return Ray(self.position, pixel)

    def pixel_ortho_ray(self, x, y):

        x_space = x - (self.width / 2) + 2 * (np.random.rand() - 0.5)
        y_space = (self.height / 2) - y + 2 * (np.random.rand() - 0.5)
        pixel = self.center + x_space * self.horizontal + y_space * self.vertical
        return Ray(pixel, self.direction)


class Light(SceneObject):

    def __init__(self, position, color, radius):

        super().__init__(position)
        self.color = color
        self.radius = radius

    @cache
    def intersect(self, ray):

        D = np.dot(ray.direction, ray.origin - self.position) ** 2 - (np.linalg.norm(ray.origin - self.position) ** 2 - self.radius ** 2)
        if D < 0:
            return None
        t = -np.dot(ray.direction, ray.origin - self.position) - np.sqrt(D)
        if t < 0:
            return None
        return ray.origin + t * ray.direction

    def change_to_pixel_coordinates(self, world_unit):

        self.position = self.position * world_unit
        self.radius = self.radius * world_unit


class VisibleObject(SceneObject):

    def __init__(self, position, color, roughness, opacity, refractive_index):

        super().__init__(position)
        self.color = color
        self.roughness = roughness
        self.opacity = opacity
        self.refractive_index = refractive_index

    def bounce_ray(self, ray, point):

        intersection_point = point
        normal = self.normal(intersection_point)

        opaque_probability = np.random.rand()

        # The ray passes through the object
        if opaque_probability > self.opacity:
            incoming_angle = np.arccos(np.dot(ray.direction, normal))
            outgoing_angle = np.arcsin(np.sin(incoming_angle) / self.refractive_index)
            # Snell's Law (https://www.flipcode.com/archives/reflection_transmission.pdf)
            new_direction = (1/self.refractive_index) * ray.direction - ((1/self.refractive_index) * np.cos(incoming_angle) + np.sqrt(1 - (np.sin(outgoing_angle) ** 2))) * normal
            return Ray(intersection_point, new_direction)

        specular_probability = np.random.rand()

        # The ray is reflected
        if specular_probability > self.roughness:
            new_direction = ray.direction - 2 * np.dot(ray.direction, normal) * normal
            return Ray(intersection_point, new_direction)

        # The ray is scattered
        new_direction = np.random.normal(size=3)
        new_direction = new_direction / np.linalg.norm(new_direction)
        if np.dot(new_direction, normal) < 0:
            new_direction = -new_direction
        return Ray(intersection_point, new_direction)

    def color_seen(self, outgoing_color, outgoing_ray):

        return self.color * outgoing_color


class Sphere(VisibleObject):

    def __init__(self, position, color, roughness, opacity, refractive_index, radius):

        super().__init__(position, color, roughness, opacity, refractive_index)
        self.radius = radius

    @cache
    def intersect(self, ray):

        D = np.dot(ray.direction, ray.origin - self.position) ** 2 - (np.linalg.norm(ray.origin - self.position) ** 2 - self.radius ** 2)
        if D < 0:
            return None
        t = -np.dot(ray.direction, ray.origin - self.position) - np.sqrt(D)
        if t < 0:
            return None
        return ray.origin + t * ray.direction

    def normal(self, point):

        return (point - self.position) / np.linalg.norm(point - self.position)

    def change_to_pixel_coordinates(self, world_unit):

        self.position = self.position * world_unit
        self.radius = self.radius * world_unit


class Plane(VisibleObject):

    def __init__(self, position, color, roughness, opacity, refractive_index, normal_vector):

        super().__init__(position, color, roughness, opacity, refractive_index)
        self.normal_vector = normal_vector / np.linalg.norm(normal_vector)

    @cache
    def intersect(self, ray):

        if np.dot(ray.direction, self.normal_vector) == 0:
            return None
        t = np.dot(self.position - ray.origin, self.normal_vector) / np.dot(ray.direction, self.normal_vector)
        if t < 0:
            return None
        return ray.origin + t * ray.direction

    def normal(self, point):

        return self.normal_vector


class Ray:

    def __init__(self, origin, direction, length=None):
        self.origin = origin
        self.direction = direction / np.sqrt(np.sum(direction ** 2))
        self.length = length


class Scene:

    def __init__(self, *items):

        items = list(items)

        for item in items:
            if isinstance(item, Camera):
                self.camera = item
                items.remove(item)
                break

        self.world_unit = self.camera.height
        self.camera.change_to_pixel_coordinates(self.world_unit)
        for i in range(len(items)):
            items[i].change_to_pixel_coordinates(self.world_unit)

        self.lights = []
        self.visible_objects = []
        for item in items:
            if isinstance(item, Light):
                self.lights.append(item)
            elif isinstance(item, VisibleObject):
                self.visible_objects.append(item)



    def ray_intersection(self, ray):

        targets = self.lights + self.visible_objects
        distances = []
        points = []
        hit = False

        for target in targets:
            point = target.intersect(ray)
            if point is not None:
                distance = np.linalg.norm(point - ray.origin)
                if distance < 1:
                    continue
                distances.append(distance)
                points.append(point)
                hit = True
            else:
                distances.append(np.inf)
                points.append(None)

        if not hit:
            return None, None
        index = np.argmin(distances)
        return targets[index], points[index]
