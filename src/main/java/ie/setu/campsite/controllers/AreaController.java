package ie.setu.campsite.controllers;

import ie.setu.campsite.models.CampingArea;
import ie.setu.campsite.models.Campsite;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static ie.setu.campsite.CampsiteApp.manager;

// adding camping areas to a campsite
public class AreaController implements Initializable {

    public static AreaController areaControl;

    @FXML
    private ChoiceBox<Campsite> campsiteChoice;

    @FXML
    private TextField areaId, description;

    @FXML
    private ListView<CampingArea> areaList;

    public void addArea(ActionEvent actionEvent) {
        Campsite campsite = campsiteChoice.getValue();
        if (campsite == null) {
            Util.error("Pick a campsite first");
            return;
        }
        if (Util.isBlank(areaId)) {
            Util.error("The area needs an identifier");
            return;
        }
        CampingArea area = new CampingArea(areaId.getText().trim(), description.getText().trim());
        // the id has to be unique within this campsite
        if (!manager.addArea(campsite, area)) {
            Util.error(campsite.getName() + " already has an area called " + area.getId());
            return;
        }
        areaId.clear();
        description.clear();
        Util.refreshAll();
    }

    // shows the areas for whichever campsite is picked
    public void showAreas(ActionEvent actionEvent) {
        areaList.getItems().clear();
        Campsite campsite = campsiteChoice.getValue();
        if (campsite != null) {
            for (CampingArea a : campsite.getAreas()) {
                areaList.getItems().add(a);
            }
        }
    }

    public void refresh() {
        Util.fill(campsiteChoice, manager.getCampsites());
        showAreas(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        areaControl = this;
        refresh();
    }
}
