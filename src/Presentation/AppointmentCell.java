package Presentation;

import Model.AppointmentType;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class AppointmentCell extends ListCell<AppointmentType> {
    private final Rectangle rectangle;
    {
      rectangle = new Rectangle(10, 10);
    }
    @Override
    protected void updateItem(AppointmentType item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            rectangle.setFill(Color.web(item.getColor()));
            setGraphic(rectangle);
            setText(item.getName());
        }
    }
}
