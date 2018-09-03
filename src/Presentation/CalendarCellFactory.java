package Presentation;

import Model.Appointment;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class CalendarCellFactory implements Callback<ListView, ListCell> {
    
    @Override
    public ListCell call(ListView lv) {
        return new CalendarCell();
    }
    
    private class CalendarCell extends ListCell<Object> {
        
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                getStyleClass().clear();
            } else if (item instanceof Appointment) {
                this.setWrapText(true);
                setText(item.toString());
                getStyleClass().add("appointment-" + ((Appointment)item).getType());
            } else if (item instanceof String) {
                setText((String) item);
                getStyleClass().add("text-date");
            } else if (item instanceof Node) {
                setGraphic((Node) item);
                getStyleClass().add("text-date");
            }
        }
    }
}

