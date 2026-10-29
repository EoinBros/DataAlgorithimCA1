module ie.setu.campsite {
    requires javafx.controls;
    requires javafx.fxml;

    opens ie.setu.campsite to javafx.fxml;
    opens ie.setu.campsite.controllers to javafx.fxml;
    exports ie.setu.campsite;
    exports ie.setu.campsite.controllers;
}
