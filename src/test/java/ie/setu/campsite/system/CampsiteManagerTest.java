package ie.setu.campsite.system;

import ie.setu.campsite.datastructures.MyLinkedList;
import ie.setu.campsite.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CampsiteManagerTest {

    private CampsiteManager manager;
    private Campsite campsite;
    private CampingArea lake;
    private CampingArea forest;
    private Plot b1;
    private Plot b2;
    private Guest mary;

    private final LocalDate july1 = LocalDate.now().plusMonths(1);
    private final LocalDate july4 = july1.plusDays(3);

    @BeforeEach
    void setUp() {
        manager = new CampsiteManager();
        campsite = new Campsite("Sunny Camp", "Tramore", 4, "051 123456");
        campsite.addFacility("Pool");
        manager.addCampsite(campsite);

        lake = new CampingArea("Lake", "By the lake");
        forest = new CampingArea("Forest", "In the trees");
        manager.addArea(campsite, lake);
        manager.addArea(campsite, forest);

        b1 = new Plot("B1", "Bungalow", true, 2, 4, 50, 80, "http://example.com/b1.jpg");
        b2 = new Plot("B2", "Bungalow", true, 2, 4, 50, 90, "http://example.com/b2.jpg");
        manager.addPlot(lake, b1);
        manager.addPlot(forest, b2);

        mary = manager.addGuest("Mary", LocalDate.of(1990, 5, 1), "mary@email.com", "");
    }

    private Booking bookingFor(String areaId) {
        return new Booking(july1, july4, areaId, mary, "Bungalow", 2, true, 40, 2, 500);
    }

    @Test
    void guestIdsGoUpAutomatically() {
        Guest john = manager.addGuest("John", LocalDate.of(1985, 1, 1), "", "");
        assertEquals(1, mary.getId());
        assertEquals(2, john.getId());
        assertEquals(john, manager.getGuest(2));
    }

    @Test
    void cantAddTwoCampsitesWithSameName() {
        assertFalse(manager.addCampsite(new Campsite("sunny camp", "Somewhere", 3, "")));
    }

    @Test
    void bookingGetsAssignedAPlot() {
        Booking b = bookingFor("Lake");
        assertTrue(manager.makeBooking(campsite, b));
        assertEquals(b1, b.getPlot());
        assertEquals(1, mary.getBookings().size());
    }

    @Test
    void bookingFailsWhenNothingIsFree() {
        manager.makeBooking(campsite, bookingFor("Lake"));
        assertFalse(manager.makeBooking(campsite, bookingFor("Lake")));
    }

    @Test
    void cancelRemovesFromPlotAndGuest() {
        Booking b = bookingFor("Lake");
        manager.makeBooking(campsite, b);
        manager.cancelBooking(b);
        assertTrue(b1.getBookings().isEmpty());
        assertTrue(mary.getBookings().isEmpty());
    }

    @Test
    void removingPlotMovesBookingToAnotherArea() {
        Booking b = bookingFor("Lake");
        manager.makeBooking(campsite, b);
        MyLinkedList<Booking> notMoved = manager.removePlot(campsite, lake, b1);
        assertTrue(notMoved.isEmpty());
        assertEquals(b2, b.getPlot());
        assertEquals("Forest", b.getAreaId());
    }

    @Test
    void smartFindPicksTheCheapest() {
        MyLinkedList<String> wants = new MyLinkedList<>();
        wants.add("pool");
        SmartFindResult r = manager.smartFind(2, 1000, 3, july1, july4, wants);
        assertNotNull(r);
        assertEquals(b1, r.getPlot());
    }

    @Test
    void smartFindMovesDatesWhenFull() {
        manager.makeBooking(campsite, bookingFor("Lake"));
        manager.makeBooking(campsite, bookingFor("Forest"));
        SmartFindResult r = manager.smartFind(2, 1000, 1, july1, july4, new MyLinkedList<>());
        assertNotNull(r);
        assertEquals(july4, r.getStart()); // first day both are free again
    }

    @Test
    void resetClearsEverything() {
        manager.reset();
        assertTrue(manager.getCampsites().isEmpty());
        assertTrue(manager.getGuests().isEmpty());
    }

    @Test
    void saveAndLoadKeepsTheData() throws Exception {
        manager.makeBooking(campsite, bookingFor("Lake"));
        File file = File.createTempFile("campsite", ".dat");
        file.deleteOnExit();
        manager.save(file);

        CampsiteManager loaded = CampsiteManager.load(file);
        Campsite c = loaded.getCampsite("Sunny Camp");
        assertNotNull(c);
        assertEquals(1, c.getArea("Lake").getPlot("B1").getBookings().size());
        assertEquals(1, loaded.getGuest(1).getBookings().size());
    }
}
