package ie.setu.campsite.controllers;

import ie.setu.campsite.models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static ie.setu.campsite.CampsiteApp.manager;

// making and cancelling bookings
public class BookingController implements Initializable {

    public static BookingController bookingControl;

    @FXML
    private ChoiceBox<Campsite> campsiteChoice;

    @FXML
    private ChoiceBox<CampingArea> areaChoice;

    @FXML
    private ChoiceBox<Guest> guestChoice;

    @FXML
    private ChoiceBox<String> typeChoice;

    @FXML
    private DatePicker startDate, endDate;

    @FXML
    private TextField numGuests, minSize, minBedrooms, budget;

    @FXML
    private CheckBox powered;

    @FXML
    private ListView<Booking> bookingList;

    public void makeBooking(ActionEvent actionEvent) {
        Campsite campsite = campsiteChoice.getValue();
        CampingArea area = areaChoice.getValue();
        Guest guest = guestChoice.getValue();
        if (campsite == null || area == null || guest == null || typeChoice.getValue() == null) {
            Util.error("Pick a campsite, area, guest and type");
            return;
        }
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        if (start == null || end == null || !end.isAfter(start)) {
            Util.error("Pick dates, and the end has to be after the start");
            return;
        }
        if (start.isBefore(LocalDate.now())) {
            Util.error("Can't book in the past");
            return;
        }
        Integer people = Util.readInt(numGuests, "Number of guests");
        if (people == null) return;
        Double money = Util.readDouble(budget, "Budget");
        if (money == null) return;

        // min size and bedrooms are optional, blank means no minimum
        double size = 0;
        int rooms = 0;
        if (!Util.isBlank(minSize)) {
            Double s = Util.readDouble(minSize, "Min size");
            if (s == null) return;
            size = s;
        }
        if (!Util.isBlank(minBedrooms)) {
            Integer r = Util.readInt(minBedrooms, "Min bedrooms");
            if (r == null) return;
            rooms = r;
        }

        Booking booking = new Booking(start, end, area.getId(), guest, typeChoice.getValue(),
                people, powered.isSelected(), size, rooms, money);
        if (manager.makeBooking(campsite, booking)) {
            Util.info("Booked into plot " + booking.getPlot().getId() + " for €" + String.format("%.2f", booking.getTotalCost()));
            Util.refreshAll();
        } else {
            Util.error("Sorry, no plot in " + area.getId() + " matches all of that for those dates");
        }
    }

    public void cancelBooking(ActionEvent actionEvent) {
        Booking booking = bookingList.getSelectionModel().getSelectedItem();
        if (booking == null) {
            Util.error("Pick a booking from the list to cancel");
            return;
        }
        if (Util.confirm("Cancel this booking?\n" + booking)) {
            manager.cancelBooking(booking);
            Util.refreshAll();
        }
    }

    public void campsiteChanged(ActionEvent actionEvent) {
        Campsite campsite = campsiteChoice.getValue();
        if (campsite == null) {
            areaChoice.getItems().clear();
        } else {
            Util.fill(areaChoice, campsite.getAreas());
        }
        showBookings();
    }

    // pending bookings in the picked campsite
    private void showBookings() {
        bookingList.getItems().clear();
        Campsite campsite = campsiteChoice.getValue();
        if (campsite == null) return;
        for (CampingArea a : campsite.getAreas()) {
            for (Plot p : a.getPlots()) {
                for (Booking b : p.getBookings()) {
                    if (b.isPending()) {
                        bookingList.getItems().add(b);
                    }
                }
            }
        }
    }

    public void refresh() {
        Util.fill(campsiteChoice, manager.getCampsites());
        Util.fill(guestChoice, manager.getGuests());
        campsiteChanged(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookingControl = this;
        typeChoice.getItems().addAll("Static mobile home", "Bungalow", "Apartment", "Empty pitch");
        refresh();
    }
}
