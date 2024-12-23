package uptime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StationTest {
    @Test
    void testAddChargerRecordSuccess() {
        Station station = new Station(101);

        station.addChargerRecord(1001, 0, 50000, true);

        assertTrue(station.hasCharger(1001));
    }

    @Test
    void testAddChargerRecordFailure() {
        Station station = new Station(101);

        assertThrows(IllegalArgumentException.class,
                () -> station.addChargerRecord(1001, 100000, 50000, true));

    }

    @Test
    void TestGetStationID() {
        Station station = new Station(101);

        assertEquals(101, station.getStationID());
    }

    @Test
    void TestGetUpTimesWithNoRecords() {
        Station station = new Station(101);

        assertEquals(0, station.getStationUpTime());
    }

    @Test
    void TestGetUpTimesWithNoUptimeRecords() {
        Station station = new Station(101);
        station.addChargerRecord(1001, 25000, 75000, false);
        station.addChargerRecord(1001, 5000, 10000, false);
        station.addChargerRecord(1003, 0, 50000, false);

        assertEquals(0, station.getStationUpTime());
    }

    @Test
    void testOverlappingIntervals() {
        Station station = new Station(101);

        station.addChargerRecord(1001, 0, 5000, true);
        station.addChargerRecord(1001, 3000, 8000, true);
        station.addChargerRecord(1001, 7000, 12000, true);
        station.addChargerRecord(1002, 1000, 4000, false);
        station.addChargerRecord(1002, 4000, 6000, true);
        station.addChargerRecord(1002, 6000, 10000, true);

        assertEquals(100, station.getStationUpTime());
    }

    @Test
    void tetNonContiguousPeriods() {
        Station station = new Station(102);

        station.addChargerRecord(2001, 0, 50, true); // 50
        station.addChargerRecord(2001, 150, 200, true); // 50
        station.addChargerRecord(2001, 300, 350, false); // 0
        station.addChargerRecord(2002, 50, 100, true); // 50
        station.addChargerRecord(2002, 250, 300, true); // 50
        station.addChargerRecord(2002, 350, 400, false); // 0
        // total 400

        assertEquals(50, station.getStationUpTime());
    }

    @Test
    void testOneRecordUpAllTime() {
        Station station = new Station(103);

        station.addChargerRecord(3001, 0, 1000, true);

        assertEquals(100, station.getStationUpTime());
    }

    @Test
    void testGetStationUpTime() {
        Station station = new Station(104);

        station.addChargerRecord(4001, 0, 100, true); // 100
        station.addChargerRecord(4001, 500, 1500, false);
        station.addChargerRecord(4001, 1500, 2000, true); // 500
        station.addChargerRecord(4002, 0, 500, false);
        station.addChargerRecord(4002, 1000, 2000, true); // 500

        assertEquals(55, station.getStationUpTime());
    }
}
