# DBOOKS Telegram Bot

DBooksTelegramBot is a Telegram bot designed to assist users in finding and downloading books

This bot uses [dBooks API](https://www.dbooks.org/api/) .



https://github.com/akshaj65/DBooksTelegramBot/assets/57281244/a8d83de0-3b65-459c-b269-8fdd1b481302


## Features

- Search Books
- Recent Releases
- Book Details
- Download Option



## Requirements

- **Java Development Kit (JDK)**: Java 17 is required for building and running this application. Ensure you have the Oracle JDK 17 installed on your system.
- **Maven**: For managing dependencies and building the project.
- **Telegram Account**: To create and manage your Telegram bot.
- **Docker** (optional)


## Setup

1) Clone the project

```bash
  git clone https://github.com/akshaj65/DBooksTelegramBot.git
```

2) Go to the project directory

```bash
  cd DBooksTelegramBot
```

3) Install Maven dependencies

```bash
  mvn clean install
```
4) Register your bot with Telegram via BotFather to obtain your  `BOT_TOKEN`.


5) Create a config.properties file in the project's resources directory with the following properties:

```
bot.name=YourBotName
bot.token=YourBotToken
```

6) Build the maven project  
```bash
    mvn clean package
```

7) Start the bot

```bash
  java -jar target/dBooksTelegramBot.jar-D exec.mainClass="com.akshaj.Main"
```


## Docker Support

This project is equipped with Docker support to facilitate deployment and execution across various environments. Dockerizing your Telegram bot allows for a simplified build and run process within containers.
Building the Docker Image

To construct the Docker image, follow these steps:

- Navigate to the project directory.
- Execute the following command:

```bash 
docker build -t your-bot-name .
 ```


Replace **your-bot-name** with the preferred name for your Docker image.

Running the Docker Container

After successfully building the Docker image, proceed with the following command to launch a container:
```bash
docker run --name your-bot-container -d -p   8080:8080 your-bot-name
```

Replace **your-bot-container** with an appropriate name for your Docker container instance. 
