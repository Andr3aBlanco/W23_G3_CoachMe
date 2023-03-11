package com.bawp.coachme.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CoachMeDB.db" ;
    private static final int DATABASE_VERSION = 1;

    // Table create statements - Selfworkout Features
    private static final String CREATE_SELF_WORKOUT_PLANS_TABLE = "CREATE TABLE selfWorkoutPlans(" +
            "_id TEXT PRIMARY KEY," +
            "title TEXT," +
            "description TEXT," +
            "planPrice FLOAT," +
            "mainGoals TEXT," +
            "daysPerWeek INT," +
            "level TEXT," +
            "duration TEXT," +
            "posterUrlFirestore TEXT" +
            ");";

    private static final String CREATE_SELF_WORKOUT_SESSION_TYPES_TABLE = "CREATE TABLE selfWorkoutSessionTypes(" +
            "_id TEXT PRIMARY KEY," +
            "selfWorkoutPlanId TEXT," +
            "sessionType TEXT," +
            "sessionTypeIconURLFirestore TEXT," +
            "FOREIGN KEY(selfWorkoutPlanId) REFERENCES selfWorkoutPlans(_id)" +
            ");";

    private static final String CREATE_SELF_WORKOUT_PLAN_EXERCISES_TABLE = "CREATE TABLE selfWorkoutPlanExercises(" +
            "_id TEXT PRIMARY KEY," +
            "exerciseName TEXT," +
            "numSets INT," +
            "repetitions TEXT," +
            "restTime TEXT," +
            "selfWorkoutSessionTypeId TEXT," +
            "execiseImageURLFirestore TEXT," +
            "FOREIGN KEY(selfWorkoutSessionTypeId) REFERENCES selfWorkoutPlans(_id)" +
            ");";

    private static final String CREATE_SELF_WORKOUT_PLANS_BY_USER_TABLE = "CREATE TABLE selfWorkoutPlansByUser(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "customerId TEXT," +
            "paymentId TEXT," +
            "paymentDate BIGINT," +
            "requestedDate BIGINT," +
            "status INT," +
            "selfWorkoutPlanId TEXT," +
            "FOREIGN KEY(selfWorkoutPlanId) REFERENCES selfWorkoutSessionTypes(_id)" +
            ");";

    private static final String CREATE_SELF_WORKOUT_SESSIONS_TABLE = "CREATE TABLE selfWorkoutSessions(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "sessionStatus INT," +
            "selfWorkoutPlansByUserId INT," +
            "FOREIGN KEY(selfWorkoutPlansByUserId) REFERENCES selfWorkoutPlansByUser(_id)" +
            ");";

    private static final String CREATE_SELF_WORKOUT_SESSION_LOGS_TABLE = "CREATE TABLE selfWorkoutSessionLogs(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "selfWorkoutSessionId INT," +
            "selfWorkoutPlanExerciseId STRING," +
            "status INT," +
            "FOREIGN KEY(selfWorkoutSessionId) REFERENCES selfWorkoutSessions(_id)," +
            "FOREIGN KEY(selfWorkoutPlanExerciseId) REFERENCES selfWorkoutPlanExercises(_id)" +
            ");";

    private static final String CREATE_APPOINTMENTS_TABLE = "CREATE TABLE appointments(" +
            "_id TEXT PRIMARY KEY," +
            "bookedDate BIGINT," +
            "registeredDate BIGINT," +
            "serviceType TEXT," +
            "status INT," +
            "totalPrice FLOAT," +
            "location TEXT," +
            "trainerId TEXT," +
            "customerId TEXT," +
            "paymentId TEXT," +
            "paymentDate BIGINT" +
            ");";

    private static final String CREATE_TRAINERS_TABLE = "CREATE TABLE trainers(" +
            "_id TEXT PRIMARY KEY," +
            "firstName TEXT," +
            "lastName TEXT," +
            "email TEXT," +
            "latitudeCoord FLOAT," +
            "longitudeCoord FLOAT," +
            "radius INT," +
            "flatPrice FLOAT," +
            "phoneNumber TEXT," +
            "address TEXT" +
            ")";

    public static final String URL_FIRESTORE_SELF_WORKOUT_PLANS_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/selfWorkoutPlans.csv";
    public static final String URL_FIRESTORE_SELF_WORKOUT_SESSION_TYPES_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/selfWorkoutSessionTypes.csv";
    public static final String URL_FIRESTORE_SELF_PLAN_EXERCISES_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/selfWorkoutPlanExercises.csv";
    public static final String URL_FIRESTORE_TRAINER_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/trainers.csv";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public interface OnDatabaseReadyListener {
        void onDatabaseReady();
    }

    private OnDatabaseReadyListener onDatabaseReadyListener;

    public void setOnDatabaseReadyListener(OnDatabaseReadyListener listener) {
        onDatabaseReadyListener = listener;
    }

    private boolean databaseJustCreated = false;

    public boolean isDatabaseJustCreated() {
        return databaseJustCreated;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SELF_WORKOUT_PLANS_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_SESSION_TYPES_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_PLAN_EXERCISES_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_PLANS_BY_USER_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_SESSIONS_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_SESSION_LOGS_TABLE);
        db.execSQL(CREATE_APPOINTMENTS_TABLE);
        db.execSQL(CREATE_TRAINERS_TABLE);
        databaseJustCreated=true;
        //uploadSelfWorkoutPlans();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS selfWorkoutPlans");
        db.execSQL("DROP TABLE IF EXISTS selfWorkoutSessionTypes");
        db.execSQL("DROP TABLE IF EXISTS selfWorkoutPlanExercises");
        db.execSQL("DROP TABLE IF EXISTS selfWorkoutPlansByUser");
        db.execSQL("DROP TABLE IF EXISTS selfWorkoutSessions");
        db.execSQL("DROP TABLE IF EXISTS selfWorkoutSessionLogs");
        db.execSQL("DROP TABLE IF EXISTS appointments");
        db.execSQL("DROP TABLE IF EXISTS trainers");
        onCreate(db);
    }

    /* -------------------------------
    ---------INITIAL DATASET----------
    ------------------------------- */
    public void uploadSelfWorkoutPlans(byte[] bytes){

        String content = null; // Convert byte array to string
        try {
            SQLiteDatabase db = getWritableDatabase();
            content = new String(bytes, "UTF-8");
            CSVReader reader = new CSVReader(new StringReader(content));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                ContentValues selfWorkoutPlanContent = new ContentValues();
                selfWorkoutPlanContent.put("_id",nextLine[0]);
                selfWorkoutPlanContent.put("title",nextLine[1]);
                selfWorkoutPlanContent.put("description",nextLine[2]);
                selfWorkoutPlanContent.put("planPrice",Double.parseDouble(nextLine[3]));
                selfWorkoutPlanContent.put("mainGoals",nextLine[4]);
                selfWorkoutPlanContent.put("level",nextLine[5]);
                selfWorkoutPlanContent.put("duration",nextLine[6]);
                selfWorkoutPlanContent.put("daysPerWeek",Integer.parseInt(nextLine[7]));
                selfWorkoutPlanContent.put("posterUrlFirestore",nextLine[8]);
                db.insert("selfWorkoutPlans", null, selfWorkoutPlanContent);

            }

            db.close();

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadSelfWorkoutSessionTypes(byte[] bytes){

        String content = null; // Convert byte array to string
        try {
            SQLiteDatabase db = getWritableDatabase();
            content = new String(bytes, "UTF-8");
            CSVReader reader = new CSVReader(new StringReader(content));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                ContentValues selfWorkoutSessionTypeContent = new ContentValues();
                selfWorkoutSessionTypeContent.put("_id",nextLine[0]);
                selfWorkoutSessionTypeContent.put("selfWorkoutPlanId",nextLine[1]);
                selfWorkoutSessionTypeContent.put("sessionType",nextLine[2]);
                selfWorkoutSessionTypeContent.put("sessionTypeIconURLFirestore",nextLine[3]);
                db.insert("selfWorkoutSessionTypes", null, selfWorkoutSessionTypeContent);

            }

            db.close();

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadSelfWorkoutPlanExercises(byte[] bytes){

        String content = null; // Convert byte array to string
        try {
            SQLiteDatabase db = getWritableDatabase();
            content = new String(bytes, "UTF-8");
            CSVReader reader = new CSVReader(new StringReader(content));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                ContentValues selfWorkoutExerciseContent = new ContentValues();
                selfWorkoutExerciseContent.put("_id",nextLine[0]);
                selfWorkoutExerciseContent.put("exerciseName",nextLine[1]);
                selfWorkoutExerciseContent.put("numSets",Integer.parseInt(nextLine[2]));
                selfWorkoutExerciseContent.put("repetitions",nextLine[3]);
                selfWorkoutExerciseContent.put("restTime",nextLine[4]);
                selfWorkoutExerciseContent.put("selfWorkoutSessionTypeId",nextLine[5]);
                selfWorkoutExerciseContent.put("execiseImageURLFirestore",nextLine[6]);
                db.insert("selfWorkoutPlanExercises", null, selfWorkoutExerciseContent);

            }

            db.close();

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void uploadTrainers(byte[] bytes){

        String content = null; // Convert byte array to string
        try {
            SQLiteDatabase db = getWritableDatabase();
            content = new String(bytes, "UTF-8");
            CSVReader reader = new CSVReader(new StringReader(content));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                ContentValues trainersContent = new ContentValues();
                trainersContent.put("_id",nextLine[0]);
                trainersContent.put("firstName",nextLine[1]);
                trainersContent.put("lastName",nextLine[2]);
                trainersContent.put("email",nextLine[3]);
                trainersContent.put("latitudeCoord",Double.parseDouble(nextLine[4]));
                trainersContent.put("longitudeCoord",Double.parseDouble(nextLine[5]));
                trainersContent.put("radius",Integer.parseInt(nextLine[6]));
                trainersContent.put("flatPrice",Double.parseDouble(nextLine[7]));
                trainersContent.put("phoneNumber",nextLine[8]);
                trainersContent.put("address",nextLine[9]);

                db.insert("trainers", null, trainersContent);

            }

            db.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    public void uploadSampleAppointment(){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues appointment = new ContentValues();
        appointment.put("_id","-NP98EOJq3hl7GMR4GHE");
        appointment.put("bookedDate",1677354923545L);
        appointment.put("location","Planet Fitness Royal City");
        appointment.put("paymentDate",(String)null);
        appointment.put("paymentId",(String)null);
        appointment.put("registeredDate",1677354923545L);
        appointment.put("serviceType","Cycling");
        appointment.put("status",1);
        appointment.put("totalPrice",100.99);
        appointment.put("trainerId","TRAIN-0001");
        appointment.put("customerId", UserSingleton.getInstance().getUserId());

        db.insert("appointments", null, appointment);

        db.close();
    }

    public void uploadSampleWorkoutPlanByUser(){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues swpByUser = new ContentValues();

        swpByUser.put("customerId",UserSingleton.getInstance().getUserId());
        swpByUser.put("paymentDate", (Long)null);
        swpByUser.put("paymentId",(String)null);
        swpByUser.put("requestedDate",1678327577120L);
        swpByUser.put("selfworkoutplanId","SWP-0001");
        swpByUser.put("status", 1);

        db.insert("selfWorkoutPlansByUser", null, swpByUser);

        db.close();
    }

    /* -------------------------------
    -----------APPOINTMENTS----------
    ------------------------------- */

    @SuppressLint("Range")
    public List<Appointment> getAppointmentsByStatus(int status){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM appointments WHERE status = " + Integer.toString(status),null );

        List<Appointment> appointmentsList = new ArrayList<>();

        if (cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                Long bookedDate = cursor.getLong(cursor.getColumnIndex("bookedDate"));
                Long registeredDate = cursor.getLong(cursor.getColumnIndex("registeredDate"));
                String serviceType = cursor.getString(cursor.getColumnIndex("serviceType"));
                Double totalPrice = cursor.getDouble(cursor.getColumnIndex("totalPrice"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                String trainerId = cursor.getString(cursor.getColumnIndex("trainerId"));
                String paymentId = cursor.getString(cursor.getColumnIndex("paymentId"));
                Long paymentDate = cursor.getLong(cursor.getColumnIndex("paymentDate"));

                Appointment appointment = new Appointment(id,bookedDate,registeredDate,serviceType,status,
                        totalPrice,location,trainerId,UserSingleton.getInstance().getUserId());

                if (paymentId == null){
                    appointment.setPaymentId(null);
                }else{
                    appointment.setPaymentId(paymentId);
                }

                if (paymentDate == null){
                    appointment.setPaymentDate(0);
                }else{
                    appointment.setPaymentDate(paymentDate);
                }

                appointmentsList.add(appointment);

            }while(cursor.moveToNext());
        }
        db.close();
        return appointmentsList;

    }

    @SuppressLint("Range")
    public List<Appointment> getAppointmentsByStatusList(int[] status){
        SQLiteDatabase db = getReadableDatabase();
        String whereClause = "status IN (" + TextUtils.join(",", Collections.nCopies(status.length, "?")) + ")";
        String[] whereArgs = new String[status.length];
        for (int i = 0; i < status.length; i++) {
            whereArgs[i] = String.valueOf(status[i]);
        }
        Cursor cursor = db.query("appointments", null, whereClause, whereArgs, null, null, null);

        List<Appointment> appointmentsList = new ArrayList<>();

        if (cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                Long bookedDate = cursor.getLong(cursor.getColumnIndex("bookedDate"));
                Long registeredDate = cursor.getLong(cursor.getColumnIndex("registeredDate"));
                int statusValue = cursor.getInt(cursor.getColumnIndex("status"));
                String serviceType = cursor.getString(cursor.getColumnIndex("serviceType"));
                Double totalPrice = cursor.getDouble(cursor.getColumnIndex("totalPrice"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                String trainerId = cursor.getString(cursor.getColumnIndex("trainerId"));
                String paymentId = cursor.getString(cursor.getColumnIndex("paymentId"));
                Long paymentDate = cursor.getLong(cursor.getColumnIndex("paymentDate"));

                Appointment appointment = new Appointment(id,bookedDate,registeredDate,serviceType,statusValue,
                        totalPrice,location,trainerId,UserSingleton.getInstance().getUserId());

                if (paymentId == null){
                    appointment.setPaymentId(null);
                }else{
                    appointment.setPaymentId(paymentId);
                }

                if (paymentDate == null){
                    appointment.setPaymentDate(0);
                }else{
                    appointment.setPaymentDate(paymentDate);
                }

                appointmentsList.add(appointment);

            }while(cursor.moveToNext());
        }
        db.close();
        return appointmentsList;

    }

    public int updateAppointmentStatus(String appointmentId, int status){
        SQLiteDatabase db = getWritableDatabase();
        // Create a ContentValues object with the new value of the "status" field
        ContentValues values = new ContentValues();
        values.put("status", status);

        // Update the appointments table with the new value of the "status" field
        int numRowsUpdated = db.update("appointments", values, "_id=?", new String[] {appointmentId});

        // Close the database connection
        db.close();

        return numRowsUpdated;

    }

    public int updateAppointmentAddPayment(String appointmentId, String paymentId, Date paymentDate){
        SQLiteDatabase db = getWritableDatabase();
        // Create a ContentValues object with the new value of the "status" field
        ContentValues values = new ContentValues();
        values.put("status", 3);
        values.put("paymentId", paymentId);
        values.put("paymentDate", paymentDate.getTime());

        // Update the appointments table with the new value of the "status" field
        int numRowsUpdated = db.update("appointments", values, "_id=?", new String[] {appointmentId});

        // Close the database connection
        db.close();

        return numRowsUpdated;
    }

    /* -------------------------------
    -----------SELF WORKOUT----------
    ------------------------------- */
    @SuppressLint("Range")
    public List<SelfWorkoutPlanByUser> getSelfWorkoutPlanByUserByStatus(int status){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT \n" +
                "su._id as selfWorkoutPlanByUserId, su.paymentId, su.paymentDate, su.requestedDate, su.status,\n" +
                " sp._id as selfWorkoutPlanId, sp.title,  sp.description, sp.planPrice, sp.mainGoals, sp.daysPerWeek,\n" +
                " sp.level,  sp.duration, sp.posterUrlFirestore\n" +
                "FROM  selfWorkoutPlansByUser su \n" +
                "JOIN selfWorkoutPlans sp \n" +
                "ON su.selfWorkoutPlanId = sp._id \n"+
                "WHERE su.status = "+status,null);

        List<SelfWorkoutPlanByUser> selfWorkoutPlanByUsersList = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{

                Integer selfWorkoutPlanByUserId = cursor.getInt(cursor.getColumnIndex("selfWorkoutPlanByUserId"));
                Long requestedDate = cursor.getLong(cursor.getColumnIndex("requestedDate"));
                Long paymentDate = cursor.getLong(cursor.getColumnIndex("paymentDate"));
                String paymentId = cursor.getString(cursor.getColumnIndex("paymentId"));

                String selfWorkoutPlanId = cursor.getString(cursor.getColumnIndex("selfWorkoutPlanId"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String description= cursor.getString(cursor.getColumnIndex("description"));
                Double planPrice = cursor.getDouble(cursor.getColumnIndex("planPrice"));
                String mainGoals = cursor.getString(cursor.getColumnIndex("mainGoals"));
                String level = cursor.getString(cursor.getColumnIndex("level"));
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                Integer daysPerWeek = cursor.getInt(cursor.getColumnIndex("daysPerWeek"));
                String posterUrlFirestore = cursor.getString(cursor.getColumnIndex("posterUrlFirestore"));

                SelfWorkoutPlan selfWorkoutPlan = new SelfWorkoutPlan(selfWorkoutPlanId, title, description,
                 planPrice, posterUrlFirestore, mainGoals, duration, daysPerWeek, level);

                SelfWorkoutPlanByUser selfWorkoutPlanByUser = new SelfWorkoutPlanByUser(selfWorkoutPlanByUserId,
                selfWorkoutPlan, new Date(requestedDate), status, new Date(paymentDate), paymentId);

                selfWorkoutPlanByUsersList.add(selfWorkoutPlanByUser);

            }while (cursor.moveToNext());
        }

        db.close();
        return selfWorkoutPlanByUsersList;
    }

    public int updateSelfWorkoutPlanByUserStatus(String selfWorkoutPlanByUserId, int status){
        SQLiteDatabase db = getWritableDatabase();
        // Create a ContentValues object with the new value of the "status" field
        ContentValues values = new ContentValues();
        values.put("status", status);

        // Update the appointments table with the new value of the "status" field
        int numRowsUpdated = db.update("selfWorkoutPlansByUser", values, "_id=?", new String[] {selfWorkoutPlanByUserId});

        // Close the database connection
        db.close();

        return numRowsUpdated;

    }

    public int updateSelfWorkoutAddPayment(String selfWorkoutPlanByUserId, String paymentId, Date paymentDate){
        SQLiteDatabase db = getWritableDatabase();
        // Create a ContentValues object with the new value of the "status" field
        ContentValues values = new ContentValues();
        values.put("status", 3);
        values.put("paymentId", paymentId);
        values.put("paymentDate", paymentDate.getTime());

        // Update the appointments table with the new value of the "status" field
        int numRowsUpdated = db.update("selfWorkoutPlansByUser", values, "_id=?", new String[] {selfWorkoutPlanByUserId});

        // Close the database connection
        db.close();

        return numRowsUpdated;
    }

    /* -------------------------------
    --------------TRAINER-------------
    ------------------------------- */

    @SuppressLint("Range")
    public Trainer getTrainerById(String trainerId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * \n" +
                "FROM trainers \n"+
                "WHERE _id = '"+trainerId+"'",null);

        Trainer trainer = null;

        if(cursor.moveToFirst()){
            String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
            String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            Double latitudeCoord = cursor.getDouble(cursor.getColumnIndex("latitudeCoord"));
            Double longitudeCoord = cursor.getDouble(cursor.getColumnIndex("longitudeCoord"));
            Integer radius = cursor.getInt(cursor.getColumnIndex("radius"));
            Double flatPrice = cursor.getDouble(cursor.getColumnIndex("flatPrice"));
            String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
            String address = cursor.getString(cursor.getColumnIndex("address"));

            trainer = new Trainer(trainerId, firstName, lastName, email,
                    latitudeCoord, longitudeCoord,  radius, flatPrice,
                     phoneNumber, address);

        }

        db.close();
        return trainer;
    }

}
