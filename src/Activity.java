import javafx.beans.property.*;

import java.text.*;
import java.time.Duration;
import java.util.*;

public class Activity {
    protected StringProperty category = new SimpleStringProperty();
    protected StringProperty activity = new SimpleStringProperty();
    protected DoubleProperty frequency = new SimpleDoubleProperty();
    protected ReadOnlyStringWrapper last = new ReadOnlyStringWrapper();
    protected ReadOnlyStringWrapper next = new ReadOnlyStringWrapper();
    protected Date lastDate;
    protected Date nextDate;

    protected static final int period = 12;
    protected static final long minutesInPeriod = Duration.ofDays( 7 ).toMinutes() * period;

    protected static String Delimiter = "|";
    protected static String EscDelimiter = "\\|";

    protected static SimpleDateFormat parserTemp = new SimpleDateFormat("EEE MM/dd/yyyy");
    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
    protected static SimpleDateFormat noTimeFormat = new SimpleDateFormat( "MM/dd/yyyy" );


    static class ActivityComparator implements Comparator<Activity> {
        @Override
        public int compare( Activity a, Activity b ) {
            return a.nextDate.compareTo( b.nextDate );
        }

        @Override
        public boolean equals( Object o ) {
            return false;
        }
    }



    public Activity( String l ) {
        try {
            String[] parts = l.split( EscDelimiter );
            category.setValue( parts[0] );
            activity.setValue( parts[1] );
            frequency.setValue( Double.parseDouble( parts[2] ) );
            lastDate = dateFormat.parse( parts[3] );
            nextDate = dateFormat.parse( parts[4] );
            updateDates();
        } catch ( ParseException e ) {
            throw new RuntimeException( e.toString() );
        } catch ( NumberFormatException e ) {
            throw new RuntimeException( e.toString() );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            throw new RuntimeException( e.toString() );
        }
    }


    public String toString() {
        return category.get() + Delimiter + activity.get() + Delimiter + frequency.get()
                + Delimiter + dateFormat.format( lastDate )
                + Delimiter + dateFormat.format( nextDate );
    }


    public boolean isOverdue() {
        return nextDate.before( new Date() );
    }


    public void done() {
        Calendar calendar = Calendar.getInstance();
        lastDate = calendar.getTime();
        if ( nextDate.after( lastDate ) ) {
            lastDate = nextDate;
        }
        calendar.setTime( lastDate );

        long minutesToPush = (long)( minutesInPeriod / frequency.get() );
        calendar.add( Calendar.MINUTE, (int)minutesToPush );
        nextDate = calendar.getTime();

        updateDates();
    }


    public void bump( int i ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( nextDate );
        calendar.add( Calendar.DATE, i );
        nextDate = calendar.getTime();
        updateDates();
    }


    public void randomize() {
        long minutesPerOne = (long)( minutesInPeriod / frequency.get() );         // Should fall in next this many minutes.
        Random r = new Random();
        long minutesToAdd = Math.abs( r.nextLong() ) % minutesPerOne;   // Little biased to be closer to now. That is ok.
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.MINUTE, (int)minutesToAdd );
        nextDate = calendar.getTime();
        updateDates();
    }


    public static ActivityComparator getComparator() {
        return new ActivityComparator();
    }


    protected void updateDates() {
        last.set( noTimeFormat.format( lastDate ) );
        next.set( noTimeFormat.format( nextDate ) );
    }


    // All the silly methods javafx needs, but should figure out on its own.

    @SuppressWarnings("unused")
    public StringProperty categoryProperty() {
        return category;
    }


    @SuppressWarnings("unused")
    public StringProperty activityProperty() {
        return activity;
    }


    @SuppressWarnings("unused")
    public DoubleProperty frequencyProperty() {
        return frequency;
    }


    @SuppressWarnings("unused")
    public ReadOnlyStringProperty nextProperty() {
        return next.getReadOnlyProperty();
    }


    @SuppressWarnings("unused")
    public ReadOnlyStringProperty lastProperty() {
        return last.getReadOnlyProperty();
    }
}
