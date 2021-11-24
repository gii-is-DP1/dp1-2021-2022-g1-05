# FlippingBoards Application 

This is a project made by the lab group 05 in the DP1 course by the University of Seville.

## Running FlippingBoards locally
FlippingBoards is a [Spring Boot](https://spring.io/guides/gs/spring-boot) application built using [Maven](https://spring.io/guides/gs/maven/). You can build a jar file and run it from the command line:


```
git clone https://github.com/gii-is-DP1/dp1-2021-2022-g1-05.git
cd dp1-2021-2022-g1-05
./mvnw package
java -jar target/*.jar
```

You can then access FlippingBoards here: http://localhost:8080/

<img width="1042" alt="petclinic-screenshot" src="https://user-images.githubusercontent.com/80274500/143267713-07de73f6-9269-4339-9a82-3b15493d06ee.PNG">

Or you can run it from Maven directly using the Spring Boot Maven plugin. If you do this it will pick up changes that you make in the project immediately (changes to Java source files require a compile as well - most people use an IDE for this):

```
./mvnw spring-boot:run
```

## Database configuration

In its default configuration, FlippingBoards uses an in-memory database (H2) which
gets populated at startup with data. 

## Working with FlippingBoards in your IDE

### Prerequisites
The following items should be installed in your system:
* Java 8 or newer.
* git command line tool (https://help.github.com/articles/set-up-git)
* Your preferred IDE 
  * Eclipse with the m2e plugin. Note: when m2e is available, there is an m2 icon in `Help -> About` dialog. If m2e is
  not there, just follow the install process here: https://www.eclipse.org/m2e/
  * [Spring Tools Suite](https://spring.io/tools) (STS)
  * IntelliJ IDEA
  * [VS Code](https://code.visualstudio.com)

### Steps:

1) On the command line
```
git clone https://github.com/gii-is-DP1/dp1-2021-2022-g1-05.git
```
2) Inside Eclipse or STS
```
File -> Import -> Maven -> Existing Maven project
```

Then either build on the command line `./mvnw generate-resources` or using the Eclipse launcher (right click on project and `Run As -> Maven install`) to generate the css. Run the application main method by right clicking on it and choosing `Run As -> Java Application`.

3) Inside IntelliJ IDEA

In the main menu, choose `File -> Open` and select the Petclinic [pom.xml](pom.xml). Click on the `Open` button.

CSS files are generated from the Maven build. You can either build them on the command line `./mvnw generate-resources`
or right click on the FlippingBoards project then `Maven -> Generates sources and Update Folders`.

A run configuration named `ParchisYOcaApplication` should have been created for you if you're using a recent Ultimate
version. Otherwise, run the application by right clicking on the `ParchisYOcaApplication` main class and choosing
`Run 'ParchisYOcaApplication'`.

4) Navigate to FluppingBoards

Visit [http://localhost:8080](http://localhost:8080) in your browser.


