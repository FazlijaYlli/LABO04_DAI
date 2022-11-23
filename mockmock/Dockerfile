FROM openjdk:11-jre
LABEL authors="Kylian Manzini, Ylli Fazlija"

RUN apt-get update && apt-get clean

COPY MockMock.jar /app/MockMock.jar

EXPOSE 25
EXPOSE 8282

CMD java -jar /app/MockMock.jar