package ie.setu.campsite.models;

import ie.setu.campsite.datastructures.MyLinkedList;

import java.time.LocalDate;

// a single plot/pitch - could be a static (mobile home, bungalow etc.) or an empty pitch
public class Plot {
    private String id;
    private String type;
    private boolean powered;
    private int bedrooms;
    private int beds;
    private double size;
    private double costPerNight;
    private String photoUrl;
    private MyLinkedList<String> facilities = new MyLinkedList<>();
    private MyLinkedList<Booking> bookings = new MyLinkedList<>();

    public Plot(String id, String type, boolean powered, int bedrooms, int beds,
                double size, double costPerNight, String photoUrl) {
        this.id = id;
        this.type = type;
        this.bedrooms = bedrooms;
        this.beds = beds;
        this.size = size;
        this.costPerNight = costPerNight;
        this.photoUrl = photoUrl;
        setPowered(powered);
    }

    // anything that isn't an empty pitch counts as a static dwelling
    public boolean isStatic() {
        return !type.toLowerCase().contains("pitch");
    }

    // free if none of its bookings clash with these dates
    public boolean isAvailable(LocalDate start, LocalDate end) {
        for (Booking b : bookings) {
            if (b.overlaps(start, end)) {
                return false;
            }
        }
        return true;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public boolean removeBooking(Booking booking) {
        return bookings.remove(booking);
    }

    public void addFacility(String facility) {
        facilities.add(facility);
    }

    public boolean hasFacility(String facility) {
        for (String f : facilities) {
            if (f.equalsIgnoreCase(facility)) {
                return true;
            }
        }
        return false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) {
        this.type = type;
        setPowered(powered); // in case it's changed to a static
    }

    public boolean isPowered() { return powered; }

    // statics always have power, it's optional for pitches
    public void setPowered(boolean powered) {
        this.powered = isStatic() || powered;
    }

    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public int getBeds() { return beds; }
    public void setBeds(int beds) { this.beds = beds; }

    public double getSize() { return size; }
    public void setSize(double size) { this.size = size; }

    public double getCostPerNight() { return costPerNight; }
    public void setCostPerNight(double costPerNight) { this.costPerNight = costPerNight; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public MyLinkedList<String> getFacilities() { return facilities; }
    public MyLinkedList<Booking> getBookings() { return bookings; }

    @Override
    public String toString() {
        return id + " - " + type + ", " + size + "m², €" + costPerNight + "/night";
    }
}
