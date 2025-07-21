# ShapeMosaic

## Overview
ShapeMosaic recreates a target image using basic polygons, aiming to use as few shapes as possible. The project is an improved version of my other project "Polygon Image Recreation" in 2022, which adds optimizations for both shape generation and runtime, as well as cleaning up the codebase.

## Optimisations

### Runtime
- Added bounding box generation to each shape, which reduces calculations on unnecessary pixels during processes like fitness evaluation and shape generation.
- Where possible, switched from accessing BufferedImage pixels with image.getRGB() and instead converted the image into a pixel array.

### Shape Generation
- Biases new shape generation to the average size of the last 5 shapes (since best shape size tends downwards as detail increases)
- Generating a difference mask between the recreation and target image, and biasing new shapes towards pixels with a greater difference.

### Planned
- Custom function to handle population/generation/children over time, as a smaller population is needed early on (since the image is so unrefined) and much more important later on (when detail is higher)

## Example Renders
<p align="center" margin="0" padding="0" >
<img height="600" alt="cat_recreation" src="https://github.com/user-attachments/assets/656338fe-092e-443b-a72f-4e74b2a97fde" />
<img height="600" alt="output" src="https://github.com/user-attachments/assets/066208e5-9e7a-4cd2-a396-3d611d2d2363" />
</p>
