# Hometask

## Prerequisites

Ensure you have the following installed:
- Java JDK (version 21.0.2)
- Gradle (version 8.7)

You can check if you have them installed by running `java -version` and `gradle -version` in your terminal.

## Setting Up

1. **Clone the Repository**

    ```shell
    git clone https://github.com/ssuslovs/Homework-QA.git
    cd <REPOSITORY_NAME>
    ```

2. **Build the Project**

    Navigate to the project directory and run the following command to build the project:

    ```shell
    gradle build
    ```

    This command compiles the code, downloads the necessary dependencies, and builds the project.

3. **Run Tests**

    Execute the tests using the following command:

    ```shell
    gradle test
    ```

    This will run the unit tests defined in the project.

4. **Run the Application**

    To run the main application, use the command:

    ```shell
    gradle run
    ```

    This will start the application as per the configuration specified in your `build.gradle.kts` file.

5. ** Script location**
   
   ```shell
   src/test/java/PrestaShopTest.java
   ```
   
## Troubleshooting

If you encounter any issues with the setup, consider the following:

- Ensure that you have the correct Java JDK and Gradle versions installed.
- Run `gradle --refresh-dependencies` to refresh the project's dependencies.
- Check if the IDE configuration is correct and that the project is recognized as a Gradle project.

## P.S.
I used IntelliJ Idea, and ran the test by ctrl+shift+F10.
Don't judge too harshly :) It's my first experience with Java, gradle and selenium webdriver.
There were a few bugs, and I wasn't sure if they were expected or not, so I decided not to use assertions to keep the tests running.
You can see a screenshot of the console output.
![2024-04-02 11_06_17-Hometask – build gradle kts (Hometask)](https://github.com/ssuslovs/Homework-QA/assets/115178774/a5504395-b1db-4d7a-8c8f-e572ba541688)

