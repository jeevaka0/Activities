import javafx.scene.Node;
import javafx.scene.layout.HBox;

/*
 * Created by Jeevaka on 9/3/17.
 */
public class HBoxPlus extends HBox {

    public void add( Node child) {
        getChildren().add(child);
    }

}
