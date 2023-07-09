package TwinderClient.GetRequestClient;

import java.util.ArrayList;
import java.util.Collections;

public class GetRequestAnalyze {
    public ArrayList<Long> latencyRecords;

    public GetRequestAnalyze(ArrayList<Long> latencyRecords) {
        this.latencyRecords = latencyRecords;
    }

    public double getMeanLatency() {
        long totalSum = 0L;
        for (long latency : this.latencyRecords) {
            totalSum += latency;
        }
        double average = (double) totalSum / this.latencyRecords.size();
        return average;
    }

    public double getMaxLatency() {
        return Collections.max(this.latencyRecords);
    }

    public double getMinLatency() {
        return Collections.min(this.latencyRecords);
    }
}
