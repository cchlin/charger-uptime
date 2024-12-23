package uptime;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

public class Station {
    private final int stationID;
    private final Map<Integer, List<long[]>> chargers = new HashMap<>();
    private long stationStartTime = Long.MAX_VALUE;
    private long stationEndTime = Long.MIN_VALUE;

    public Station(int stationID) {
        this.stationID = stationID;
    }

    public int getStationID() {
        return stationID;
    }

    public void addCharger(int chargerID) {
        chargers.putIfAbsent(chargerID, new ArrayList<>());
    }

    public void addChargerRecord(int chargerID, long startTime, long endTime, boolean up) {
        if (endTime < startTime) {
            throw new IllegalArgumentException("End time cannot be earlier than start time: startTime="
                                                + startTime + ", endTime=" + endTime);
        }

        stationStartTime = Math.min(stationStartTime, startTime);
        stationEndTime = Math.max(stationEndTime, endTime);

        chargers.putIfAbsent(chargerID, new ArrayList<>());
        chargers.get(chargerID).add(new long[] {startTime, endTime, up ? 1 : 0});
    }

    public int getStationUpTime() {
        // First add all records across the chargers together
        List<long[]> allRecords = new ArrayList<>();
        for (List<long[]> chargerRecords : chargers.values()) {
            allRecords.addAll(chargerRecords);
        }
        // then sort the records by start time
        allRecords.sort(Comparator.comparingLong(record -> record[0]));

        long totalUpTime = 0;
        long prevEnd = -1;
        long currStart;
        long currEnd;
        boolean isUp;

        for (long[] record : allRecords) {
            currStart = record[0];
            currEnd = record[1];
            isUp = record[2] == 1;

            // as long as there's one charger up, the station is up, so
            // only need to track uptimes and their overlaps
            if (isUp) {
               if (currStart < prevEnd)  { // overlapped
                   if (currEnd > prevEnd) { // partial overlapped, add the non-overlap time
                       totalUpTime += currEnd - prevEnd;
                       prevEnd = currEnd;
                   } // do nothing if fully overlapped
               } else { // no overlap
                  totalUpTime += currEnd - currStart;
                  prevEnd = currEnd;
               }

            }
        }

        long totalTime = stationEndTime - stationStartTime;
        return totalTime > 0 ? (int) Math.floor((totalUpTime * 100.0) / totalTime) : 0;
    }

    public boolean hasCharger(int chargerID) {
        return chargers.containsKey(chargerID);
    }
}
