# MyName-Search-Engine

UPITT_2020Spring_CS1660_Course_Project

## Submission

### First application communicates with the User: 
* Accepts user input and displays the output
* Deployed on Docker container

### Docker to Local (or GCP) Cluster Communication: 
* The first application can communicate with second application on gcp
* Submitting hadoop jobs
* Reading output of the jobs on gcp
* Output base on user's input

### Second application processes User requests: 
* Constructing basic Map-Reduce inverting indices
* Deployed on the GCP Cluster

## Getting Start
	
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.


### Prerequisites

Install docker

`pip install docker-compose`

Install socat

`sudo apt-get update && sudo apt-get install socat`

Install X11 (xQuartz also ok)

`sudo apt-get install xorg openbox`


### Installing

Pull docker image

`docker pull quelegeque/my-name-search:latest`

## Running the tests

### Run socat

`socat TCP-LISTEN:6000,reuseaddr,fork UNIX-CLIENT:\"$DISPLAY\"`

### Docker run

`docker run -e DISPLAY=localhost:0 quelegeque/my-name-search`

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [XQuartz](https://www.xquartz.org/index.html) - windowsGUI

## Walkthrough video

[![Walkthrough](https://img.youtube.com/vi/iawv2tOq-zQ/0.jpg)](https://youtu.be/iawv2tOq-zQ)

## Credentials

If you need the google cluster credentials, you can check the diff of 'Second' commit.
