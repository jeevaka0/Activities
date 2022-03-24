/*
 * Created by Jeevaka on 8/30/17.
 */


import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;


public class TableViewPlus<E> extends TableView<E> {

    public TableViewPlus( ObservableList<E> list ) {
        super( list );
    }


    public void add( TableColumn column ) {
        getColumns().add( column );
    }


    public <T, U> TableColumn addColumn( String name, double minWidth, double preferredWidth, boolean editable
            , Class<?> uClass, String style ) {
        TableColumn column = new TableColumn<T, U>(name);
        column.setMinWidth( minWidth );
        column.setPrefWidth( preferredWidth );
        column.setStyle( style );
        column.setSortable( false );
        String propertyName = Character.toLowerCase( name.charAt( 0 ) ) + name.substring( 1 );
        column.setCellValueFactory( new PropertyValueFactory<T, U>( propertyName ) );
        if ( editable ) {
            if( uClass == String.class ) {
                column.setCellFactory( TextFieldTableCell.<T>forTableColumn());
            } else if ( uClass == Integer.TYPE ) {
                column.setCellFactory( TextFieldTableCell.<T, Integer>forTableColumn( new IntegerStringConverter() ) );
            } else if ( uClass == Double.TYPE ) {
                column.setCellFactory( TextFieldTableCell.<T, Double>forTableColumn( new DoubleStringConverter() ) );
            }
        }
        add( column );
        return column;
    }
}

