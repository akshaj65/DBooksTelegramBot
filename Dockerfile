# Use the distroless Java base image
FROM gcr.io/distroless/java17-debian12

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/dependency-jars dependency-jars
COPY target/application.jar dbookbot.jar


# Expose the port your app runs on
EXPOSE 8080

# Run the jar file
CMD ["dbookbot.jar","-D","exec.mainClass=\"com.akshaj.Main\""]