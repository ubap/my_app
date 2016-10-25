package pg.eti.inz.eti.engineer.data;

public class DbManager {
    static DbManager instance;

    TripContainer trips;

    public static DbManager getInstance() {
        if(instance == null) {
            instance = new DbManager();
        }
        return instance;
    }


    public DbManager() {
        trips = new TripContainer();
    }

    public TripContainer getTripContainer() {
        return trips;
    }
}
