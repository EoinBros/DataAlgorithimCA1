package ie.setu.campsite.controllers;

import ie.setu.campsite.models.Guest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static ie.setu.campsite.CampsiteApp.manager;

// adding guests and looking at a guest's record
public class GuestController implements Initializable {

    public static GuestController guestControl;

    @FXML
    private TextField name, contact, notes, searchId;

    @FXML
    private DatePicker dob;

    @FXML
    private ListView<Guest> guestList;

    @FXML
    private TextArea record;

    public void addGuest(ActionEvent actionEvent) {
        if (Util.isBlank(name) || dob.getValue() == null) {
            Util.error("The guest needs a name and date of birth");
            return;
        }
        if (dob.getValue().isAfter(LocalDate.now())) {
            Util.error("Date of birth can't be in the future");
            return;
        }
        Guest g = manager.addGuest(name.getText().trim(), dob.getValue(), contact.getText().trim(), notes.getText().trim());
        Util.info("Added " + g.getName() + " with id " + g.getId());
        name.clear();
        contact.clear();
        notes.clear();
        dob.setValue(null);
        Util.refreshAll();
    }

    // look up by id
    public void viewRecord(ActionEvent actionEvent) {
        Integer id = Util.readInt(searchId, "Guest id");
        if (id != null) {
            record.setText(manager.guestRecord(id));
        }
    }

    // clicking a guest in the list shows their record too
    public void guestClicked() {
        Guest g = guestList.getSelectionModel().getSelectedItem();
        if (g != null) {
            record.setText(manager.guestRecord(g.getId()));
        }
    }

    public void refresh() {
        guestList.getItems().clear();
        for (Guest g : manager.getGuests()) {
            guestList.getItems().add(g);
        }
        record.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        guestControl = this;
        refresh();
    }
}
