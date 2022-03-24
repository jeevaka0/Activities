import javafx.scene.Node;
import javafx.scene.control.ToolBar;

public class ToolBarPlus extends ToolBar {

    public void add(Node child) {
        getItems().add(child);
    }
}
