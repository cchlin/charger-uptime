package uptime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextParser {
    public static Map<Integer, Station> parse(String filePath) {
        Map<Integer, Station> stations = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inStationSection = false;
            boolean inChargerRecordSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    if (line.equals("[Stations]")) {
                        inStationSection = true;
                        inChargerRecordSection = false;
                    } else if (line.equals("[Charger Availability Reports]")) {
                        inStationSection = false;
                        inChargerRecordSection = true;
                    } else if (inStationSection) {
                        parseStationLine(line, stations);
                    } else if (inChargerRecordSection) {
                        parseChargerRecordLine(line, stations);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Cannot find the file: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading the file: " + filePath, e);
        }

        return stations;
    }

    private static void parseStationLine(String line, Map<Integer, Station> stations) {
        // split the line
        String[] parts = line.split("\\s+"); // one or more space

        if (parts.length < 2) {
            throw new IllegalArgumentException("Station format is not valid");
        }

        int stationID = Integer.parseInt(parts[0]);
        Station station = stations.computeIfAbsent(stationID, Station::new); // create a station if not already exist

        for (int i = 1; i < parts.length; i++) {
           station.addCharger(Integer.parseInt(parts[i]));
        }
    }

    private static void parseChargerRecordLine(String line, Map<Integer, Station> stations) {
        String[] parts = line.split("\\s+");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Charger record format is not valid");
        }

        int chargerID = Integer.parseInt(parts[0]);
        long startTime = Long.parseLong(parts[1]);
        long endTime = Long.parseLong(parts[2]);
        boolean up = Boolean.parseBoolean(parts[3]);

        for (Station station : stations.values()) {
            if (station.hasCharger(chargerID)) {
                station.addChargerRecord(chargerID, startTime, endTime,up);
                return; // no need to keep going through after the record is added
            }
        }

        throw new IllegalArgumentException("No charger found in stations: ChargerID=" + chargerID);

    }
}
