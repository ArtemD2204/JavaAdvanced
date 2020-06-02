package ru.progwards.java2.lessons.patterns;

public enum FilterByVelocity {
    INSTANCE;

    private long counter;
    private GPS lastPoint;
    private double expectedValue;
    private double dispersion;

    FilterByVelocity() {
        counter = 0;
        lastPoint = null;
        expectedValue = 0;
        dispersion = 0;
    }

    private double getLatVelocity(GPS point) {
        return (point.lat - lastPoint.lat) / (point.time - lastPoint.time);
    }

    private double getLonVelocity(GPS point) {
        return (point.lon - lastPoint.lon) / (point.time - lastPoint.time);
    }

    private double calculateVelocity(GPS point) {
        if (lastPoint == null)
            return 0;
        double latVel = getLatVelocity(point);
        double lonVel = getLonVelocity(point);
        return Math.sqrt(latVel * latVel + lonVel * lonVel);
    }

    private void updateCounter() {
        counter++;
    }

    private void updateExpectedValue(double velocity) {
        expectedValue = (counter-1.0) / counter * expectedValue + 1.0 / counter * velocity;
    }

    private void updateDispersion(double velocity) {
        double deviation = velocity - expectedValue;
        dispersion = (counter-1.0) / counter * dispersion + 1.0 / counter * (deviation * deviation);
    }

    public GPS filterPoint(GPS point) {
        double velocity = calculateVelocity(point);
        double deviation = Math.abs(velocity - expectedValue);
        if (counter > 50 && deviation > 3 * Math.sqrt(dispersion)) {
            return null;
        } else {
            updateCounter();
            updateExpectedValue(velocity);
            updateDispersion(velocity);
            lastPoint = point;
            return point;
        }
    }

    public void resetStatistic() {
        counter = 0;
        lastPoint = null;
        expectedValue = 0;
        dispersion = 0;
    }
}
