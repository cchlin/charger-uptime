package uptime;

import java.util.Map;

public class Uptime {
    public static void main(String[] args) {
        if (args.length != 1) {
           System.err.println("Usage:");
           return;
        }

        String filePath = args[0];

        try {
            Map<Integer, Station> stations = TextParser.parse(filePath);

            stations.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEach(station -> {
                       System.out.println(station.getKey() + " " + station.getValue().getStationUpTime());
                    });
        } catch (Exception e) {
            System.err.println("ERROR");
            return;
        }
    }
}
