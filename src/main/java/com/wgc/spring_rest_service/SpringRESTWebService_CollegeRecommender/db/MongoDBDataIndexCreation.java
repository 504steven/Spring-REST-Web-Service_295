package com.wgc.spring_rest_service.SpringRESTWebService_CollegeRecommender.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.wgc.spring_rest_service.SpringRESTWebService_CollegeRecommender.config.DBConfig.*;


public class MongoDBDataIndexCreation {
    private static String fileName = "E:/__BackUp/__Ppriva/CollegeUniversity_Material/SJSU_Software/_____295_MasterProject/college_data_cleaned.csv";
    private static String[] filedNames = {"id", "name", "loc_", "loc", "state", "control", "urbanization", "religious_affiliation", "offers_associate_degree", "offers_bachelor_degree", "offers_master_degree", "offers_doctor_degree_research_scholarship", "offers_doctor_degree_professional_practice", "applicants_total", "admissions_total", "enrolled_total", "percent_admitted_total", "total_enrollment", "undergraduate_total_enrollment", "graduate_total_enrollment"
            , "tuition_and_fees", "sat_reading_25th_percentile_score", "sat_reading_75th_percentile_score", "sat_math_25th_percentile_score", "sat_math_75th_percentile_score", "sat_writing_25th_percentile_score", "sat_writing_75th_percentile_score", "act_25th_percentile_score", "act_75th_percentile_score"};

    public static void main(String[] args) {
        MongoClientURI mongoClientURI = new MongoClientURI("mongodb://"+ MONGODB_USERNAME +":"
                                            + MONGODB_PASSWORD + "@"+ MONGODB_IP +":"+ MONGODB_PORT+"/" + MONGODB_DB_NAME);
        MongoClient mongoClient = new MongoClient( mongoClientURI);
        MongoDatabase db = mongoClient.getDatabase(MONGODB_DB_NAME);
        db.getCollection(COLLEGE_COLLECTION).drop();

        // load data
        String line = null;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            boolean headerline = true;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                Document doc = new Document();
                for (int i = 0; i < filedNames.length; i++) {
                    if (headerline) {
//                        doc.append(filedNames[i], values[i]);
                    } else {
                        if (i == 0) {
                            doc.append(filedNames[i], Integer.parseInt(values[i]) );
                        } else if (i == 2) {
                            continue;
                        } else if (i == 3) {
                            List<Double> coords = new LinkedList<>();
                            coords.add(Double.parseDouble(values[i - 1]));
                            coords.add(Double.parseDouble(values[i]));
                            Document locDoc = new Document().append("coordinates", coords).append("type", "Point");
                            doc.append(filedNames[i], locDoc);
                        } else if (i >= 8 && i <= 12) {
                            doc.append(filedNames[i], !values[i].equals("no"));
                        } else if (i > 12) {
                            if (values[i].equals("")) {
                                values[i] = "0";
                            }
                            doc.append(filedNames[i], Integer.parseInt(values[i]));
                        } else {
                            doc.append(filedNames[i], values[i]);
                        }
                    }
                }

                db.getCollection(COLLEGE_COLLECTION).insertOne(doc);
                headerline = false;
            }
            bufferedReader.close();
            System.out.println("--------------  Data Import Done!  ------------");


            // create db index
            IndexOptions uniqueIndex = new IndexOptions().unique(true);
            db.getCollection(COLLEGE_COLLECTION).createIndex(Indexes.geo2dsphere("loc"), uniqueIndex);
            db.getCollection(COLLEGE_COLLECTION).createIndex(Indexes.text("name"));
            db.getCollection(COLLEGE_COLLECTION).createIndex(new Document("id", 1), uniqueIndex);
//            db.getCollection(COLLEGE_COLLECTION).createIndex(new Document("name", 1), uniqueIndex);
            db.getCollection(COLLEGE_COLLECTION).createIndex(new Document("state", 1));
            db.getCollection(COLLEGE_COLLECTION).createIndex(new Document("control", 1));
            db.getCollection(COLLEGE_COLLECTION).createIndex(new Document("urbanization", 1));
            db.getCollection(COLLEGE_COLLECTION).createIndex(new Document("religious_affiliation", 1));
            db.getCollection(COLLEGE_COLLECTION).createIndex(new Document("offers_associate_degree", 1));
//            .append("offers_bachelor_degree",1).append("offers_master_degree",1)
//                   .append("offers_doctor_degree_research_scholarship",1).append("offers_doctor_degree_professional_practice",1));
            db.getCollection(COLLEGE_COLLECTION).createIndex(new Document("tuition_and_fees", 1));
            System.out.println("-------------    Index Created!       -------------");


            // retrieve data for testing
            FindIterable<Document> res = db.getCollection(COLLEGE_COLLECTION).find(new Document().append("state", "alabama"));
            Iterator<Document> itr = res.iterator();
            System.out.println("-------------    get alabama data for testing from MongoDB-" + COLLEGE_COLLECTION + "， found: " + itr.hasNext() );
            while(itr.hasNext()) {
                Document doc = itr.next();
                System.out.println("    " + filedNames[0] + " : " + doc.getInteger(filedNames[0] ));
                System.out.println("    " + filedNames[1] + " : " + doc.getString(filedNames[1] ));
                System.out.println("    " + filedNames[4] + " : " + doc.getString(filedNames[4] ));
                System.out.println("    " + filedNames[13] + " : " + doc.getInteger(filedNames[13] ));
            }

            mongoClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
