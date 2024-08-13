package com.ewm.util.geo;

import com.ewm.model.Location;

public abstract class GeoUtil {
    private static final double EARTH_RADIUS = 6371e3;

    public static boolean isWithinRadius(double eventLatitude, double eventLongitude, Location location) {
        double lat1 = Math.toRadians(location.getLatitude());
        double lon1 = Math.toRadians(location.getLongitude());
        double lat2 = Math.toRadians(eventLatitude);
        double lon2 = Math.toRadians(eventLongitude);

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
            Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;

        return distance <= location.getRadius();
    }
}
