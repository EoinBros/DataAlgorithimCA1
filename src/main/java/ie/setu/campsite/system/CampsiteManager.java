package ie.setu.campsite.system;

import ie.setu.campsite.datastructures.MyLinkedList;
import ie.setu.campsite.models.*;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// the main logic for the whole system - the GUI talks to this, not straight to the lists
public class CampsiteManager implements Serializable {
    private MyLinkedList<Campsite> campsites = new MyLinkedList<>();
    private MyLinkedList<Guest> guests = new MyLinkedList<>();
    private int nextGuestId = 1;

    // ---------- adding ----------

    // campsite names have to be unique
    public boolean addCampsite(Campsite campsite) {
        if (getCampsite(campsite.getName()) != null) {
            return false;
        }
        campsites.add(campsite);
        return true;
    }

    public Campsite getCampsite(String name) {
        for (Campsite c : campsites) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public boolean addArea(Campsite campsite, CampingArea area) {
        return campsite.addArea(area);
    }

    public boolean addPlot(CampingArea area, Plot plot) {
        return area.addPlot(plot);
    }

    // ids are handed out automatically so they never clash
    public Guest addGuest(String name, LocalDate dob, String contact, String notes) {
        Guest g = new Guest(nextGuestId++, name, dob, contact, notes);
        guests.add(g);
        return g;
    }

    public Guest getGuest(int id) {
        for (Guest g : guests) {
            if (g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    // ---------- bookings ----------

    // goes through the plots in the area and takes the first one that suits
    public boolean makeBooking(Campsite campsite, Booking booking) {
        CampingArea area = campsite.getArea(booking.getAreaId());
        if (area == null) {
            return false;
        }
        Plot plot = findSuitablePlot(area, booking);
        if (plot == null) {
            return false;
        }
        assign(booking, plot);
        booking.getGuest().addBooking(booking);
        return true;
    }

    private Plot findSuitablePlot(CampingArea area, Booking booking) {
        for (Plot p : area.getPlots()) {
            if (booking.suits(p)) {
                return p;
            }
        }
        return null;
    }

    private void assign(Booking booking, Plot plot) {
        booking.setPlot(plot);
        plot.addBooking(booking);
    }

    // takes it off the plot and off the guest
    public void cancelBooking(Booking booking) {
        if (booking.getPlot() != null) {
            booking.getPlot().removeBooking(booking);
        }
        booking.getGuest().removeBooking(booking);
    }

    // ---------- removing a plot ----------

    // removes the plot and tries to move its pending bookings somewhere else in the same campsite
    // returns the bookings that couldn't be moved (these get cancelled)
    public MyLinkedList<Booking> removePlot(Campsite campsite, CampingArea area, Plot plot) {
        MyLinkedList<Booking> notMoved = new MyLinkedList<>();
        area.removePlot(plot);

        for (Booking b : plot.getBookings()) {
            if (!b.isPending()) {
                continue; // old bookings just go with the plot
            }
            boolean moved = false;
            for (CampingArea a : campsite.getAreas()) {
                Plot newPlot = findSuitablePlot(a, b);
                if (newPlot != null) {
                    b.setAreaId(a.getId());
                    assign(b, newPlot);
                    moved = true;
                    break;
                }
            }
            if (!moved) {
                b.getGuest().removeBooking(b);
                notMoved.add(b);
            }
        }
        return notMoved;
    }

    // ---------- viewing ----------

    // pending bookings for one campsite, by area then plot
    public String pendingBookings(Campsite campsite) {
        StringBuilder sb = new StringBuilder();
        double total = writePending(campsite, sb);
        sb.append("Total: €").append(String.format("%.2f", total));
        return sb.toString();
    }

    // same thing but for every campsite
    public String allPendingBookings() {
        StringBuilder sb = new StringBuilder();
        double total = 0;
        for (Campsite c : campsites) {
            total += writePending(c, sb);
            sb.append("\n");
        }
        sb.append("Overall total: €").append(String.format("%.2f", total));
        return sb.toString();
    }

    private double writePending(Campsite campsite, StringBuilder sb) {
        double total = 0;
        int count = 0;
        sb.append(campsite.getName()).append("\n");
        for (CampingArea a : campsite.getAreas()) {
            sb.append("  ").append(a.getId()).append("\n");
            for (Plot p : a.getPlots()) {
                for (Booking b : p.getBookings()) {
                    if (b.isPending()) {
                        sb.append("    ").append(p.getId()).append(": ").append(b.getGuest().getName())
                                .append(", ").append(b.getStart()).append(" to ").append(b.getEnd())
                                .append(", ").append(b.getNumGuests()).append(" guests, €")
                                .append(String.format("%.2f", b.getTotalCost())).append("\n");
                        total += b.getTotalCost();
                        count++;
                    }
                }
            }
        }
        sb.append("  ").append(count).append(" pending, €").append(String.format("%.2f", total)).append("\n");
        return total;
    }

    // guest details plus their pending and past bookings
    public String guestRecord(int id) {
        Guest g = getGuest(id);
        if (g == null) {
            return "No guest with id " + id;
        }
        StringBuilder pending = new StringBuilder();
        StringBuilder past = new StringBuilder();
        for (Booking b : g.getBookings()) {
            StringBuilder target = b.isPending() ? pending : past;
            target.append("  ").append(b).append(", €").append(String.format("%.2f", b.getTotalCost())).append("\n");
        }
        return g + "\nDOB: " + g.getDateOfBirth() + "\nContact: " + g.getContact()
                + "\nNotes: " + g.getNotes()
                + "\n\nPending:\n" + (pending.length() == 0 ? "  none\n" : pending)
                + "\nPast:\n" + (past.length() == 0 ? "  none\n" : past);
    }

    // ---------- smart find ----------

    // looks for the cheapest plot that fits - if the dates asked for are full,
    // it tries pushing the stay back a day at a time for up to 2 weeks
    public SmartFindResult smartFind(int numGuests, double budget, int minStars,
                                     LocalDate start, LocalDate end, MyLinkedList<String> facilities) {
        long nights = ChronoUnit.DAYS.between(start, end);
        for (int shift = 0; shift <= 14; shift++) {
            LocalDate s = start.plusDays(shift);
            LocalDate e = s.plusDays(nights);
            SmartFindResult best = null;

            for (Campsite c : campsites) {
                if (c.getStars() < minStars) continue;
                for (CampingArea a : c.getAreas()) {
                    for (Plot p : a.getPlots()) {
                        if (fits(c, p, numGuests, budget, nights, s, e, facilities)
                                && (best == null || p.getCostPerNight() < best.getPlot().getCostPerNight())) {
                            best = new SmartFindResult(c, a, p, s, e);
                        }
                    }
                }
            }
            if (best != null) {
                return best;
            }
        }
        return null;
    }

    private boolean fits(Campsite c, Plot p, int numGuests, double budget, long nights,
                         LocalDate s, LocalDate e, MyLinkedList<String> facilities) {
        if (p.getCostPerNight() * nights > budget) return false;
        if (p.isStatic() && p.getBeds() < numGuests) return false;
        if (!p.isAvailable(s, e)) return false;
        // each facility can be at the campsite (e.g. pool) or the plot (e.g. air con)
        for (String f : facilities) {
            if (!c.hasFacility(f) && !p.hasFacility(f)) return false;
        }
        return true;
    }

    // ---------- reset / save / load ----------

    public void reset() {
        campsites.clear();
        guests.clear();
        nextGuestId = 1;
    }

    // saves the whole thing in one go - all the lists come with it
    public void save(File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        }
    }

    public static CampsiteManager load(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (CampsiteManager) in.readObject();
        }
    }

    public MyLinkedList<Campsite> getCampsites() { return campsites; }
    public MyLinkedList<Guest> getGuests() { return guests; }
}
