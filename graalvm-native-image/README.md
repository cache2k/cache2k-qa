Test code to check that GraalVM build runs without additional parameters and
all needed functional variants are present in the build. Build and run with:

````
mvn clean package
native-image -jar target/graalvm-native-image.jar
./graalvm-native-image
````
