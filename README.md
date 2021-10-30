# CS480 Homework 3

## Set up database config file
1. Copy the `db_config.sample.json` file located inside the in `src/main/resources/` and rename it to `db_config.json`
2. Change the contents of `db_config.json` by replacing all `<...>` with the appropriate database config
3. Save the file

## `transfile.txt`
This is the file that contains all the commands. Make sure all commands are placed in this file.


## Running the program

### Using IntelliJ IDEA
This is probably the simplest way to run this program. Simply open this project in IntelliJ and press the "play" button and the program should run without any issues.

### Using Maven
Make sure Maven is installed in your system. Once that is done, open up your terminal app and type the following commands:
```
$ mvn package
```
This will create the `.jar` files in the `target/` folder
```
$ mvn exec:java
```
With this command, the program should run without any issues.
