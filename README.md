## Charger Uptime
The program calculates the uptime for stations. It reads in the file path, and reads from the file, pasrse the text into the Station class that stores chargers and records and calcuate the uptime, and ouptut the result to console.


### Structure
The project is built with maven so the structure is as follows:
```
charger-uptime
|-- src/
|    |-- main/
|         |-- java/
|               |-- uptime/
|                    |-- Uptime.java      # Entry point of the app
|                    |-- Station.java     # Represents a station and calculate uptime
|                    |-- TextParser.java  # Parses input files
|-- test/
|    |-- java/
|          |-- uptime/
|                |-- StationTest.java     # Unit tests
|                |-- TextParserTest.java  # Unit tests
```

### How to compile and run
##### With out Maven
1. Navigate to project directory
   ``` bash
   cd src/main/java
   ```
2. Compile
   ``` bash
   javac uptime/*.java -d ../../../out
   ```
3. Run the program
   ``` bash
   cd ../../../out
   java uptime.Uptime pathToFile/input.txt
   ```

##### With Maven
1. Compile
   ``` bash
   mvn compile
   ```
2. Run
   ``` bash
   mvn exec:java -Dexec.mainClass="uptime.Uptime" -Dexec.args="pathToFile/input.txt"
   ```

### Explain
##### Data structure
The main operation is in `Station` class. Each statoin stores charger and record data in hashmap that the key is the ID of the chargers, and the value is the records of the chargers, stored in a list of `long[]` long array. I think hashmap is good for this charger uptime scenario. In the future if we need more operation on each charger, maybe we can just store a list of new charger class.

##### calculate uptime
First I sort the records using Java's built in sort method I think it is O(nlogn) time. Then single pass through the sorted list to calculate the uptime that would be O(n). But it needs O(n) time to add all the records together from chargers' lists first.

##### Tests
The test files test:
- valid input cases
- invalid input cases (end time earlier than start time, wrong file content format ...)
- edge cases (overlapping intervals, gaps, 0 uptime situation)
