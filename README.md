# ShapeMosaic

## Overview
ShapeMosaic recreates a target image using basic polygons, aiming to use as few shapes as possible. The project is an improved version of my other project _"Polygon Image Recreation"_ in 2022, which adds optimizations for both shape generation and runtime, as well as cleaning up the codebase.

The program uses a genetic algorithm to approximate the best shape, which is an effective but also relatively slow approach. Back in 2022, this resulted in extremely slow renders of up to 13 hours, whereas this optimised version can achieve similar renders in less than 2 hours.

## Optimisations

### Runtime
- Added bounding box generation to each shape, which reduces calculations on unnecessary pixels during processes like fitness evaluation and shape generation.
- Where possible, switched from accessing BufferedImage pixels with `image.getRGB()` and instead converted the image into a pixel array.

### Shape Generation
- Biases new shape generation to the average size of the last 5 shapes (since best shape size tends downwards as detail increases)
- Generating a difference mask between the recreation and target image, and biasing new shapes towards pixels with a greater difference.

### Planned
- Custom function to handle _population/generation/children_ over time, as a smaller population is needed early on (since the image is so unrefined) and much more important later on (when detail is higher)

## Example Renders
<p align="center" margin="0" padding="0" >
<img height="600" alt="cat_recreation" src="https://github.com/user-attachments/assets/656338fe-092e-443b-a72f-4e74b2a97fde" />
<img height="600" alt="output" src="https://github.com/user-attachments/assets/066208e5-9e7a-4cd2-a396-3d611d2d2363" />
<img width="708" alt="output" src="https://github.com/user-attachments/assets/bf08d888-f953-4b11-a15f-33813dd8b2da" />
</p>

## Levels of Detail
<img width="400" alt="250" src="https://github.com/user-attachments/assets/14ab9bb2-d936-468c-b38d-4a40acc5cd67" />
<img width="400"  alt="500" src="https://github.com/user-attachments/assets/a02b8e31-d431-4523-a13e-b80eec9a7744" />
<img width="400" alt="1000" src="https://github.com/user-attachments/assets/5e1e3da0-ce5b-4e41-bb7c-74537b2ab789" />
