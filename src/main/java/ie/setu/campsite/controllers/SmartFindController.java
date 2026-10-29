package ie.setu.campsite.controllers;

import ie.setu.campsite.models.Booking;
import ie.setu.campsite.models.Guest;
import ie.setu.campsite.system.SmartFindResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static ie.setu.campsite.CampsiteApp.manager;

// smart find - give it what you want and it suggests somewhere
public class SmartFindController implements Initializable {

    public static SmartFindController smartFindControl;

    @FXML
    private TextField numGuests, budget, minStars, facilities;

    @FXML
    private DatePicker startDate, endDate;

    @FXML
    private ChoiceBox<Guest> guestChoice;

    @FXML
    private Label result;

    private SmartFindResult found;

    public void find(ActionEvent actionEvent) {
        found = null;
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        if (start == null || end == null || !end.isAfter(start)) {
            Util.error("Pick dates, and the end has to be after the start");
            return;
        }
        Integer people = Util.readInt(numGuests, "Number of guests");
        if (people == null) return;
        Double money = Util.readDouble(budget, "Budget");
        if (money == null) return;
        int stars = 1;
        if (!Util.isBlank(minStars)) {
            Integer s = Util.readInt(minStars, "Min stars");
            if (s == null) return;
            stars = s;
        }

        found = manager.smartFind(people, money, stars, start, end, Util.readList(facilities.getText()));
        if (found == null) {
            result.setText("Nothing found, try a bigger budget or fewer facilities");
        } else {
            double total = found.getPlot().getCostPerNight() * (end.toEpochDay() - start.toEpochDay());
            String moved = found.getStart().equals(start) ? "" : "\n(your dates were full so this is a bit later)";
            result.setText("Found: " + found + "\n" + found.getPlot() + "\nTotal: €" + String.format("%.2f", total) + moved);
        }
    }

    // books exactly the plot that was found
    public void bookIt(ActionEvent actionEvent) {
        if (found == null) {
            Util.error("Find something first");
            return;
        }
        Guest guest = guestChoice.getValue();
        if (guest == null) {
            Util.error("Pick which guest it's for");
            return;
        }
        Integer people = Util.readInt(numGuests, "Number of guests");
        if (people == null) return;
        Booking booking = new Booking(found.getStart(), found.getEnd(), found.getArea().getId(), guest,
                found.getPlot().getType(), people, false, 0, 0, Double.MAX_VALUE);
        if (manager.bookPlot(booking, found.getPlot())) {
            Util.info("Booked " + found);
            found = null;
            result.setText("");
            Util.refreshAll();
        } else {
            Util.error("That plot's not free any more, try finding again");
        }
    }

    public void refresh() {
        Util.fill(guestChoice, manager.getGuests());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        smartFindControl = this;
        refresh();
    }
}
