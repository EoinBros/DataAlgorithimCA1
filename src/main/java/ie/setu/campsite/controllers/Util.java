package ie.setu.campsite.controllers;

import ie.setu.campsite.datastructures.MyLinkedList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

// bits that every controller uses - popups, reading numbers, refreshing the tabs
public class Util {

    public static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void info(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static boolean isBlank(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    // returns null (and shows an error) if it's not a proper whole number
    public static Integer readInt(TextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            error(name + " has to be a whole number");
            return null;
        }
    }

    public static Double readDouble(TextField field, String name) {
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException e) {
            error(name + " has to be a number");
            return null;
        }
    }

    // turns "pool, gym, wifi" into a list - done by hand since we can't use arrays (so no split())
    public static MyLinkedList<String> readList(String text) {
        MyLinkedList<String> items = new MyLinkedList<>();
        if (text == null) {
            return items;
        }
        int start = 0;
        while (start <= text.length()) {
            int comma = text.indexOf(',', start);
            if (comma == -1) {
                comma = text.length();
            }
            String item = text.substring(start, comma).trim();
            if (!item.isEmpty()) {
                items.add(item);
            }
            start = comma + 1;
        }
        return items;
    }

    // reloads a choice box but keeps whatever was picked, if it's still there
    public static <T> void fill(ChoiceBox<T> box, MyLinkedList<T> items) {
        T picked = box.getValue();
        box.getItems().clear();
        for (T item : items) {
            box.getItems().add(item);
        }
        if (picked != null && box.getItems().contains(picked)) {
            box.setValue(picked);
        }
    }

    // after anything changes, every tab reloads its lists and choice boxes
    public static void refreshAll() {
        if (CampsiteController.campsiteControl != null) CampsiteController.campsiteControl.refresh();
        if (AreaController.areaControl != null) AreaController.areaControl.refresh();
        if (PlotController.plotControl != null) PlotController.plotControl.refresh();
        if (GuestController.guestControl != null) GuestController.guestControl.refresh();
        if (BookingController.bookingControl != null) BookingController.bookingControl.refresh();
        if (ViewController.viewControl != null) ViewController.viewControl.refresh();
        if (SmartFindController.smartFindControl != null) SmartFindController.smartFindControl.refresh();
    }
}
