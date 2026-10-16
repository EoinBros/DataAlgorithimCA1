package ie.setu.campsite.system;

import ie.setu.campsite.models.CampingArea;
import ie.setu.campsite.models.Campsite;
import ie.setu.campsite.models.Plot;

import java.time.LocalDate;

// holds what smart find came up with so the GUI can show it / book it
public class SmartFindResult {
    private final Campsite campsite;
    private final CampingArea area;
    private final Plot plot;
    private final LocalDate start;
    private final LocalDate end;

    public SmartFindResult(Campsite campsite, CampingArea area, Plot plot, LocalDate start, LocalDate end) {
        this.campsite = campsite;
        this.area = area;
        this.plot = plot;
        this.start = start;
        this.end = end;
    }

    public Campsite getCampsite() { return campsite; }
    public CampingArea getArea() { return area; }
    public Plot getPlot() { return plot; }
    public LocalDate getStart() { return start; }
    public LocalDate getEnd() { return end; }

    @Override
    public String toString() {
        return campsite.getName() + " > " + area.getId() + " > " + plot.getId()
                + " from " + start + " to " + end;
    }
}
