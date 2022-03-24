import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.*;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;


public class Activities extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        setStage( primaryStage );
        createToolbar();
        readIn();
        createTable();

        primaryStage.show();

        cleanOldFiles();
    }


    protected void done() {
        //System.out.println( activities );
        writeOut( true );
        tableView.getSelectionModel().getSelectedItem().done();
        writeOut( false );
        tableView.getSelectionModel().selectFirst();
    }


    protected void bump() {
        writeOut( true );
        int i = Integer.parseInt( bumpValue.getText() );
        for ( Activity a : activities.getSource() ) {
            if ( i == -999 ) {
                a.randomize();
                //System.out.println( a.toString() );
            } else {
                a.bump( i );
            }
        }
        tableView.getSelectionModel().selectFirst();
        writeOut( false );
    }


    protected void createTable() {
        tableView = new TableViewPlus( activities );
        tableView.setEditable( true );

        tableView.<Activity, Double>addColumn( "Frequency", 20, 105, true, Double.TYPE, "-fx-alignment: CENTER-RIGHT;" );
        tableView.addColumn( "Last", 100, 120, false, Date.class, "-fx-alignment: CENTER;" );
        tableView.addColumn( "Next", 100, 120, false, Date.class, "-fx-alignment: CENTER;" );
        tableView.<Activity, String>addColumn( "Activity", 200, 795, true, String.class, "" );
        tableView.<Activity, String>addColumn( "Category", 100, 155, true, String.class, "" );

        tableView.getSelectionModel().selectFirst();

        tableView.setRowFactory(tv -> new TableRow<Activity>() {
            @Override
            public void updateItem(Activity item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item != null) {
                    String color = item.isOverdue() ? "#f0c0c0" : "lime";
                    setStyle("-fx-background-color: " + color + ";");
                }
            }
        });

        root.add( tableView );
        root.setVgrow( tableView, Priority.ALWAYS );
    }


    protected void writeOut( boolean backup ) {
        try {
            String backupPart = "";
            if ( backup ) {
                backupPart = backupName + dateFormat.format( new Date() ) + "_";
            }
            Path backupPath = path.resolve( backupPart + fileName );
            List<String> lines = new ArrayList<>();
            for ( Activity a : activities ) {
                lines.add( a.toString() );
            }
            Files.write( backupPath, lines, StandardOpenOption.CREATE );
        } catch ( IOException e ) {
            throw new RuntimeException( e.toString() );
        }
    }

    
    protected void readIn() {
        try {
            List<String> parameters = getParameters().getRaw();
            String filePath = parameters.get( 0 );
            Path file = Paths.get( filePath );

            path = file.getParent();
            fileName = file.getFileName().toString();

            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            // If we keep a file header read it here.

            Callback<Activity,Observable[]> callback
                    = ( Activity activity) -> new Observable[] { activity.nextProperty() };
            ObservableList<Activity> list = FXCollections.observableArrayList( callback );
            for ( String l : lines ) {
                list.add( new Activity( l ) );
            }
            activities = new SortedList<>( list, Activity.getComparator() );
            
        } catch ( IOException e ) {
            throw new RuntimeException( e.toString() );
        }
    }


    protected void cleanOldFiles() {
        File directory = path.resolve( backupName ).toFile();
        ArrayList<File> files = new ArrayList<>( Arrays.asList( directory.listFiles() ) );
        files.sort( (File a, File b ) -> (int)( ( b.lastModified() - a.lastModified() ) / 1000 ) );
        while ( files.size() > 20 ) {
            File lastFile = files.remove( files.size() - 1 );
            lastFile.delete();
        }
    }


    protected void setStage(Stage primaryStage) {
        try {
            primaryStage.setTitle( "Activities" );

            URL imageResource = getClass().getResource( "/origami.png" );
            Image icon = new Image( imageResource.openStream() );
            primaryStage.getIcons().add( icon );

            root = new VBoxPlus();

            URL styleUrl = getClass().getResource( "/styles.css" );
            root.getStylesheets().add( styleUrl.toExternalForm() );
            primaryStage.setScene( new Scene( root, 1300, 960 ) );
        } catch ( Exception e ) {
            throw new RuntimeException( e.toString() );
        }
    }


    protected void createToolbar() {
        ToolBarPlus toolBarPlus = new ToolBarPlus();

        Button sortButton = new Button("Done");
        sortButton.setOnAction( e -> done() );
        sortButton.prefWidthProperty().bind( toolBarPlus.widthProperty().subtract( 320 ) );
        toolBarPlus.add(sortButton);

        Label periodLabel = new Label( "  (Period: " + Integer.toString( Activity.period ) + " wks)  " );
        toolBarPlus.add( periodLabel );

        Button bumpButton = new Button( "Bump" );
        bumpButton.setOnAction( e -> bump() );
        toolBarPlus.add( bumpButton );

        bumpValue = new TextField( "0" );
        bumpValue.setPrefWidth( 48 );
        bumpValue.setTextFormatter(
                new TextFormatter<>(new IntegerStringConverter(), 0
                        , ( TextFormatter.Change c )-> c.getControlNewText().matches( "-?([0-9]*)?") ? c : null ) );
        toolBarPlus.add( bumpValue );

        root.add(toolBarPlus);
    }


    VBoxPlus root;
    SortedList<Activity> activities;
    TableViewPlus<Activity> tableView;
    TextField bumpValue;

    static Path path;
    static String fileName;
    static final String backupName = "Backup/";
    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS");
}
