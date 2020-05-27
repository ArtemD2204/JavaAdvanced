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
        return Math.sqrt(Math.pow(getLatVelocity(point), 2.0) + Math.pow(getLonVelocity(point), 2.0));
    }

    private void updateCounter() {
        counter++;
    }

    private void updateExpectedValue(double velocity) {
        expectedValue = (counter-1.0) / counter * expectedValue + 1.0 / counter * velocity;
    }

    private void updateDispersion(double velocity) {
        dispersion = (counter-1.0) / counter * dispersion + 1.0 / counter * Math.pow(velocity - expectedValue, 2.0);
    }

    public GPS filterPoint(GPS point) {
        double velocity = calculateVelocity(point);
        updateCounter();
        updateExpectedValue(velocity);
        updateDispersion(velocity);
        if (Math.abs(velocity - expectedValue) > 3 * Math.sqrt(dispersion) && counter > 50) {
            return null;
        } else {
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
