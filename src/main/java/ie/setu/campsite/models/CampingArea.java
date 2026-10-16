package ie.setu.campsite.models;

import java.io.Serializable;

import ie.setu.campsite.datastructures.MyLinkedList;

// an area inside a campsite e.g. "Lake View Area 1" - holds the plots
public class CampingArea implements Serializable {
    private String id;
    private String description;
    private MyLinkedList<Plot> plots = new MyLinkedList<>();

    public CampingArea(String id, String description) {
        this.id = id;
        this.description = description;
    }

    // same idea as areas, plot ids shouldn't repeat in one area
    public boolean addPlot(Plot plot) {
        if (getPlot(plot.getId()) != null) {
            return false;
        }
        plots.add(plot);
        return true;
    }

    public Plot getPlot(String id) {
        for (Plot p : plots) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    public boolean removePlot(Plot plot) {
        return plots.remove(plot);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public MyLinkedList<Plot> getPlots() { return plots; }

    @Override
    public String toString() {
        return id + " - " + description;
    }
}
