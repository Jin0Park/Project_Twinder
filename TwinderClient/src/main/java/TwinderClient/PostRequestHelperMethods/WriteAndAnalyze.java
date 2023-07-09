package TwinderClient.PostRequestHelperMethods;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *  helper.WriteAndAnalyze class writes stored information (start time, request type, latency, respnose code) into a CSV file
 *  and analyze the performance of operation.
 */
public class WriteAndAnalyze {
    private ArrayList<Output> records;
    private long totalResTime = 0;
    private long medianResTime = 0;
    private long meanResTime = 0;
    private long minResTime = (long) Double.POSITIVE_INFINITY;
    private long maxResTime = (long) Double.NEGATIVE_INFINITY;
    private int numReq;
    public WriteAndAnalyze(ArrayList<Output> records, int numReq) {
        this.records = records;
        this.numReq = numReq;
    }

    public void analyze() {
        ArrayList<Long> latencyList = getSortedLatencyList();
        for (int i = 0; i < latencyList.size(); i++) {
            long currentResTime = latencyList.get(i);
            totalResTime += currentResTime;
        }
    }

    public ArrayList<Long> getSortedLatencyList() {
        ArrayList<Long> latencyList = new ArrayList<>();
        for (int i = 0; i < this.records.size(); i++) {
            if (this.records.get(i) != null) {
                latencyList.add(this.records.get(i).getLatency());
            }
        }
        latencyList.sort(Comparator.naturalOrder());
        return latencyList;
    }

    public double getMeanResTime() {
        return Double.valueOf(getTotalResTime() / this.numReq);
    }

    public long getTotalResTime() {
        long sum = 0;
        for (int i = 0; i < records.size(); i++) {
            sum += records.get(i).getLatency();
        }
        return sum;
    }

    public double getMedianResTime() {
        ArrayList<Long> latencyList = getSortedLatencyList();
        if (latencyList.size() % 2 == 0) {
            return Double.valueOf((latencyList.get(latencyList.size()/2) + latencyList.get(latencyList.size()/2 - 1))/2);
        } else {
            return Double.valueOf(latencyList.get(latencyList.size() / 2));
        }
    }

    public double getMinResTime() {
        ArrayList<Long> latencyList = getSortedLatencyList();
        return Double.valueOf(latencyList.get(0));
    }

    public double getMaxResTime() {
        ArrayList<Long> latencyList = getSortedLatencyList();
        return Double.valueOf(latencyList.get(latencyList.size()-1));
    }

    public long get99Percentile() {
        ArrayList<Long> latencyList = getSortedLatencyList();
        int arrayIndex = (int) (Math.round(latencyList.size() * 0.99 + .5) - 1);
        return latencyList.get(arrayIndex);
    }

    public long getMinStartTime() {
        long minStart = (long) Double.POSITIVE_INFINITY;
        for (int i = 0; i < this.records.size(); i++) {
            if (this.records.get(i) != null) {
                minStart = Math.min(minStart, this.records.get(i).getStartTime());
            }
        }
        return minStart;
    }

    public Map<Long, Integer> convertData() {
        Map<Long, Integer> info = new HashMap<>();
        long minStart = getMinStartTime();
        for (int i = 0; i < this.records.size(); i++) {
            Output currOutput = this.records.get(i);
            if (currOutput != null) {
                long currTime = currOutput.getStartTime() - minStart;
                if (info.containsKey(currTime)) {
                    info.put(currTime, info.get(currTime) + 1);
                } else {
                    info.put(currTime, 1);
                }
            }
        }
        return info;
    }

    public void writeData(String filePath) {
        File file = new File(filePath);
        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            String[] header = {"start time", "request type", "latency", "response code"};
            writer.writeNext(header);
            int i = 0;
            long minStart = getMinStartTime();
            while (i < this.records.size()) {
                Output currOutput = this.records.get(i);
                if (currOutput != null) {
                    currOutput.setStartTime(currOutput.getStartTime() - minStart);
                    writer.writeNext(currOutput.getData());
                }
                i++;
            }
            writer.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
