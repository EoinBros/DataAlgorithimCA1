package ie.setu.campsite.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// a booking for a guest - the preferences are used to pick a plot, then plot gets set
public class Booking implements Serializable {
    private LocalDate start;
    private LocalDate end;
    private String areaId;
    private Guest guest;
    private Plot plot;

    // what the guest is looking for
    private String type;
    private int numGuests;
    private boolean powered;
    private double minSize;
    private int minBedrooms;
    private double budget;

    public Booking(LocalDate start, LocalDate end, String areaId, Guest guest, String type,
                   int numGuests, boolean powered, double minSize, int minBedrooms, double budget) {
        this.start = start;
        this.end = end;
        this.areaId = areaId;
        this.guest = guest;
        this.type = type;
        this.numGuests = numGuests;
        this.powered = powered;
        this.minSize = minSize;
        this.minBedrooms = minBedrooms;
        this.budget = budget;
    }

    // end date is checkout day so it doesn't count as a night
    public long getNights() {
        return ChronoUnit.DAYS.between(start, end);
    }

    public double getTotalCost() {
        if (plot == null) {
            return 0;
        }
        return getNights() * plot.getCostPerNight();
    }

    // two stays clash if one starts before the other ends
    public boolean overlaps(LocalDate otherStart, LocalDate otherEnd) {
        return start.isBefore(otherEnd) && otherStart.isBefore(end);
    }

    // pending = hasn't finished yet
    public boolean isPending() {
        return !end.isBefore(LocalDate.now());
    }

    // checks if a plot ticks all the boxes for this booking
    public boolean suits(Plot p) {
        return p.getType().equalsIgnoreCase(type)
                && (!powered || p.isPowered())
                && p.getSize() >= minSize
                && p.getBedrooms() >= minBedrooms
                && (!p.isStatic() || p.getBeds() >= numGuests)
                && p.getCostPerNight() * getNights() <= budget
                && p.isAvailable(start, end);
    }

    public LocalDate getStart() { return start; }
    public void setStart(LocalDate start) { this.start = start; }

    public LocalDate getEnd() { return end; }
    public void setEnd(LocalDate end) { this.end = end; }

    public String getAreaId() { return areaId; }
    public void setAreaId(String areaId) { this.areaId = areaId; }

    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }

    public Plot getPlot() { return plot; }
    public void setPlot(Plot plot) { this.plot = plot; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getNumGuests() { return numGuests; }
    public void setNumGuests(int numGuests) { this.numGuests = numGuests; }

    public boolean isPowered() { return powered; }
    public void setPowered(boolean powered) { this.powered = powered; }

    public double getMinSize() { return minSize; }
    public void setMinSize(double minSize) { this.minSize = minSize; }

    public int getMinBedrooms() { return minBedrooms; }
    public void setMinBedrooms(int minBedrooms) { this.minBedrooms = minBedrooms; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    @Override
    public String toString() {
        String where = plot == null ? "no plot yet" : "plot " + plot.getId();
        return guest.getName() + ": " + start + " to " + end + " (" + where + ")";
    }
}
