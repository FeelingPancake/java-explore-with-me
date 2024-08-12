package com.ewm.util.geo;

import com.ewm.model.Location;

import java.util.Map;

public abstract class GeoUtil {
    private static final double EARTH_RADIUS = 6371e3;

    public static Map<String, Double> getBoundingBox(Location location) {
        double latRadians = Math.toRadians(location.getLatitude());
        double radius = location.getRadius();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        double latDelta = Math.toDegrees(radius / EARTH_RADIUS);
        double lonDelta = Math.toDegrees(radius / EARTH_RADIUS * Math.cos(latRadians));

        double minLat = latitude - latDelta;
        double maxLat = latitude + latDelta;
        double minLon = longitude - lonDelta;
        double maxLon = longitude + lonDelta;

        return Map.of(
            "minLatitude", minLat,
            "maxLatitude", maxLat,
            "minLongitude", minLon,
            "maxLongitude", maxLon
        );
    }
}
