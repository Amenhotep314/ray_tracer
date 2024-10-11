import numpy as np
from PIL import Image
from scene_objects import Scene, Ray, Camera, Light, Sphere, Plane


def cast_ray(ray, scene, max_depth=5, depth=0, background_color=np.array([0, 0, 0])):

    if depth > max_depth:
        return background_color

    closest_object, point = scene.ray_intersection(ray)
    if closest_object is None:
        return background_color

    if isinstance(closest_object, Light):
        return closest_object.color

    new_ray = closest_object.bounce_ray(ray, point)
    color = cast_ray(new_ray, scene, max_depth=max_depth, depth=depth+1, background_color=background_color)
    return closest_object.color_seen(color, new_ray)


def render(scene, max_depth=5, trials=1, ortho=False, background_color=np.array([0, 0, 0])):

    width = scene.camera.width
    height = scene.camera.height
    image = Image.new("RGB", size=(width, height))

    for x in range(width):
        for y in range(height):
            color = background_color
            attempts = []
            while len(attempts) < trials:
                if ortho:
                    ray = scene.camera.pixel_ortho_ray(x, y)
                else:
                    ray = scene.camera.pixel_perspective_ray(x, y)

                color = cast_ray(ray, scene, max_depth=max_depth, background_color=background_color)
                attempts.append(color)
            color = np.mean(attempts, axis=0)
            image.putpixel((x, y), tuple(int(c * 255) for c in color))
            percent = (x * height + y) / (width * height) * 100
            print(f"Progress: {percent:.2f}%", end="\r")

    image.save("out.png")

sample_scene = Scene(
    Camera(height=720, width=1080, depth=720/(2*np.tan(np.pi/4 - np.pi/16))),
    Light(np.array([0, 2, 3]), np.array([3, 3, 3]), 1),
    Sphere(np.array([0, 2, 0]), np.array([1, 0, 0]), 1, 1, 1.5, 0.7),
    Sphere(np.array([-0.7, 1.5, -0.3]), np.array([0, 0, 1]), 0.5, 1, 1.5, 0.3),
    Sphere(np.array([0.9, 2.5, 0.3]), np.array([0, 1, 0]), 1, 1, 1.5, 0.4),
    Sphere(np.array([0, 2, -15]), np.array([0.25, 0.25, 0.25]), 1, 1, 1.5, 14.3)
)

render(sample_scene, max_depth=10, trials=100, background_color=np.array([0.7, 0.8, 1]))