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

The initial screenshot is then split into segments. There on, any changes that occur within a segment, only those
changes are sent to the client. This achieves really low latency. Transmission by happens at 30fps. There isn't an improvement in quality or performance by lowering/increasing this.

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
