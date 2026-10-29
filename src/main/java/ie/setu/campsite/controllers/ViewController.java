package ie.setu.campsite.controllers;

import ie.setu.campsite.models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

import static ie.setu.campsite.CampsiteApp.manager;

// pending bookings report + the drill down tree
public class ViewController implements Initializable {

    public static ViewController viewControl;

    @FXML
    private ChoiceBox<Campsite> campsiteChoice;

    @FXML
    private TextArea output, details;

    @FXML
    private TreeView<Object> drillDown;

    public void showCampsiteBookings(ActionEvent actionEvent) {
        if (campsiteChoice.getValue() == null) {
            Util.error("Pick a campsite first");
            return;
        }
        output.setText(manager.pendingBookings(campsiteChoice.getValue()));
    }

    public void showAllBookings(ActionEvent actionEvent) {
        output.setText(manager.allPendingBookings());
    }

    // builds the tree straight from the data: campsite > area > plot > booking
    private void buildTree() {
        TreeItem<Object> root = new TreeItem<>("All campsites");
        root.setExpanded(true);
        for (Campsite c : manager.getCampsites()) {
            TreeItem<Object> campsiteItem = new TreeItem<>(c);
            for (CampingArea a : c.getAreas()) {
                TreeItem<Object> areaItem = new TreeItem<>(a);
                for (Plot p : a.getPlots()) {
                    TreeItem<Object> plotItem = new TreeItem<>(p);
                    for (Booking b : p.getBookings()) {
                        plotItem.getChildren().add(new TreeItem<>(b));
                    }
                    areaItem.getChildren().add(plotItem);
                }
                campsiteItem.getChildren().add(areaItem);
            }
            root.getChildren().add(campsiteItem);
        }
        drillDown.setRoot(root);
    }

    // shows more info about whatever's clicked in the tree
    public void itemClicked() {
        TreeItem<Object> item = drillDown.getSelectionModel().getSelectedItem();
        if (item == null) return;
        Object o = item.getValue();

        if (o instanceof Campsite) {
            Campsite c = (Campsite) o;
            details.setText(c.getName() + "\n" + c.getAddress() + "\nStars: " + c.getStars()
                    + "\nContact: " + c.getContact() + "\nFacilities: " + c.getFacilities()
                    + "\nAreas: " + c.getAreas().size());
        } else if (o instanceof CampingArea) {
            CampingArea a = (CampingArea) o;
            details.setText(a.getId() + "\n" + a.getDescription() + "\nPlots: " + a.getPlots().size());
        } else if (o instanceof Plot) {
            Plot p = (Plot) o;
            details.setText(p.getId() + " - " + p.getType()
                    + "\nPower: " + (p.isPowered() ? "yes" : "no")
                    + "\nBedrooms: " + p.getBedrooms() + ", beds: " + p.getBeds()
                    + "\nSize: " + p.getSize() + "m²\nCost: €" + p.getCostPerNight() + " a night"
                    + "\nFacilities: " + p.getFacilities() + "\nPhoto: " + p.getPhotoUrl()
                    + "\nBookings: " + p.getBookings().size());
        } else if (o instanceof Booking) {
            Booking b = (Booking) o;
            details.setText("Guest: " + b.getGuest() + "\n" + b.getStart() + " to " + b.getEnd()
                    + " (" + b.getNights() + " nights)\nGuests: " + b.getNumGuests()
                    + "\nCost: €" + String.format("%.2f", b.getTotalCost())
                    + "\n" + (b.isPending() ? "Pending" : "Finished"));
        } else {
            details.clear();
        }
    }

    public void refresh() {
        Util.fill(campsiteChoice, manager.getCampsites());
        buildTree();
        output.clear();
        details.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewControl = this;
        refresh();
    }
}
