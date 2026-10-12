module ie.setu.campsite {
    requires javafx.controls;
    requires javafx.fxml;

    opens ie.setu.campsite to javafx.fxml;
    exports ie.setu.campsite;
}
