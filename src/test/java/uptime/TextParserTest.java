package uptime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TextParserTest {
    @TempDir
    Path tempDir;

    @Test
    void testParserValidInputFile() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        String content = """
                [Stations]
                0 1001 1002
                1 1003
                2 1004
                
                [Charger Availability Reports]
                1001 0 50000 true
                1002 50000 100000 true
                1003 25000 75000 false
                1004 0 50000 true
                1004 100000 200000 true""";

        Files.writeString(inputFile, content);
        Map<Integer, Station> stations = TextParser.parse(inputFile.toString());

        assertEquals(3, stations.size());
        assertTrue(stations.containsKey(0));
        Station station0 = stations.get(0);
        assertTrue(station0.hasCharger(1001));
        assertTrue(station0.hasCharger(1002));

        assertTrue(stations.containsKey(1));
        Station station1 = stations.get(1);
        assertTrue(station1.hasCharger(1003));

        assertTrue(stations.containsKey(2));
        Station station2 = stations.get(2);
        assertTrue(station2.hasCharger(1004));
    }

    @Test
    void testInvalidStation() throws IOException {
       Path inputFile = tempDir.resolve("input.txt");
       String content = """
               [Stations]
               9
               
               [Charger Availability Reports]
               1001 10 20 true""";
       Files.writeString(inputFile, content);
       assertThrows(IllegalArgumentException.class, () -> TextParser.parse(inputFile.toString()));
    }

    @Test
    void testInvalidRecords() throws IOException {
        Path inputFile = tempDir.resolve("input.txt");
        String content = """
                [Stations]
                0 1001
                
                [Charger Availability Reports]
                1001 50 true""";
        Files.writeString(inputFile, content);
        assertThrows(IllegalArgumentException.class, () -> TextParser.parse(inputFile.toString())) ;
    }

    @Test
    void testFileNtExist() {
       assertThrows(IllegalArgumentException.class, () -> TextParser.parse("ghostFile.txt"));
    }
}
