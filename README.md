# **Laboratory DAI SMTP 22-23**
### *Developement team members* :
- Ylli Fazlija
- Kylian Manzini

## **What is this project about ?** 🤔
The goal of this developement project was to create a robot that can read e-mail addresses and settings from a configuration file, which will then be used to create a variable number of groups and a sender which will send fake mails to the members of the group.

### Details on the Maven project 👇
All the java files created have been placed within a folder called `mvn-project`. Inside is the `pom.xml` file used to decribe a maven project. Our source files are inside the `src` package.
then inside the `main.config` are the configuration files, which we will conver a bit later, and finally inside `main.java.ch.heigvd` are all of our java classes described on the UML diagram.

## **What is MockMock ?** ☕
MockMock is a very simple Java implemetation of an SMTP server. When launched, it opens an SMTP server on port 25 and also an http graphical interface on the port 8282 which allows for consultation of the mails received on the server. We used Docker to run this MockMock server and we also tested the features of our bot on it.

## **How to use our PrankBot ?** 🤖
### Setting up docker
To use our bot, you first need to build a docker image containing the MockMock server.
#### *Step 1 : Run Docker desktop*
Docker needs to be running for these next steps to work !
#### *Step 2 : Find the mockmock folder*
The jar file called `MockMock.jar` is already in the `mockmock` folder at root.
Inside this folder you will also find a file called `Dockerfile` this file does not need to be modified and is used to correctly create the docker image.
#### *Step 3 : Run the `build-image.sh` script*
This script will execute the necessary commands to build the mockmock image for docker. It does no need to be modified. A console should appear, print some text and then close by iteself. If the console doesn't print anything, please check if Docker is running.
#### *Step 4 : Run the `start-image.sh` script*
This script will tell Docker to use the newly created image to create a container and start it. After this script has been executed, a console will be opened and stay open until the docker image is closed. Once the console tells you that the HTTP server is running on port 8282, you can go to http://localhost:8282 and you should see the MockMock interface.

### Using the program
#### Step 1 : Customize the behiaviour using the config files
Go into the root folder, then `src/main/config/`. Here you will find `mails.xml`, `profiles.xml` and `settings.properties`.

To add a mail to the list of mail, simply create a `<mail>` tag, and inside create a `<subject>` tag, and put the subject of your mail inside it. Also inside the mail tag, create a `<content>` tag and put the content of your mail in it. Your new mail is finished ! You can repeat the process indefinitely.

To add an email address to the pool, open the `profiles.xml` file and add a `<person>` tag inside the `<people>` tag. Inside the newly created person tag, create an `<address>` tag and inside, put the email address. This added the mail to the email pools.

Finally, open the `settings.properties` file and you will see 3 fields. The first two are used to configure the server address and port, and the third one is the number of the groups you want to create. It must be positive otherwise the program will throw an exception.

#### Step 2 : Compile and run our project
Next, go back to the root of the folder and go into the `mvn-project` folder. Once in there, execute the `mvn clean package` command. This will recompile the project and create a jar executable file in the `target` folder.
So go in this `target` folder and use the `java -jar .\LABO-SMTP-1.0-SNAPSHOT.jar`.
This should launch our program and send the correct e-mails from the mails file using the mails in the profiles files, if all the configuration files were correct.

### Stopping docker
Simply run the `stop-image.sh` script in the `mockmock` folder and the mockmock container should stop.