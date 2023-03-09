package com.bawp.coachme.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
            "duration TEXT" +
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
            "FOREIGN KEY(selfWorkoutSessionTypeId) REFERENCES selfWorkoutPlans(_id)" +
            ");";

    private static final String CREATE_SELF_WORKOUT_PLANS_BY_USER_TABLE = "CREATE TABLE selfWorkoutPlansByUser(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "paymentDate DATETIME," +
            "requestedDate DATETIME," +
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

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SELF_WORKOUT_PLANS_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_SESSION_TYPES_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_PLAN_EXERCISES_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_PLANS_BY_USER_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_SESSIONS_TABLE);
        db.execSQL(CREATE_SELF_WORKOUT_SESSION_LOGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
