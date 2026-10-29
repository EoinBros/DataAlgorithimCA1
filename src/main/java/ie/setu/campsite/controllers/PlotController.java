package ie.setu.campsite.controllers;

import ie.setu.campsite.datastructures.MyLinkedList;
import ie.setu.campsite.models.Booking;
import ie.setu.campsite.models.CampingArea;
import ie.setu.campsite.models.Campsite;
import ie.setu.campsite.models.Plot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static ie.setu.campsite.CampsiteApp.manager;

// adding and removing plots/pitches
public class PlotController implements Initializable {

    public static PlotController plotControl;

    @FXML
    private ChoiceBox<Campsite> campsiteChoice;

    @FXML
    private ChoiceBox<CampingArea> areaChoice;

    @FXML
    private ChoiceBox<String> typeChoice;

    @FXML
    private TextField plotId, bedrooms, beds, size, cost, photoUrl, facilities;

    @FXML
    private CheckBox powered;

    @FXML
    private ListView<Plot> plotList;

    public void addPlot(ActionEvent actionEvent) {
        CampingArea area = areaChoice.getValue();
        if (area == null) {
            Util.error("Pick a campsite and area first");
            return;
        }
        if (Util.isBlank(plotId) || typeChoice.getValue() == null) {
            Util.error("The plot needs an identifier and a type");
            return;
        }
        Double plotSize = Util.readDouble(size, "Size");
        if (plotSize == null) return;
        Double costPerNight = Util.readDouble(cost, "Cost per night");
        if (costPerNight == null) return;

        // bedrooms and beds only matter for statics, so pitches can leave them blank
        int rooms = 0;
        int bedCount = 0;
        boolean isPitch = typeChoice.getValue().toLowerCase().contains("pitch");
        if (!isPitch) {
            Integer r = Util.readInt(bedrooms, "Bedrooms");
            if (r == null) return;
            Integer b = Util.readInt(beds, "Beds");
            if (b == null) return;
            rooms = r;
            bedCount = b;
        }
        if (plotSize <= 0 || costPerNight < 0) {
            Util.error("Size has to be more than 0 and cost can't be negative");
            return;
        }

        Plot plot = new Plot(plotId.getText().trim(), typeChoice.getValue(), powered.isSelected(),
                rooms, bedCount, plotSize, costPerNight, photoUrl.getText().trim());
        for (String f : Util.readList(facilities.getText())) {
            plot.addFacility(f);
        }
        if (!manager.addPlot(area, plot)) {
            Util.error(area.getId() + " already has a plot called " + plot.getId());
            return;
        }
        plotId.clear();
        bedrooms.clear();
        beds.clear();
        size.clear();
        cost.clear();
        photoUrl.clear();
        facilities.clear();
        powered.setSelected(false);
        Util.refreshAll();
    }

    // any pending bookings get moved to another plot in the campsite if possible
    public void removePlot(ActionEvent actionEvent) {
        Plot plot = plotList.getSelectionModel().getSelectedItem();
        if (plot == null) {
            Util.error("Pick a plot from the list to remove");
            return;
        }
        if (!Util.confirm("Remove plot " + plot.getId() + "?")) {
            return;
        }
        MyLinkedList<Booking> notMoved = manager.removePlot(campsiteChoice.getValue(), areaChoice.getValue(), plot);
        if (notMoved.isEmpty()) {
            Util.info("Plot removed. Any pending bookings were moved to other plots.");
        } else {
            Util.info("Plot removed, but these bookings couldn't be moved and were cancelled:\n" + notMoved);
        }
        Util.refreshAll();
    }

    public void showAreas(ActionEvent actionEvent) {
        Campsite campsite = campsiteChoice.getValue();
        if (campsite == null) {
            areaChoice.getItems().clear();
        } else {
            Util.fill(areaChoice, campsite.getAreas());
        }
        showPlots(null);
    }

    public void showPlots(ActionEvent actionEvent) {
        plotList.getItems().clear();
        CampingArea area = areaChoice.getValue();
        if (area != null) {
            for (Plot p : area.getPlots()) {
                plotList.getItems().add(p);
            }
        }
    }

    public void refresh() {
        Util.fill(campsiteChoice, manager.getCampsites());
        showAreas(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        plotControl = this;
        typeChoice.getItems().addAll("Static mobile home", "Bungalow", "Apartment", "Empty pitch");
        refresh();
    }
}
