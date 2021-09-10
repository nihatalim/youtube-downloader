# Youtube Downloader (with Event Driven Architecture)

## Technologies
- Docker
- Spring Boot
- Kafka
- Youtube-DL

# Prerequirements

- Docker
- Docker Compose

## Installation

- Clone repository
- Go root directory in terminal and run following command (it takes approximately 30 minutes)

`
  docker compose up
`

- After completed build and start process you have 5 running containers: 

![image](https://user-images.githubusercontent.com/33811062/132825969-5ba72291-a78b-4203-a343-4c0ea2dcb34e.png)


## Usage

### Show downloaded audios

- Go http://localhost:8080/api/all
- You can access downloaded audios from here.


### Download audio from youtube

- Send post request to http://localhost:8080/api/downloads like below:

![image](https://user-images.githubusercontent.com/33811062/132827639-4cbd68a8-fab3-4775-a7a4-feb22c9fb290.png)


- After a while, you can see downloaded mp3 file in the above list.
