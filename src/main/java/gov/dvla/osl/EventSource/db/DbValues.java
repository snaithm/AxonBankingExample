package gov.dvla.osl.EventSource.db;

import com.mongodb.MongoClient;

public class DbValues {

    public static MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
    public static String eventDBName = "axonBankingEvents";
    public static String readModelDBName = "axonBankingReadModel";
    public static String collectionName = "accounts";
    public static String snapshotEventsCollectionName = "snapshotevents";
}
