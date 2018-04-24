FROM openjdk:8
RUN wget -q https://services.gradle.org/distributions/gradle-4.7-bin.zip \
&& unzip gradle-4.7-bin.zip -d /opt \
&& rm gradle-4.7-bin.zip
ENV GRADLE_HOME /opt/gradle-4.7
ENV PATH $PATH:/opt/gradle-4.7/bin
EXPOSE 8080
COPY . /app
WORKDIR /app
RUN gradle build
CMD ["gradle", "run"]
