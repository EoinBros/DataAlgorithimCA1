package ie.setu.campsite.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ModelsTest {

    private Campsite campsite;
    private CampingArea area;
    private Plot bungalow;
    private Plot pitch;
    private Guest guest;

    @BeforeEach
    void setUp() {
        campsite = new Campsite("Sunny Camp", "Tramore", 4, "051 123456");
        area = new CampingArea("Lake View Area 1", "By the lake");
        bungalow = new Plot("B1", "Bungalow", false, 2, 4, 50, 80, "http://example.com/b1.jpg");
        pitch = new Plot("P1", "Empty pitch", false, 0, 0, 30, 20, "http://example.com/p1.jpg");
        guest = new Guest(1, "Mary", LocalDate.of(1990, 5, 1), "mary@email.com", "");
    }

    @Test
    void cantAddTwoAreasWithSameId() {
        assertTrue(campsite.addArea(area));
        assertFalse(campsite.addArea(new CampingArea("lake view area 1", "dupe")));
        assertEquals(1, campsite.getAreas().size());
    }

    @Test
    void starsStayBetweenOneAndFive() {
        campsite.setStars(9);
        assertEquals(5, campsite.getStars());
        campsite.setStars(0);
        assertEquals(1, campsite.getStars());
    }

    @Test
    void staticPlotsAlwaysHavePower() {
        assertTrue(bungalow.isPowered());
        assertFalse(pitch.isPowered());
    }

    @Test
    void nightsAndCostWorkedOut() {
        Booking b = new Booking(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 4),
                area.getId(), guest, "Bungalow", 2, false, 0, 0, 500);
        b.setPlot(bungalow);
        assertEquals(3, b.getNights());
        assertEquals(240, b.getTotalCost());
    }

    @Test
    void overlappingBookingMakesPlotUnavailable() {
        Booking b = new Booking(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5),
                area.getId(), guest, "Bungalow", 2, false, 0, 0, 1000);
        bungalow.addBooking(b);
        assertFalse(bungalow.isAvailable(LocalDate.of(2026, 7, 3), LocalDate.of(2026, 7, 8)));
        // checking out the same day someone else checks in is fine
        assertTrue(bungalow.isAvailable(LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 8)));
    }

    @Test
    void suitsChecksTheGuestsPreferences() {
        Booking b = new Booking(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3),
                area.getId(), guest, "Bungalow", 2, true, 40, 2, 200);
        assertTrue(b.suits(bungalow));
        assertFalse(b.suits(pitch));
        b.setBudget(100); // 2 nights at 80 is over budget now
        assertFalse(b.suits(bungalow));
    }

    @Test
    void canFindPlotInAreaById() {
        area.addPlot(bungalow);
        area.addPlot(pitch);
        assertEquals(pitch, area.getPlot("p1"));
        assertNull(area.getPlot("X9"));
        assertTrue(area.removePlot(pitch));
        assertEquals(1, area.getPlots().size());
    }
}
