# Remote Control Tool

**Note**: This repository is a work in progress.

This Java project is a tool that allows you to remotely access another machine. 

## Current Features
* Serverless - instead it's peer-to-peer
* Connecting to a computer using a unique identifier (IP address and port is masked)

## Goals
The goal of the project is to recreate a Teamviewer or AnyDesk type-clone and open-source it.

## Work In Progress
### MVP: Screen Capture
An optimized rendering mechanism. The way the project is being designed is that frames are captured and encoded
into the [TIFF](https://en.wikipedia.org/wiki/TIFF) image format and then compressed using [LZW](https://en.wikipedia.org/wiki/Lempel%E2%80%93Ziv%E2%80%93Welch).

The initial screenshot is then split into 12x12 segments. There on, any changes that occur within a segment, only those
changes are sent to the client. This achieves really low latency. 

An experiment has been done by sending the entire image. On a 2K display, an individual frame can be around 0.5 MB in size.
This works and may be acceptable to some users if this was a simple streaming application however in a "remote control" scenario,
there's a 1-2 second delay between inputs which can feel jarring. An experiment of reducing the image size to 1080x720 which reduces the image to around 0.2MB.
This works great, it's super snappy and is like-real-time but the image becomes blurry when scaled up and appears tiny if you have a really large monitor.

The current work in progress is sending segments. Each segment (on a 2K display) turns out to be around 213x120 which means tiny images sizes that can be
transmitted over the network super quickly. When there is little motion, the image feels ultra-real-time. If there is a lot of movement happening e.g. a video playing in full screen,
then we are back to the jarring latency and poor quality stream. This is a theorized outcome so an update will be provided as progress is made. If this is an issue, a hybrid approach 
is next. Using both segmented processing and full image processing but the ultimate goal is to have a good remote experience.

### User Input
Keyboard and mouse inputs work. Pretty well and actions scale to the relative positions. There is a bug when using a second screen, if the second screen is shared then the
relative mouse positions are currently not taken into calculations.

### Visual Bounding Box
There is a visual yellow border around the screen being captured. This border does not get transmitted over the stream.

### File Transfer
There is a base implementation for transferring files from one computer to another.

## Contributing
Everyone interested in the project is welcome to contribute. Just pull the code and create a PR with your changes. Ensure to include some good details on what your PR is.

## Screenshots

![Image of Launch](https://i.imgur.com/svPUgwt.png)

![Image of Launch When Offline](https://i.imgur.com/daFrrka.png)

![Image When Connected](https://i.imgur.com/De8RKop.png)

[Video Preview](https://youtu.be/YuK69U8Tog8)
