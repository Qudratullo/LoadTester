public class Statistics {
    private long minTime;
    private long maxTime;
    private long sumTime;
    private int countSuccess;

    public Statistics(long minTime, long maxTime, long sumTime, int countSuccess) {
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.sumTime = sumTime;
        this.countSuccess = countSuccess;
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getSumTime() {
        return sumTime;
    }

    public int getCountSuccess() {
        return countSuccess;
    }
}
