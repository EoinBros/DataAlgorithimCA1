package ie.setu.campsite.controllers;

import ie.setu.campsite.CampsiteApp;
import ie.setu.campsite.models.Campsite;
import ie.setu.campsite.system.CampsiteManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static ie.setu.campsite.CampsiteApp.manager;

// first tab - adding campsites, plus save / load / reset
public class CampsiteController implements Initializable {

    public static CampsiteController campsiteControl;

    @FXML
    private TextField name, address, stars, contact, facilities;

    @FXML
    private ListView<Campsite> campsiteList;

    private final File saveFile = new File("campsites.dat");

    public void addCampsite(ActionEvent actionEvent) {
        if (Util.isBlank(name) || Util.isBlank(address)) {
            Util.error("Name and address can't be empty");
            return;
        }
        Integer starCount = Util.readInt(stars, "Stars");
        if (starCount == null) {
            return;
        }
        if (starCount < 1 || starCount > 5) {
            Util.error("Stars has to be between 1 and 5");
            return;
        }

        Campsite campsite = new Campsite(name.getText().trim(), address.getText().trim(), starCount, contact.getText().trim());
        for (String f : Util.readList(facilities.getText())) {
            campsite.addFacility(f);
        }

        if (!manager.addCampsite(campsite)) {
            Util.error("There's already a campsite called " + campsite.getName());
            return;
        }
        name.clear();
        address.clear();
        stars.clear();
        contact.clear();
        facilities.clear();
        Util.refreshAll();
    }

    public void save(ActionEvent actionEvent) {
        try {
            manager.save(saveFile);
            Util.info("Saved");
        } catch (Exception e) {
            Util.error("Couldn't save: " + e.getMessage());
        }
    }

    public void load(ActionEvent actionEvent) {
        if (!saveFile.exists()) {
            Util.error("Nothing saved yet");
            return;
        }
        try {
            CampsiteApp.manager = CampsiteManager.load(saveFile);
            Util.refreshAll();
            Util.info("Loaded");
        } catch (Exception e) {
            Util.error("Couldn't load: " + e.getMessage());
        }
    }

    // wipes everything, so ask first
    public void reset(ActionEvent actionEvent) {
        if (Util.confirm("This clears all the data. Are you sure?")) {
            manager.reset();
            Util.refreshAll();
        }
    }

    public void refresh() {
        campsiteList.getItems().clear();
        for (Campsite c : manager.getCampsites()) {
            campsiteList.getItems().add(c);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        campsiteControl = this;
        refresh();
    }
}
