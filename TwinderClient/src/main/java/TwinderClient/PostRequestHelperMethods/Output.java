package TwinderClient.PostRequestHelperMethods;

/**
 *  helper.Output class stores start time, request type, latency, response code.
 */
public class Output {
    long startTime;
    String requestType = "post";
    long latency;
    int responseCode;

    public long getStartTime() {
        return startTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public long getLatency() {
        return latency;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String[] getData() {
        return new String[]{String.valueOf(getStartTime()), getRequestType(), String.valueOf(getLatency()), String.valueOf(getResponseCode())};
    }
}
