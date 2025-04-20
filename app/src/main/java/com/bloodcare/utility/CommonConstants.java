package com.bloodcare.utility;

/**
 * Created by osamazeeshan on 03/09/2018.
 */

public final class CommonConstants {

    public final static int PASSWORD_MIN_LEN = 6;
    public final static int CELL_PHONE_MIN_LEN = 6;
    public final static int AGE_MIN_LEN = 1;
    public final static int NAME_MIN_LEN = 3;
    public final static int VIEW_CORNER_RADIUS = 32;
    public final static String COLOR_BLACK_STRING = "#000000";

    public final static String BLOOD_TYPE_A_POS = "A+";
    public final static String BLOOD_TYPE_A_NEG = "A-";
    public final static String BLOOD_TYPE_B_POS = "B+";
    public final static String BLOOD_TYPE_B_NEG = "B-";
    public final static String BLOOD_TYPE_O_POS = "O+";
    public final static String BLOOD_TYPE_O_NEG = "O-";
    public final static String BLOOD_TYPE_AB_POS = "AB+";
    public final static String BLOOD_TYPE_AB_NEG = "AB-";

    public final static String HOSPITAL_TYPE_ALL_HOSPITAL = "Hospital";
    public final static String HOSPITAL_TYPE_BLOOD_BANK = "BloodBank";
    public final static String HOSPITAL_TYPE_LUKEMIA = "Leukemia";
    public final static String HOSPITAL_TYPE_THALYSEMMIA = "Thalassemia";
    public final static String HOSPITAL_TYPE_HAEMOPHILLIA = "Hemophilia";

    public final static int GOOGLE_AUTH_REQ_CODE = 9001;

    public final static String DOC_TYPE_CARDIO = "Cardiologist";
    public final static String DOC_TYPE_UROLOGISTS = "Urologist";
    public final static String DOC_TYPE_GENERAL_SURGEON = "General Surgeons";
    public final static String DOC_TYPE_RHEUMATOLOGISTS = "Rheumatologists";
    public final static String DOC_TYPE_RADIOLOGISTS= "Radiologists";
    public final static String DOC_TYPE_PULMONOLOGISTS = "Pulmonologists";
    public final static String DOC_TYPE_PLASTIC_SURGEON = "Plastic Surgeons";
    public final static String DOC_TYPE_Physician_Gastroenterologist = "Physician & Gastroenterologist";
    public final static String DOC_TYPE_Physiotherapist = "Physiotherapist";
    public final static String DOC_TYPE_PATHOLOGISTS = "Pathologists";
    public final static String DOC_TYPE_ORTHOPEDIC_SURGEON = "Orthopedic surgeons";
    public final static String DOC_TYPE_DERMATOLOGISTS = "Dermatologists";
    public final static String DOC_TYPE_GYNECOLOGIST = "Gynecologist";
    public final static String DOC_TYPE_SPINAL_SURGEON = "Spinal Surgeon";
    public final static String DOC_TYPE_Hematologist = "Hematologist";
    public final static String DOC_TYPE_Neurophysician = "Neurophysician";
    public final static String DOC_TYPE_Neurosurgeon = "Neurosurgeon";
    public final static String DOC_TYPE_Nephrologists = "Nephrologists";
    public final static String DOC_TYPE_Nutritionist = "Nutritionist";
    public final static String DOC_TYPE_Anesthesiologist = "Anesthesiologist";
    public final static String DOC_TYPE_Cardiac_Surgeon = "Cardiac Surgeon";
    public final static String DOC_TYPE_Cardiac_Electro_Physiologist = "Cardiac Electro Physiologist";
    public final static String DOC_TYPE_General_Physician = "General Physician";
    public final static String DOC_TYPE_General_Vascular_Laparoscopic_Surgeon = "General, Vascular & Laparoscopic Surgeon ";
    public final static String DOC_TYPE_Dental_Surgeon = "Dental Surgeon";
    public final static String DOC_TYPE_Interventional_Cardiologist = "Interventional Cardiologist";
    public final static String DOC_TYPE_Interventional_Neuroradiologist = "Interventional Neuroradiologist";
    public final static String DOC_TYPE_Cosmetologist = "Cosmetologist";
    public final static String DOC_TYPE_Histopathologist = "Histopathologist";
    public final static String DOC_TYPE_Trauma_Orthopedic_Surgeon = "Trauma and Orthopedic Surgeon ";
    public final static String DOC_TYPE_Endocrinologist = "Endocrinologist";
    public final static String DOC_TYPE_ENT_Surgeon = "ENT Surgeon";

    public final static String REQUEST_ID_KEY = "requestId";
    public final static String REQUESTER_ID_KEY = "requesterId";
    public final static String REQUESTER_BLOOD_TYPE_KEY = "requesterBloodType";
    public final static String REQUESTER_NAME_KEY = "requesterName";
    public final static String REQUEST_TIME_STAMP = "timestamp";

    public final static int USER_REQUEST_PENDING = 1;
    public final static int USER_REQUEST_RECEIVED_BLOOD = 2;
    public final static int USER_REQUEST_DO_NOT_RECEIVED_BLOOD = 3;
    public final static int USER_REQUEST_CANCELED = 4;
    public final static int USER_REQUEST_NO_DONOR_AVAILABLE = 5;

    public final static int USER_DONATE_PENDING = 1;
    public final static int USER_DONATE_BLOOD = 2;
    public final static int USER_DO_NOT_DONATE_BLOOD = 3;

    public final static String APP_FIREBASE_USER_AUTHENTICATE = "FirebaseUserAuthenticate";
    public final static String CHECK_REQUEST_COMPLETE = "CheckRequestComplete";
    public final static String DONOR_ACCEPT_REQUESTER_ID = "DonorAcceptData";
    public final static String REQUEST_ID = "RequestId";

    public final static int USE_FRAGMENT_MANAGER_SUPPORT = 0;
    public final static int USE_FRAGMENT_MANAGER_PARENTS_CHILD = 1;
    public final static int USE_FRAGMENT_MANAGER_CHILD = 2;

    public final static int REQUEST_HOME_SCREEN = 1;
    public final static int REQUEST_BLOOD_CONFIRM_SCREEN = 2;
    public final static int REQUEST_LOCATION_CONFIRM_SCREEN = 3;
    public final static int REQUEST_BLOOD_SEARCH_SCREEN = 4;
    public final static int REQUEST_ACCEPT_DONOR_SCREEN = 5;
    public final static int REQUEST_ACCEPT_DONOR_LIST_FRAGMENT = 6;
    public final static int REQUEST_ACCEPT_CUSTOM_DIALOG_BOX_FRAGMENT = 7;
    public final static int DONOR_ACCEPT_SCREEN = 8;
    public final static int HOSPITAL_MAIN_SCREEN = 9;
    public final static int HOSPITAL_NEARBY_SCREEN = 10;
    public final static int DOCTOR_MAIN_SCREEN = 11;
    public final static int DOCTOR_NEARBY_SCREEN = 12;
    public final static int HOSPITAL_DIALOG_SCREEN = 13;
    public final static int DOCTOR_DETAILS_SCREEN = 14;

    public final static int MAP_CAMERA_ON_MOVE = 1;
    public final static int MAP_CAMERA_STOP = 2;

}
