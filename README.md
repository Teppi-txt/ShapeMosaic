# ShapeMosaic

## Overview 📐
> The project does have a command line interface, but does not have a GUI or executable compilation currently, so it is left as a pure java project which can be edited via an IDE or code editor. There aren't any plans for a GUI in the near future.

**ShapeMosaic** recreates a target image using basic polygons, aiming to use as few shapes as possible. The project is an improved version of my other project _"Polygon Image Recreation"_ in 2022, which adds optimizations for both shape generation and runtime, as well as cleaning up the codebase. 


The program uses a genetic algorithm to approximate the best shape, which is an effective but also relatively slow approach. Back in 2022, this resulted in extremely slow renders of up to 13 hours, whereas this optimised version can achieve similar renders in less than 2 hours.

## Features
- Generation of polygon renders
- Exports render history which can then be saved and rerendered on command
- Ability to adjust rendering parameters via a CLI
- Ability to view image masks in realtime


## Optimisations 

### Runtime 🏃
- Added bounding box generation to each shape, which reduces calculations on unnecessary pixels during processes like fitness evaluation and shape generation.
- Where possible, switched from accessing BufferedImage pixels with `image.getRGB()` and instead converted the image into a pixel array.
- Adjusted evaluation to calculate improvement over similarity score, as improvement can be calculated within the bounding box while similarity cannot.

### Shape Generation 🔵
- Biases new shape generation to the average size of the last 5 shapes (since best shape size tends downwards as detail increases)
- Generating a difference mask between the recreation and target image, and biasing new shapes towards pixels with a greater difference.

### Planned 🏗️
- Custom function to handle _population/generation/children_ over time, as a smaller population is needed early on (since the image is so unrefined) and much more important later on (when detail is higher)

## Example Renders
<p align="center" margin="50px" padding="0" >
  <img width="708" alt="output" src="https://github.com/user-attachments/assets/bf08d888-f953-4b11-a15f-33813dd8b2da" />
  <img height="600" alt="cat_recreation" src="https://github.com/user-attachments/assets/656338fe-092e-443b-a72f-4e74b2a97fde" />
  <img height="600" alt="output" src="https://github.com/user-attachments/assets/066208e5-9e7a-4cd2-a396-3d611d2d2363" />
  <img width="708" alt="image" src="https://github.com/user-attachments/assets/ad8ae645-f01e-4914-a623-ee96f6e413bb" />

</p>


## Levels of Detail
Different shape quantities produce different styles of render, ranging from a "low-poly" aesthetic to detailed but fuzzy.

<p align="center" margin="50px" padding="0" >
  <img width="400" alt="250" src="https://github.com/user-attachments/assets/14ab9bb2-d936-468c-b38d-4a40acc5cd67" />
  <img width="400"  alt="500" src="https://github.com/user-attachments/assets/a02b8e31-d431-4523-a13e-b80eec9a7744" />
  <img width="400" alt="1000" src="https://github.com/user-attachments/assets/5e1e3da0-ce5b-4e41-bb7c-74537b2ab789" />
  <img width="400" alt="1500" src="https://github.com/user-attachments/assets/bf1a2cf3-a6ab-415b-b04d-74d2713645c8" />
  <img width="400" alt="2000" src="https://github.com/user-attachments/assets/da7d0c05-5b19-492b-a7b3-68f7144dcd27" />
<img width="400" alt="2500" src="https://github.com/user-attachments/assets/ba79b639-defe-44e2-aec6-8dc4564fdd8b" />

</p>
