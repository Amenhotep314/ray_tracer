# Ray Tracer
This is a Java raytracing library I made because I want to! I wrote most of the algorithm myself, inspired by [Sebastian Lague](https://www.youtube.com/watch?v=Qz0KTGYJtUk). For more technical details, I turned to the [Raytracing in One Weekend Series](https://raytracing.github.io/).

<img width="3840" height="2160" alt="sampleScene_4k" src="https://github.com/user-attachments/assets/a79abc8e-ca0b-432d-9ee0-485be369e1ec" />

## Implementations
 - Python: This is a complete but rudimentary implementation that lacks any kind of optimization or parallelization. It's just a proof-of-concept for my algorithm, and it leaves out antialiasing, defocus blur, animation, and much more.
 - Java: This is the primary implementation right now, although I'm working on a port to Rust to replace it. This has all of the bells and whistles left out of the Python implementation, and as an added bonus it doesn't take 50 years to render.
 - C++: This implementation is incomplete, although most of the structure is outlined. It does not compile, and I'm not actively working on it since I chose Rust instead.
