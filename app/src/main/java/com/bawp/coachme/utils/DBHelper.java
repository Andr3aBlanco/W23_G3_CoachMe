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
import com.bawp.coachme.model.Payment;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.SelfWorkoutPlanExercise;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
            "exerciseImageURLFirestore TEXT," +
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
            "sessionDate BIGINT,"+
            "status INT,"+
            "selfWorkoutPlansByUserId INT," +
            "selfWorkoutSessionTypeId TEXT," +
            "FOREIGN KEY(selfWorkoutPlansByUserId) REFERENCES selfWorkoutPlansByUser(_id)," +
            "FOREIGN KEY(selfWorkoutSessionTypeId) REFERENCES selfWorkoutSessionTypes(_id)" +
            ");";

    private static final String CREATE_SELF_WORKOUT_SESSION_LOGS_TABLE = "CREATE TABLE selfWorkoutSessionLogs(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "selfWorkoutSessionId INT," +
            "selfWorkoutPlanExerciseId STRING," +
            "status INT," +
            "FOREIGN KEY(selfWorkoutSessionId) REFERENCES selfWorkoutSessions(_id)," +
            "FOREIGN KEY(selfWorkoutPlanExerciseId) REFERENCES selfWorkoutPlanExercises(_id)" +
            ");";

    // Changed this to match the Appointment model
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
            "paymentDate BIGINT," +
            "deviceToken TEXT," +
            "rating INT," +
            "comment TEXT" +
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
            "address TEXT," +
            "trainerProfileImage TEXT"+
            ");";

    private static final String CREATE_PAYMENTS_TABLE = "CREATE TABLE payments(" +
            "_id TEXT PRIMARY KEY," +
            "paymentDate BIGINT,"+
            "finalPrice FLOAT" +
            ")";

    // Remove this to simplify the model - rating in appointment 1-1  - avg rating in trainer
    private static final String CREATE_RATINGS_TABLE = "CREATE TABLE ratings(" +
            "_id TEXT PRIMARY KEY, " +
            "trainerID TEXT, " +
            "rating FLOAT, " +
            "FOREIGN KEY(trainerID) REFERENCES trainers(_id)" +
            ");";


    private static final String CREATE_TRAINERSERVICE_TABLE = "CREATE TABLE trainerservice("+
            "_id INT PRIMARY KEY,"+
            "service TEXT, " +
            "trainerID TEXT, " +
            "FOREIGN KEY(trainerID) REFERENCES trainers(_id)" +
            ");";

    private static final String CREATE_TRAINER_OPEN_SCHEDULE_TABLE = "CREATE TABLE schedule( " +
            "_id INT PRIMARY KEY, " +
            "time BIGINT, " +
            "trainerID TEXT, " +
            "FOREIGN KEY(trainerID) REFERENCES trainers(_id) " +
            ");";
    public static final String URL_FIRESTORE_SELF_WORKOUT_PLANS_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/selfWorkoutPlans.csv";
    public static final String URL_FIRESTORE_SELF_WORKOUT_SESSION_TYPES_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/selfWorkoutSessionTypes.csv";
    public static final String URL_FIRESTORE_SELF_PLAN_EXERCISES_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/selfWorkoutPlanExercises.csv";
    public static final String URL_FIRESTORE_TRAINER_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/trainers.csv";

    public static  final String URL_FIRESTORE_RATINGS_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/ratings.csv";

    public static final String URL_FIRESTORE_TRAINERSERVICE_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/trainerService.csv";
    public static final String URL_FIRESTORE_TRAINER_OPEN_SCHEDULE_TABLE="gs://w23-g3-coachme.appspot.com/sqlite_datasets/schedule.csv";

    public static final String URL_FIRESTORE_APPOINTMENTS_TABLE = "gs://w23-g3-coachme.appspot.com/sqlite_datasets/appointments.csv";
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
        db.execSQL(CREATE_PAYMENTS_TABLE);
        db.execSQL(CREATE_RATINGS_TABLE);
        db.execSQL(CREATE_TRAINERSERVICE_TABLE);
        db.execSQL(CREATE_TRAINER_OPEN_SCHEDULE_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS payments");
        db.execSQL("DROP TABLE IF EXISTS trainers");
        db.execSQL("DROP TABLE IF EXISTS ratings");
        db.execSQL("DROP TABLE IF EXISTS trainerservice");
        db.execSQL("DROP TABLE IF EXISTS schedule");
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
                selfWorkoutExerciseContent.put("exerciseImageURLFirestore",nextLine[6]);
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
                trainersContent.put("trainerProfileImage",nextLine[10]);

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


    // Upload ratings
    public void uploadRatings(byte[] bytes){

        String content = null; // Convert byte array to string
        try {
            SQLiteDatabase db = getWritableDatabase();
            content = new String(bytes, "UTF-8");
            CSVReader reader = new CSVReader(new StringReader(content));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                ContentValues ratingsContent = new ContentValues();
                ratingsContent.put("_id",nextLine[0]);
                ratingsContent.put("trainerID",nextLine[1]);
                ratingsContent.put("rating",Double.parseDouble(nextLine[2]));
                db.insert("ratings", null, ratingsContent);

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


    public void uploadTrainerService(byte[] bytes){

        String content = null; // Convert byte array to string
        try {
            SQLiteDatabase db = getWritableDatabase();
            content = new String(bytes, "UTF-8");
            CSVReader reader = new CSVReader(new StringReader(content));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                ContentValues trainerServContents = new ContentValues();
                trainerServContents.put("_id",nextLine[0]);
                trainerServContents.put("service",nextLine[1]);
                trainerServContents.put("trainerID",nextLine[2]);
                db.insert("trainerservice", null, trainerServContents);

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


    public void uploadTrainerSchedule(byte[] bytes){

        String content = null; // Convert byte array to string
        try {
            SQLiteDatabase db = getWritableDatabase();
            content = new String(bytes, "UTF-8");
            CSVReader reader = new CSVReader(new StringReader(content));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                ContentValues trainerScheduleContents = new ContentValues();
                trainerScheduleContents.put("_id",nextLine[0]);
                trainerScheduleContents.put("time",nextLine[1]);
                trainerScheduleContents.put("trainerID",nextLine[2]);
                db.insert("schedule", null, trainerScheduleContents);

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

    public void uploadAppointments(byte[] bytes){

        String content = null; // Convert byte array to string
        try {
            SQLiteDatabase db = getWritableDatabase();
            content = new String(bytes, "UTF-8");
            CSVReader reader = new CSVReader(new StringReader(content));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                ContentValues appointmentscontents = new ContentValues();
                appointmentscontents.put("_id",nextLine[0]);
                appointmentscontents.put("bookedDate",nextLine[1]);
                appointmentscontents.put("registeredDate",nextLine[2]);
                appointmentscontents.put("serviceType",nextLine[3]);
                appointmentscontents.put("status",nextLine[4]);
                appointmentscontents.put("totalPrice",nextLine[5]);
                appointmentscontents.put("location",nextLine[6]);
                appointmentscontents.put("trainerId",nextLine[7]);
                appointmentscontents.put("customerId",nextLine[8]);
                appointmentscontents.put("paymentId",nextLine[9]);
                appointmentscontents.put("paymentDate",nextLine[10]);
                appointmentscontents.put("deviceToken",nextLine[11]);
                appointmentscontents.put("rating",nextLine[12]);
                appointmentscontents.put("comment",nextLine[13]);
                db.insert("appointments", null, appointmentscontents);

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
//                String deviceToken = cursor.getString(cursor.getColumnIndex("deviceToken"));

                Appointment appointment = new Appointment(id,bookedDate,registeredDate,serviceType,status,
                        totalPrice,location,trainerId,UserSingleton.getInstance().getUserId(), paymentId, paymentDate, UserSingleton.getInstance().getUserDeviceToken());

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
                        totalPrice,location,trainerId,UserSingleton.getInstance().getUserId(),paymentId,paymentDate,UserSingleton.getInstance().getUserDeviceToken());

                appointmentsList.add(appointment);

            }while(cursor.moveToNext());
        }
        db.close();
        return appointmentsList;

    }

    @SuppressLint("Range")
    public List<Appointment> getAppointmentsByPaymentId(String paymentId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM appointments WHERE paymentID = '"+paymentId+"'",null);

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
                Long paymentDate = cursor.getLong(cursor.getColumnIndex("paymentDate"));

                Appointment appointment = new Appointment(id,bookedDate,registeredDate,serviceType,statusValue,
                        totalPrice,location,trainerId,UserSingleton.getInstance().getUserId(),paymentId,paymentDate,UserSingleton.getInstance().getUserDeviceToken());

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


    public void addAppToCart(String id,long bookedDate,long registeredDate, String serviceType, int status,
                             double totalPrice, String location,
                             String trainerId, String customerId, String deviceToken){

        //Adding the appointment into firebase
        FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();
        DatabaseReference newAppRef = CoachMeDatabaseRef.child("appointments").child(id);

        newAppRef.child("bookedDate").setValue(bookedDate);
        newAppRef.child("registeredDate").setValue(registeredDate);
        newAppRef.child("serviceType").setValue(serviceType);
        newAppRef.child("status").setValue(status);
        newAppRef.child("totalPrice").setValue(totalPrice);
        newAppRef.child("location").setValue(location);
        newAppRef.child("trainerId").setValue(trainerId);
        newAppRef.child("customerId").setValue(customerId);
        newAppRef.child("deviceToken").setValue(deviceToken);

        //Now let's add the new appointment

        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("bookedDate", bookedDate);
        values.put("registeredDate", registeredDate);
        values.put("serviceType", serviceType);
        values.put("status", status);
        values.put("totalPrice", totalPrice);
        values.put("location", location);
        values.put("trainerId", trainerId);
        values.put("customerId", customerId);
        values.put("deviceToken", deviceToken);

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert("appointments", null, values);
        db.close();

        if (result == -1) {
            // Insert failed
            Log.e("addAppToCart", "Failed to add appointment to cart.");
        } else {
            // Insert succeeded
            Log.d("addAppToCart", "Appointment added to cart with ID: " + id);
        }

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
                selfWorkoutPlan, new Date(requestedDate).getTime(), status, new Date(paymentDate).getTime(), paymentId);

                selfWorkoutPlanByUsersList.add(selfWorkoutPlanByUser);

            }while (cursor.moveToNext());
        }

        db.close();
        return selfWorkoutPlanByUsersList;
    }

   @SuppressLint("Range")
   public SelfWorkoutPlan getSelfWorkoutPlanById(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM selfWorkoutPlans WHERE _id = '"+id+"'",null);

        if(cursor.moveToFirst()){

            String selfWorkoutPlanId = cursor.getString(cursor.getColumnIndex("_id"));
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

            db.close();
            return selfWorkoutPlan;
        }

        db.close();
        return null;
    }

    @SuppressLint("Range")
    public SelfWorkoutPlanByUser getSelfWorkoutPlanByUserById(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT \n" +
                "su._id as selfWorkoutPlanByUserId, su.paymentId, su.paymentDate, su.requestedDate, su.status,\n" +
                " sp._id as selfWorkoutPlanId, sp.title,  sp.description, sp.planPrice, sp.mainGoals, sp.daysPerWeek,\n" +
                " sp.level,  sp.duration, sp.posterUrlFirestore\n" +
                "FROM  selfWorkoutPlansByUser su \n" +
                "JOIN selfWorkoutPlans sp \n" +
                "ON su.selfWorkoutPlanId = sp._id \n"+
                "WHERE su._id = "+id,null);

        SelfWorkoutPlanByUser selfWorkoutPlanByUser = null;

        if(cursor.moveToFirst()){
            Integer selfWorkoutPlanByUserId = cursor.getInt(cursor.getColumnIndex("selfWorkoutPlanByUserId"));
            Long requestedDate = cursor.getLong(cursor.getColumnIndex("requestedDate"));
            Long paymentDate = cursor.getLong(cursor.getColumnIndex("paymentDate"));
            String paymentId = cursor.getString(cursor.getColumnIndex("paymentId"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
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

            selfWorkoutPlanByUser = new SelfWorkoutPlanByUser(selfWorkoutPlanByUserId,
                    selfWorkoutPlan, new Date(requestedDate).getTime(), status, new Date(paymentDate).getTime(), paymentId);

        }

        db.close();
        return selfWorkoutPlanByUser;
    }

    @SuppressLint("Range")
    public List<SelfWorkoutPlanByUser> getSelfWorkoutPlanByUserByPaymentId(String paymentId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT \n" +
                "su._id as selfWorkoutPlanByUserId, su.paymentId, su.paymentDate, su.requestedDate, su.status,\n" +
                " sp._id as selfWorkoutPlanId, sp.title,  sp.description, sp.planPrice, sp.mainGoals, sp.daysPerWeek,\n" +
                " sp.level,  sp.duration, sp.posterUrlFirestore\n" +
                "FROM  selfWorkoutPlansByUser su \n" +
                "JOIN selfWorkoutPlans sp \n" +
                "ON su.selfWorkoutPlanId = sp._id \n"+
                "WHERE su.paymentId = '"+paymentId+"'",null);

        List<SelfWorkoutPlanByUser> selfWorkoutPlanByUsersList = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{

                Integer selfWorkoutPlanByUserId = cursor.getInt(cursor.getColumnIndex("selfWorkoutPlanByUserId"));
                Long requestedDate = cursor.getLong(cursor.getColumnIndex("requestedDate"));
                Long paymentDate = cursor.getLong(cursor.getColumnIndex("paymentDate"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
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
                        selfWorkoutPlan, new Date(requestedDate).getTime(), status, new Date(paymentDate).getTime(), paymentId);

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

    public void createWorkoutPlanByUser(String customerId, String selfWorkoutPlanId){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues swpByUser = new ContentValues();

        swpByUser.put("customerId",customerId);
        swpByUser.put("paymentDate", (Long)null);
        swpByUser.put("paymentId",(String)null);
        swpByUser.put("requestedDate",new Date().getTime());
        swpByUser.put("selfworkoutplanId",selfWorkoutPlanId);
        swpByUser.put("status", 1);

        db.insert("selfWorkoutPlansByUser", null, swpByUser);

        db.close();
    }

    /* ---------------------------------------
    -----------SELF WORKOUT SESSIONS----------
    ------------------------------------------ */

    @SuppressLint("Range")
    public SelfWorkoutSession getActiveSelfWorkoutSession(int swpUserId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT \n" +
                "ss.*\n" +
                "FROM selfWorkoutSessions ss\n" +
                "JOIN selfWorkoutPlansByUser swu ON ss.selfWorkoutPlansByUserId = swu._id\n" +
                "JOIN selfWorkoutSessionTypes swt ON ss.selfWorkoutSessionTypeId = swt._id\n" +
                "WHERE swu._id = "+ swpUserId +" AND ss.sessionStatus = 1\n",null);

        SelfWorkoutSession selfWorkoutSession = null;
        int selfWorkoutPlansByUserId;
        String selfWorkoutSessionTypeId;

        if(cursor.moveToFirst()){
            Integer sessionId = cursor.getInt(cursor.getColumnIndex("_id"));
            int sessionStatus = cursor.getInt(cursor.getColumnIndex("status"));
            Long sessionDate = cursor.getLong(cursor.getColumnIndex("sessionDate"));
            selfWorkoutPlansByUserId = cursor.getInt(cursor.getColumnIndex("selfWorkoutPlansByUserId"));
            selfWorkoutSessionTypeId = cursor.getString(cursor.getColumnIndex("selfWorkoutSessionTypeId"));
            selfWorkoutSession = new SelfWorkoutSession(sessionId,null,null,
                    sessionDate,sessionStatus);

        }else{
            db.close();
            return null;
        }

        db.close();

        //get the workout object of the user
        SelfWorkoutPlanByUser swpUser = getSelfWorkoutPlanByUserById(selfWorkoutPlansByUserId);

        //get the workout session type of the session
        SelfWorkoutSessionType ssType = getSelfWorkoutSessionTypeById(selfWorkoutSessionTypeId);

        selfWorkoutSession.setSelfWorkoutPlanByUser(swpUser);
        selfWorkoutSession.setSelfworkoutSessionType(ssType);

        return selfWorkoutSession;
    }

    @SuppressLint("Range")
    public SelfWorkoutSession getTodaySelfWorkoutSession(int swpUserId){

        //Validate if it is a resume or a start
        Date currentDate = new Date();
        // Set the start time to the beginning of the current day
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        //set initial dates
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        Long startTime = calendar.getTime().getTime();
        calendar.set(year, month, dayOfMonth, 23, 59, 59);
        Long endTime = calendar.getTime().getTime();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT \n" +
                "ss.*\n" +
                "FROM selfWorkoutSessions ss\n" +
                "JOIN selfWorkoutPlansByUser swu ON ss.selfWorkoutPlansByUserId = swu._id\n" +
                "JOIN selfWorkoutSessionTypes swt ON ss.selfWorkoutSessionTypeId = swt._id\n" +
                "WHERE swu._id = "+ swpUserId + " AND ss.sessionDate BETWEEN "+startTime+" AND "+endTime;

        Cursor cursor = db.rawQuery(query,null);

        SelfWorkoutSession selfWorkoutSession = null;
        int selfWorkoutPlansByUserId;
        String selfWorkoutSessionTypeId;

        if(cursor.moveToFirst()){
            Integer sessionId = cursor.getInt(cursor.getColumnIndex("_id"));
            int sessionStatus = cursor.getInt(cursor.getColumnIndex("status"));
            Long sessionDate = cursor.getLong(cursor.getColumnIndex("sessionDate"));
            selfWorkoutPlansByUserId = cursor.getInt(cursor.getColumnIndex("selfWorkoutPlansByUserId"));
            selfWorkoutSessionTypeId = cursor.getString(cursor.getColumnIndex("selfWorkoutSessionTypeId"));
            selfWorkoutSession = new SelfWorkoutSession(sessionId,null,null,
                    sessionDate,sessionStatus);

        }else{
            db.close();
            return null;
        }

        db.close();

        //get the workout object of the user
        SelfWorkoutPlanByUser swpUser = getSelfWorkoutPlanByUserById(selfWorkoutPlansByUserId);

        //get the workout session type of the session
        SelfWorkoutSessionType ssType = getSelfWorkoutSessionTypeById(selfWorkoutSessionTypeId);

        selfWorkoutSession.setSelfWorkoutPlanByUser(swpUser);
        selfWorkoutSession.setSelfworkoutSessionType(ssType);

        return selfWorkoutSession;
    }

    @SuppressLint("Range")
    public SelfWorkoutSession getSessionById(int swpUserId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT \n" +
                "ss.*\n" +
                "FROM selfWorkoutSessions ss\n" +
                "JOIN selfWorkoutPlansByUser swu ON ss.selfWorkoutPlansByUserId = swu._id\n" +
                "JOIN selfWorkoutSessionTypes swt ON ss.selfWorkoutSessionTypeId = swt._id\n" +
                "WHERE ss._id = "+ swpUserId,null);

        SelfWorkoutSession selfWorkoutSession = null;
        int selfWorkoutPlansByUserId;
        String selfWorkoutSessionTypeId;

        if(cursor.moveToFirst()){
            Integer sessionId = cursor.getInt(cursor.getColumnIndex("_id"));
            int sessionStatus = cursor.getInt(cursor.getColumnIndex("status"));
            Long sessionDate = cursor.getLong(cursor.getColumnIndex("sessionDate"));
            selfWorkoutPlansByUserId = cursor.getInt(cursor.getColumnIndex("selfWorkoutPlansByUserId"));
            selfWorkoutSessionTypeId = cursor.getString(cursor.getColumnIndex("selfWorkoutSessionTypeId"));
            selfWorkoutSession = new SelfWorkoutSession(sessionId,null,null,
                    sessionDate,sessionStatus);

        }else{
            db.close();
            return null;
        }

        db.close();

        //get the workout object of the user
        SelfWorkoutPlanByUser swpUser = getSelfWorkoutPlanByUserById(selfWorkoutPlansByUserId);

        //get the workout session type of the session
        SelfWorkoutSessionType ssType = getSelfWorkoutSessionTypeById(selfWorkoutSessionTypeId);

        selfWorkoutSession.setSelfWorkoutPlanByUser(swpUser);
        selfWorkoutSession.setSelfworkoutSessionType(ssType);

        return selfWorkoutSession;
    }

    @SuppressLint("Range")
    public List<SelfWorkoutPlan> getSelfWorkoutPlanAvailable (String customerId){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT sp.* \n" +
                "FROM selfWorkoutPlans sp\n" +
                "LEFT JOIN (\n" +
                "\tSELECT  selfWorkoutPlanId, customerId \n" +
                "\tFROM selfWorkoutPlansByUser \n" +
                "\tWHERE customerId = '"+customerId+"' AND status IN (1,3) \n" +
                "\t)spu\n" +
                "ON sp._id = spu.selfWorkoutPlanId\n" +
                "WHERE spu.selfWorkoutPlanId IS NULL";

        Cursor cursor = db.rawQuery(query,null);

        List<SelfWorkoutPlan> selfWorkoutPlanList = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{

                String selfWorkoutPlanId = cursor.getString(cursor.getColumnIndex("_id"));
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

                selfWorkoutPlanList.add(selfWorkoutPlan);

            }while (cursor.moveToNext());
        }

        db.close();
        return selfWorkoutPlanList;
    }

    public void deleteSelfWorkoutSessionLogsBySessionId(int sessionId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("selfWorkoutSessionLogs","selfWorkoutSessionId" +
                " = ?",new String[]{Integer.toString(sessionId)});
        db.close();
    }

    public void deleteSelfWorkoutSessionBySessionId(int sessionId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("selfWorkoutSessions","_id" +
                " = ?",new String[]{Integer.toString(sessionId)});
        db.close();
    }

    @SuppressLint("Range")
    public SelfWorkoutSessionType getSelfWorkoutSessionTypeById(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM selfWorkoutSessionTypes WHERE _id = '"+id+"'",null);

        if(cursor.moveToFirst()){

            String sessionType = cursor.getString(cursor.getColumnIndex("sessionType"));
            String sessionTypeIconURLFirestore = cursor.getString(cursor.getColumnIndex("sessionTypeIconURLFirestore"));
            String selfWorkoutPlanId = cursor.getString(cursor.getColumnIndex("selfWorkoutPlanId"));

            SelfWorkoutSessionType swType = new SelfWorkoutSessionType();
            swType.setId(id);
            swType.setSessionType(sessionType);
            swType.setSessionTypeIconURLFirestore(sessionTypeIconURLFirestore);

            db.close();

            SelfWorkoutPlan selfWorkoutPlan = getSelfWorkoutPlanById(selfWorkoutPlanId);
            swType.setSelfWorkoutPlan(selfWorkoutPlan);
            return swType;

        }

        db.close();
        return null;

    }

    @SuppressLint("Range")
    public List<SelfWorkoutSessionType> getSessionTypesByPlanId(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM selfWorkoutSessionTypes WHERE " +
                "selfWorkoutPlanId ='"+id+"'",null);

        List<SelfWorkoutSessionType> selfWorkoutSessionTypes = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                String sessionTypeId = cursor.getString(cursor.getColumnIndex("_id"));
                String sessionType = cursor.getString(cursor.getColumnIndex("sessionType"));
                String sessionTypeIconURLFirestore = cursor.getString(cursor.getColumnIndex("sessionTypeIconURLFirestore"));
                SelfWorkoutSessionType selfWorkoutSessionType = new SelfWorkoutSessionType();

                selfWorkoutSessionType.setId(sessionTypeId);
                selfWorkoutSessionType.setSessionType(sessionType);
                selfWorkoutSessionType.setSessionTypeIconURLFirestore(sessionTypeIconURLFirestore);

                selfWorkoutSessionTypes.add(selfWorkoutSessionType);

            }while(cursor.moveToNext());
        }

        db.close();
        return selfWorkoutSessionTypes;
    }

    @SuppressLint("Range")
    public SelfWorkoutSessionType getSessionTypeById(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM selfWorkoutSessionTypes WHERE " +
                "_id ='"+id+"'",null);

        SelfWorkoutSessionType selfWorkoutSessionType = null;

        if(cursor.moveToFirst()){

            String sessionType = cursor.getString(cursor.getColumnIndex("sessionType"));
            String sessionTypeIconURLFirestore = cursor.getString(cursor.getColumnIndex("sessionTypeIconURLFirestore"));

            selfWorkoutSessionType = new SelfWorkoutSessionType();
            selfWorkoutSessionType.setId(id);
            selfWorkoutSessionType.setSessionType(sessionType);
            selfWorkoutSessionType.setSessionTypeIconURLFirestore(sessionTypeIconURLFirestore);

        }

        db.close();
        return selfWorkoutSessionType;
    }

    public int updateSelfWorkoutSessionStatus(int id, int status){
        SQLiteDatabase db = getWritableDatabase();
        // Create a ContentValues object with the new value of the "status" field
        ContentValues values = new ContentValues();
        values.put("status", status);

        // Update the appointments table with the new value of the "status" field
        int numRowsUpdated = db.update("selfWorkoutSessions", values, "_id=?", new String[] {Integer.toString(id)});

        // Close the database connection
        db.close();

        return numRowsUpdated;
    }

    @SuppressLint("Range")
    public SelfWorkoutSession createNewSession(int selfworkoutUserId,String sessionTypeId,Long sessionDate,int status){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues session = new ContentValues();

        session.put("status",status);
        session.put("sessionDate",sessionDate);
        session.put("selfWorkoutPlansByUserId",selfworkoutUserId);
        session.put("selfWorkoutSessionTypeId",sessionTypeId);

        Long id = db.insert("selfWorkoutSessions", null, session);

        db.close();

        SelfWorkoutSession ses = new SelfWorkoutSession();
        ses.setId(id.intValue());
        ses.setSessionDate(sessionDate);
        ses.setSessionStatus(status);

        SelfWorkoutPlanByUser swpUser = getSelfWorkoutPlanByUserById(selfworkoutUserId);
        SelfWorkoutSessionType ssT = getSessionTypeById(sessionTypeId);
        ses.setSelfWorkoutPlanByUser(swpUser);
        ses.setSelfworkoutSessionType(ssT);

        //Setting exercises Logs
        List<SelfWorkoutPlanExercise> exercisesList = getSelfWorkoutExerciseBySessionTypeId(ssT.getId());

        Log.d("SIZE LIST",Integer.toString(exercisesList.size()));

        for(SelfWorkoutPlanExercise exercise : exercisesList){

            db = getWritableDatabase();
            ContentValues log = new ContentValues();

            log.put("selfWorkoutSessionId",id.intValue());
            log.put("selfWorkoutPlanExerciseId",exercise.getId());
            log.put("status",1); //pending

            db.insert("selfWorkoutSessionLogs", null, log);

            db.close();

        }

        return ses;

    }

    /* ------------------------------------------------
    --------------SESSION EXERCISES (LOGS)-------------
    --------------------------------------------------- */
    @SuppressLint("Range")
    public List<SelfWorkoutPlanExercise> getSelfWorkoutExerciseBySessionTypeId(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM selfWorkoutPlanExercises WHERE " +
                "selfWorkoutSessionTypeId ='"+id+"'",null);

        List<SelfWorkoutPlanExercise> selfWorkoutPlanExercises = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                String exerciseId = cursor.getString(cursor.getColumnIndex("_id"));
                String exerciseName = cursor.getString(cursor.getColumnIndex("exerciseName"));
                int numSets = cursor.getInt(cursor.getColumnIndex("numSets"));
                String repetitions = cursor.getString(cursor.getColumnIndex("repetitions"));
                String restTime = cursor.getString(cursor.getColumnIndex("restTime"));
                String selfWorkoutSessionTypeId = cursor.getString(cursor.getColumnIndex("selfWorkoutSessionTypeId"));
                String exerciseImageURLFirestore = cursor.getString(cursor.getColumnIndex("exerciseImageURLFirestore"));

                SelfWorkoutPlanExercise selfWorkoutPlanExercise = new SelfWorkoutPlanExercise();

                selfWorkoutPlanExercise.setId(exerciseId);
                selfWorkoutPlanExercise.setExerciseName(exerciseName);
                selfWorkoutPlanExercise.setSelfWorkoutSessionTypeId(selfWorkoutSessionTypeId);
                selfWorkoutPlanExercise.setExerciseImageURLFirestore(exerciseImageURLFirestore);
                selfWorkoutPlanExercise.setNumSets(numSets);
                selfWorkoutPlanExercise.setRestTime(restTime);
                selfWorkoutPlanExercise.setRepetitions(repetitions);

                selfWorkoutPlanExercises.add(selfWorkoutPlanExercise);

            }while(cursor.moveToNext());
        }

        db.close();
        return selfWorkoutPlanExercises;
    }

    @SuppressLint("Range")
    public List<SelfWorkoutSessionLog> getSessionLogs(int sessionId){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT \n" +
                "slogs._id,\n" +
                "slogs.selfWorkoutSessionId,\n"+
                "slogs.status,\n" +
                "exercises.exerciseName,\n" +
                "exercises._id as exerciseId,\n" +
                "exercises.numSets,\n" +
                "exercises.repetitions,\n" +
                "exercises.restTime,\n" +
                "exercises.exerciseImageURLFirestore,\n" +
                "exercises.selfWorkoutSessionTypeId\n" +
                "FROM selfWorkoutSessionLogs slogs\n" +
                "JOIN selfWorkoutPlanExercises exercises on slogs.selfWorkoutPlanExerciseId = exercises._id\n" +
                "WHERE selfWorkoutSessionId = '"+sessionId+"'",null);

        List<SelfWorkoutSessionLog> selfWorkoutSessionLogs = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{

                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                int selfWorkoutSessionId = cursor.getInt(cursor.getColumnIndex("selfWorkoutSessionId"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                String exerciseId = cursor.getString(cursor.getColumnIndex("exerciseId"));
                String exerciseName = cursor.getString(cursor.getColumnIndex("exerciseName"));
                int numSets = cursor.getInt(cursor.getColumnIndex("numSets"));
                String repetitions = cursor.getString(cursor.getColumnIndex("repetitions"));
                String restTime = cursor.getString(cursor.getColumnIndex("restTime"));
                String selfWorkoutSessionTypeId = cursor.getString(cursor.getColumnIndex("selfWorkoutSessionTypeId"));
                String exerciseImageURLFirestore = cursor.getString(cursor.getColumnIndex("exerciseImageURLFirestore"));

                SelfWorkoutPlanExercise selfWorkoutPlanExercise = new SelfWorkoutPlanExercise(exerciseId,selfWorkoutSessionTypeId,exerciseName,numSets,repetitions,restTime,exerciseImageURLFirestore);
                SelfWorkoutSessionLog selfWorkoutSessionLog = new SelfWorkoutSessionLog(id,selfWorkoutSessionId,selfWorkoutPlanExercise,status);
                selfWorkoutSessionLogs.add(selfWorkoutSessionLog);

            }while (cursor.moveToNext());
        }

        db.close();
        return selfWorkoutSessionLogs;

    }

    @SuppressLint("Range")
    public void createNewSessionLog(int selfWorkoutSessionId,String exerciseId,int status){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues sessionLog = new ContentValues();

        sessionLog.put("selfWorkoutSessionId",selfWorkoutSessionId);
        sessionLog.put("selfWorkoutPlanExerciseId",exerciseId);
        sessionLog.put("status",status);

        db.insert("selfWorkoutSessionLogs", null, sessionLog);

        db.close();

    }

    @SuppressLint("Range")
    public SelfWorkoutSessionLog getSessionLogById(int sessionLogId){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT \n" +
                "slogs._id,\n" +
                "slogs.status,\n" +
                "exercises.exerciseName,\n" +
                "exercises._id as exerciseId,\n" +
                "exercises.numSets,\n" +
                "exercises.repetitions,\n" +
                "exercises.restTime,\n" +
                "exercises.execiseImageURLFirestore,\n" +
                "exercises.selfWorkoutSessionTypeId\n" +
                "FROM selfWorkoutSessionLogs slogs\n" +
                "JOIN selfWorkoutPlanExercises exercises on slogs.selfWorkoutPlanExerciseId = exercises._id\n" +
                "WHERE _id = "+sessionLogId,null);

        SelfWorkoutSessionLog selfWorkoutSessionLog = null;

        if(cursor.moveToFirst()){
            do{

                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                int selfWorkoutSessionId = cursor.getInt(cursor.getColumnIndex("selfWorkoutSessionId"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                String exerciseId = cursor.getString(cursor.getColumnIndex("exerciseId"));
                String exerciseName = cursor.getString(cursor.getColumnIndex("exerciseName"));
                int numSets = cursor.getInt(cursor.getColumnIndex("numSets"));
                String repetitions = cursor.getString(cursor.getColumnIndex("repetitions"));
                String restTime = cursor.getString(cursor.getColumnIndex("restTime"));
                String selfWorkoutSessionTypeId = cursor.getString(cursor.getColumnIndex("selfWorkoutSessionTypeId"));
                String exerciseImageURLFirestore = cursor.getString(cursor.getColumnIndex("exerciseImageURLFirestore"));

                SelfWorkoutPlanExercise selfWorkoutPlanExercise = new SelfWorkoutPlanExercise(exerciseId,selfWorkoutSessionTypeId,exerciseName,numSets,repetitions,restTime,exerciseImageURLFirestore);
                selfWorkoutSessionLog = new SelfWorkoutSessionLog(id,selfWorkoutSessionId,selfWorkoutPlanExercise,status);


            }while (cursor.moveToNext());
        }

        db.close();
        return selfWorkoutSessionLog;

    }

    public int updateSelfWorkoutSessionLogByStatus(int selfWorkoutSessionLogId,int status){
        SQLiteDatabase db = getWritableDatabase();
        // Create a ContentValues object with the new value of the "status" field
        ContentValues values = new ContentValues();
        values.put("status", status);

        // Update the appointments table with the new value of the "status" field
        int numRowsUpdated = db.update("selfWorkoutSessionLogs", values, "_id=?", new String[] {Integer.toString(selfWorkoutSessionLogId)});

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
        Cursor cursor = db.rawQuery("SELECT t._id, t.firstName, t.lastName, t.email, \n" +
                        "t.latitudeCoord, t.longitudeCoord, t.radius, t.flatPrice, t.phoneNumber,t.trainerProfileImage," +
                        "t.address, AVG(r.rating) AS avgRating " +
                        "FROM trainers t \n" +
                        "JOIN ratings r ON t._id = r.trainerID \n" +
                        "WHERE t._id = '"+trainerId+"'" +
                        "GROUP BY t._id, t.firstName, t.lastName, t.email, t.latitudeCoord, t.longitudeCoord, t.radius, t.flatPrice, t.phoneNumber, t.address, t.trainerProfileImage ",null);

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
            String trainerProfileImage = cursor.getString(cursor.getColumnIndex("trainerProfileImage"));
            Double rating = cursor.getDouble(cursor.getColumnIndex("avgRating"));

            trainer = new Trainer(trainerId, firstName, lastName, email,
                    latitudeCoord, longitudeCoord,  radius, flatPrice,
                     phoneNumber, address, trainerProfileImage, rating);

        }

        db.close();
        return trainer;
    }


    @SuppressLint("Range")
    public List<Trainer> getTrainers(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT t._id, t.firstName, t.lastName, t.email, \n" +
                "t.latitudeCoord, t.longitudeCoord, t.radius, t.flatPrice, t.phoneNumber," +
                "t.address,t.trainerProfileImage, AVG(r.rating) AS avgRating " +
                "FROM trainers t \n" +
                "JOIN ratings r ON t._id = r.trainerID \n" +
                "GROUP BY t._id, t.firstName, t.lastName, t.email, t.latitudeCoord, t.longitudeCoord, t.radius, t.flatPrice, t.phoneNumber, t.address,t.trainerProfileImage  "
                ,null);

        List<Trainer> tempTrainers =  new ArrayList<>(); // List for the trainers
        Trainer trainer = null;
        int count = 0;

        if(cursor.moveToFirst()){
            do {
                String trainerId = cursor.getString(cursor.getColumnIndex("_id"));
                String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                Double latitudeCoord = cursor.getDouble(cursor.getColumnIndex("latitudeCoord"));
                Double longitudeCoord = cursor.getDouble(cursor.getColumnIndex("longitudeCoord"));
                Integer radius = cursor.getInt(cursor.getColumnIndex("radius"));
                Double flatPrice = cursor.getDouble(cursor.getColumnIndex("flatPrice"));
                String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                Double rating = cursor.getDouble(cursor.getColumnIndex("avgRating"));
                String trainerProfileImage = cursor.getString(cursor.getColumnIndex("trainerProfileImage"));

                trainer = new Trainer(trainerId, firstName, lastName, email,
                        latitudeCoord, longitudeCoord, radius, flatPrice,
                        phoneNumber, address,trainerProfileImage, rating);

                tempTrainers.add(trainer);
                count++;

            } while(cursor.moveToNext());
        }

        Log.d("Andrea", "Trainers from SQLLite " + count);

        db.close();
        return tempTrainers;
    }


    @SuppressLint("Range")
    public List<Trainer> getTrainersByServicesAndDate(List<String> services, long dateFrom, long dateTo) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT trainers._id, trainers.firstName, trainers.lastName, trainers.email, " +
                "trainers.latitudeCoord, trainers.longitudeCoord, trainers.radius, trainers.flatPrice, trainers.phoneNumber," +
                "trainers.address,trainers.trainerProfileImage, AVG(ratings.rating) AS avgRating " +
                " FROM trainers " +
                "LEFT JOIN ratings ON trainers._id = ratings.trainerID " +
                "JOIN trainerservice ON trainers._id = trainerservice.trainerID " +
                "JOIN schedule ON trainers._id = schedule.trainerID " +
                "WHERE schedule.time BETWEEN " + dateFrom +" AND " + dateTo ;


        // Add service filtering if services list is not empty
        if (!services.isEmpty()) {
            String serviceList = "'" + TextUtils.join("','", services) + "'";
            System.out.println(serviceList);
            selectQuery += " AND trainerservice.service IN (" + serviceList + ") ";
        }

        selectQuery += "\n GROUP BY trainers._id, trainers.firstName, trainers.lastName, trainers.email,  " +
                " trainers.latitudeCoord, trainers.longitudeCoord, trainers.radius, trainers.flatPrice, trainers.phoneNumber, " +
                "trainers.address, trainers.trainerProfileImage";

        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Trainer> trainers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Trainer trainer = new Trainer();
                trainer.setId(cursor.getString(cursor.getColumnIndex("_id")));
                trainer.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
                trainer.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
                trainer.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                trainer.setLatitudeCoord(cursor.getFloat(cursor.getColumnIndex("latitudeCoord")));
                trainer.setLongitudeCoord(cursor.getFloat(cursor.getColumnIndex("longitudeCoord")));
                trainer.setRadius(cursor.getInt(cursor.getColumnIndex("radius")));
                trainer.setFlatPrice(cursor.getFloat(cursor.getColumnIndex("flatPrice")));
                trainer.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
                trainer.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                trainer.setRating(cursor.getDouble(cursor.getColumnIndex("avgRating")));
                trainer.setTrainerProfileImage(cursor.getString(cursor.getColumnIndex("trainerProfileImage")));

                System.out.println("NNNNN " + cursor.getString(cursor.getColumnIndex("_id")));

                trainers.add(trainer);
            } while (cursor.moveToNext());
        }
        System.out.println("FROM DBHELPER " + trainers.size() + "\n Date from " + dateFrom + " \n Date to " + dateTo);
        db.close();

        return trainers;
    }

    // Get available schedule by trainer ID
    @SuppressLint("Range")
    public List<Long> getTimesByTrainerID(String trainerId){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM schedule " +
                "JOIN trainers ON trainers._id = schedule.trainerID " +
                "WHERE trainerID =  '"+trainerId+"'", null);

        List<Long> times = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                times.add(cursor.getLong(cursor.getColumnIndex("time")));
                System.out.println(cursor.getLong(cursor.getColumnIndex("time")));

            }while(cursor.moveToNext());
        }
        db.close();
        return times;
    }

    public void removeFromSchedule(String trainerID, long initTime, long endTime){

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "trainerID = ? AND time >= ? AND time <= ?";
        String[] whereArgs = { trainerID, String.valueOf(initTime), String.valueOf(endTime) };

        // Execute the DELETE SQL statement with the specified WHERE clause
        db.delete("schedule", whereClause, whereArgs);

        System.out.println("DELETING APPOINTMENT " + initTime); // working
        // Close the database connection
        db.close();
    }

    public void insertSchedule(long time, String trainerID) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT MAX(_id) FROM schedule";
        Cursor cursor = db.rawQuery(query, null);
        int maxId = 0;
        if (cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
        }
        maxId++;

        ContentValues values = new ContentValues();
        values.put("_id", maxId);
        values.put("time", time);
        values.put("trainerID", trainerID);

        db.insert("schedule", null, values);

        db.close();
    }


    // This one is not necessary
    @SuppressLint("Range")
    public List<Appointment> getAppointmentsByCustomerIdAndStatus(String trainerId, int status) {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM appointments WHERE status=? AND customerId='?'";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(status), trainerId});

        if (cursor.moveToFirst()) {
            do {
                String appointmentId = cursor.getString(cursor.getColumnIndex("_id"));
                long bookedDate = cursor.getLong(cursor.getColumnIndex("bookedDate"));
                long registeredDate = cursor.getLong(cursor.getColumnIndex("registeredDate"));
                String serviceType = cursor.getString(cursor.getColumnIndex("serviceType"));
                int appointmentStatus = cursor.getInt(cursor.getColumnIndex("status"));
                float totalPrice = cursor.getFloat(cursor.getColumnIndex("totalPrice"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                String appointmentTrainerId = cursor.getString(cursor.getColumnIndex("trainerId"));
                String customerId = cursor.getString(cursor.getColumnIndex("customerId"));
                String paymentId = cursor.getString(cursor.getColumnIndex("paymentId"));
                long paymentDate = cursor.getLong(cursor.getColumnIndex("paymentDate"));
                String deviceToken = cursor.getString(cursor.getColumnIndex("deviceToken"));
                int rating = cursor.getInt(cursor.getColumnIndex("rating"));

                Appointment appointment = new Appointment(appointmentId, bookedDate, registeredDate, serviceType, appointmentStatus, totalPrice, location, appointmentTrainerId, customerId, paymentId, paymentDate, deviceToken);
                appointments.add(appointment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return appointments;
    }



    /* -------------------------------
    -----------ORDER HISTORY----------
    ------------------------------- */
    public void insertPayment(Payment payment){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues paymentInfo = new ContentValues();

        paymentInfo.put("_id",payment.getPaymentId());
        paymentInfo.put("paymentDate", payment.getPaymentDate());
        paymentInfo.put("finalPrice",payment.getFinalPrice());

        db.insert("payments", null, paymentInfo);

        db.close();
    }

    @SuppressLint("Range")
    public List<Payment> getUserOrderHistory(){

        List<Payment> orderHistory = new ArrayList<>();

        //First we need to get the unique paymentId
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM payments ORDER BY paymentDate DESC",null);

        if(cursor.moveToFirst()){
            do{
                String paymentId = cursor.getString(cursor.getColumnIndex("_id"));
                Long paymentDate = cursor.getLong(cursor.getColumnIndex("paymentDate"));
                Double finalPrice = cursor.getDouble(cursor.getColumnIndex("finalPrice"));
                Payment payment = new Payment(paymentId,paymentDate,finalPrice);
                orderHistory.add(payment);
            }while (cursor.moveToNext());
        }

        db.close();

        //Get the list of appointments/selfworkout per each payment
        for(Payment payment: orderHistory){
            List<Appointment> appList = getAppointmentsByPaymentId(payment.getPaymentId());
            List<SelfWorkoutPlanByUser> swpList = getSelfWorkoutPlanByUserByPaymentId(payment.getPaymentId());
            payment.setAppointmentList(appList);
            payment.setSelfWorkoutPlanByUserList(swpList);
        }

        return orderHistory;
    }



}
