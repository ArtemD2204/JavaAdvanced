package ru.progwards.java2.lessons.patterns;

// паттерн Proxy
public class FilteredGPS {
    private GPS point;

    public FilteredGPS(GPS point) {
        this.point = FilterByVelocity.INSTANCE.filterPoint(point);
    }

    public GPS getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return point.toString();
    }

    public static void main(String[] args) {
        final int NUMBER_OF_POINTS = 1000;
        FilteredGPS[] filteredGPSArr = new FilteredGPS[NUMBER_OF_POINTS];
        for (int i=0; i < NUMBER_OF_POINTS; i++) {
            GPS point = new GPS();
            if (i == 500
                    || i == 700
                    || i == 900
            ) {
                point.lat = 0;
                point.lon = 0;
            } else {
                point.lat = i;
                point.lon = i;
            }
            point.time = i;
            filteredGPSArr[i] = new FilteredGPS(point);
        }

        for (int i=0; i < NUMBER_OF_POINTS; i++) {
            if (filteredGPSArr[i].getPoint() == null)
                System.out.println(i + " : " + "null");
        }
    }
}
