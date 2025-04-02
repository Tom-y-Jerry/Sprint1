package es.ulpgc.dacd.domain.model;

public class Station {
    private final int id;
    private final String carrierId;
    private final String shortName;
    private final String longName;
    private final String timeZone;
    private final double latitude;
    private final double longitude;
    private final boolean isMetaGare;
    private final String address;

    public Station(int id, String carrierId, String shortName, String longName,
                   String timeZone, double latitude, double longitude,
                   boolean isMetaGare, String address) {
        this.id = id;
        this.carrierId = carrierId;
        this.shortName = shortName;
        this.longName = longName;
        this.timeZone = timeZone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isMetaGare = isMetaGare;
        this.address = address;
    }

    public int getId() { return id; }
    public String getCarrierId() { return carrierId; }
    public String getShortName() { return shortName; }
    public String getLongName() { return longName; }
    public String getTimeZone() { return timeZone; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isMetaGare() { return isMetaGare; }
    public String getAddress() { return address; }
}
