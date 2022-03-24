import javafx.scene.*;
import javafx.scene.layout.*;

public class VBoxPlus extends VBox {

    public void add(Node child) {
        getChildren().add(child);
    }
}
