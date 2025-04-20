package com.bloodcare.utility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bloodcare.R;
import com.bloodcare.activity.MainActivity;
import com.bloodcare.fragment.history.DonateInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;


public class CommonUtil {

    private static ProgressDialog mProgressDialog = null;
    private static final int PERMISSIONS_REQUEST = 1;

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.length() == 0);
    }

    public static void showDialog(Context context, String title, String message, int iconId, String positiveButton, String negativeButton, final AlertDialogOnClickListener listener) {
        if (context == null) {
            return;
        }
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setIcon(iconId);

            if (!isNullOrEmpty(positiveButton)) {
                alertDialogBuilder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (listener != null) {
                                listener.onClick(dialog, which);
                            }
                            dialog.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            if (!isNullOrEmpty(positiveButton)) {
                alertDialogBuilder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (listener != null) {
                                listener.onClick(dialog, which);
                            }
                            dialog.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<GeoPoint> calculateNearbyLocation(int distance, double latitude, double longitude) {
        try {
            double lat = 0.0144927536231884;
            double lon = 0.0181818181818182;

            double lowerLat = latitude - (lat * distance);
            double lowerLon = longitude - (lon * distance);

            double greaterLat = latitude + (lat * distance);
            double greaterLon = longitude + (lon * distance);

            GeoPoint lesserGeoPoint = new GeoPoint(lowerLat, lowerLon);
            GeoPoint greaterGeoPoint = new GeoPoint(greaterLat, greaterLon);

            ArrayList<GeoPoint> geoArray = new ArrayList<>();
            geoArray.add(lesserGeoPoint);
            geoArray.add(greaterGeoPoint);

            return geoArray;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setRoundedBackground(Context context, View view, String backgroundColor, int radius, String borderColor, int borderWidth) {
        if (view == null || CommonUtil.isNullOrEmpty(backgroundColor)) {
            return;
        }
        try {
            GradientDrawable gdDefault = new GradientDrawable();
            gdDefault.setColor(Color.parseColor(backgroundColor));
            gdDefault.setCornerRadius(radius);
            if (borderWidth > 0 && !CommonUtil.isNullOrEmpty(borderColor)) {
                borderWidth = (int) CommonUtil.convertDpToPixel(borderWidth, context);
                gdDefault.setShape(GradientDrawable.RECTANGLE);
                gdDefault.setStroke(borderWidth, Color.parseColor(borderColor));
            }
            setViewBackground(view, gdDefault);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPendingStatus(ArrayList<DonateInfo> arrayList) {
        if(arrayList== null) {
            return true;
        }
        try {
            for(DonateInfo donateInfo : arrayList) {
                if(donateInfo.getDonateStatus().equals("1")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    public static boolean isBloodReceived(ArrayList<DonateInfo> arrayList) {
        if(arrayList== null) {
            return false;
        }
        try {
            for(DonateInfo donateInfo : arrayList) {
                if(donateInfo.getDonateStatus().equals("2")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static String getDocDesWithID(Context context, int id) {
        try {
            String docDesc = "";
            if(id == 1) {
                docDesc = context.getString(R.string.cardiologist_toast_text);
            } else if(id == 2) {
                docDesc = context.getString(R.string.physician_gastroenterologist_toast_text);
            } else if(id == 3) {
                docDesc = context.getString(R.string.dermatologist_toast_text);
            } else if(id == 4) {
                docDesc = context.getString(R.string.physiotherapist_toast_text);
            } else if(id == 5) {
                docDesc = context.getString(R.string.gynecologist_toast_text);
            } else if(id == 6) {
                docDesc = context.getString(R.string.spinal_surgeon_toast_text);
            } else if(id == 7) {
                docDesc = context.getString(R.string.orthopedic_surgeon_toast_text);
            } else if(id == 8) {
                docDesc = context.getString(R.string.pathologist_toast_text);
            } else if(id == 9) {
                docDesc = context.getString(R.string.hematologist_toast_text);
            } else if(id == 10) {
                docDesc = context.getString(R.string.neurophysician_toast_text);
            } else if(id == 11) {
                docDesc = context.getString(R.string.plastic_surgeon_toast_text);
            } else if(id == 12) {
                docDesc = context.getString(R.string.neurosurgeon_toast_text);
            } else if(id == 13) {
                docDesc = context.getString(R.string.pulmonologist_toast_text);
            } else if(id == 14) {
                docDesc = context.getString(R.string.radiologist_toast_text);
            } else if(id == 15) {
                docDesc = context.getString(R.string.rheumatologist_toast_text);
            } else if(id == 16) {
                docDesc = context.getString(R.string.urologist_toast_text);
            } else if(id == 17) {
                docDesc = context.getString(R.string.general_surgeon_toast_text);
            } else if(id == 18) {
                docDesc = context.getString(R.string.nephrologists_toast_text);
            } else if(id == 19) {
                docDesc = context.getString(R.string.nutritionist_toast_text);
            } else if(id == 20) {
                docDesc = context.getString(R.string.anesthesiologist_toast_text);
            } else if(id == 21) {
                docDesc = context.getString(R.string.cardiac_surgeon_toast_text);
            } else if(id == 22) {
                docDesc = context.getString(R.string.cardiac_electro_physiologist_toast_text);
            } else if(id == 23) {
                docDesc = context.getString(R.string.general_physician_toast_text);
            } else if(id == 24) {
                docDesc = context.getString(R.string.general_vascular_laparoscopic_surgeon_toast_text);
            } else if(id == 25) {
                docDesc = context.getString(R.string.dental_surgeon_toast_text);
            } else if(id == 26) {
                docDesc = context.getString(R.string.interventional_toast_text);
            } else if(id == 27) {
                docDesc = context.getString(R.string.interventional_neuroradiologist_toast_text);
            } else if(id == 28) {
                docDesc = context.getString(R.string.cosmetologist_toast_text);
            } else if(id == 29) {
                docDesc = context.getString(R.string.histopathologist_toast_text);
            } else if(id == 30) {
                docDesc = context.getString(R.string.trauma_orthopedic_surgeon_toast_text);
            } else if(id == 31) {
                docDesc = context.getString(R.string.endocrinologist_toast_text);
            } else if(id == 32) {
                docDesc = context.getString(R.string.ent_surgeon_toast_text);
            }
            return docDesc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void dialNumber(FragmentActivity fragmentActivity, String number) {
        if (fragmentActivity == null || CommonUtil.isNullOrEmpty(number)) {
            return;
        }
        try {
            String url = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            if (ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fragmentActivity.startActivity(intent);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(FragmentActivity fragmentActivity, String number) {
        if(fragmentActivity == null || CommonUtil.isNullOrEmpty(number)) {
            return;
        }
        try {
            String url = "sms:" + number;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.putExtra("sms_body", fragmentActivity.getString(R.string.sms_body_text));
            fragmentActivity.startActivity(intent);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        if(context == null) {
            return 0;
        }
        try {
            Resources resources = context.getResources();
            if(resources == null) {
                return 0;
            }
            DisplayMetrics metrics = resources.getDisplayMetrics();
            if(metrics == null) {
                return 0;
            }
            return dp * (metrics.densityDpi / 160f);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getAddressFromLocation(Activity activity, LatLng location) {
        try {
            List<Address> addresses;
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();

//            String house = addresses.get(0).getSubThoroughfare();
            String street = addresses.get(0).getThoroughfare();
            String locality = addresses.get(0).getSubLocality();
            String[] firstAddress = address.split(",");
            return String.format("%s", firstAddress[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Point getCenterLocationOfScreen(Activity activity) {
        if(activity == null) {
            return null;
        }
        try {
            DisplayMetrics dimension = getDisplayMetrics(activity);
            int y = (dimension.heightPixels / 2);// - 20;
            int x = dimension.widthPixels / 2;
            return new Point(x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri getUriFromFile(String file) {
        if(isNullOrEmpty(file)) {
            return null;
        }
        try {
            Uri uri = Uri.fromFile(new File(file));
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight, boolean makeRounded) {
        if(source == null) {
            return null;
        }
        try {
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            RectF targetRect;
            Bitmap destination;
            if(newWidth != sourceWidth || newHeight != sourceHeight) {
                Matrix matrix = new Matrix();
                if(sourceWidth > sourceHeight) {
                    matrix.postRotate(90);
                }

                int left, top, width, height;
                if(sourceWidth >= sourceHeight) {
                    left = (sourceWidth - sourceHeight) / 2;
                    top = 0;
                    width = sourceHeight;
                    height = sourceHeight;
                } else {
                    left = 0;
                    top = (sourceHeight - sourceWidth) / 2;
                    width = sourceWidth;
                    height = sourceWidth;
                }

                if(width <= 0 || height <= 0) {
                    return null;
                }

                if(sourceWidth > sourceHeight) {
                    destination = Bitmap.createBitmap(source, left, top, width, height, matrix, false);
                } else {
                    destination = Bitmap.createBitmap(source, left, top, width, height);
                }

                float xScale = ((float) newWidth) / width;
                float yScale = ((float) newHeight) / height;
                float scale = Math.max(xScale, yScale);
                float scaledWidth = scale * width;
                float scaledHeight = scale * height;
                targetRect = new RectF(0, 0, scaledWidth, scaledHeight);
            } else {
                targetRect = new RectF(0, 0, newWidth, newHeight);
                destination = source;
            }

            if(newWidth <= 0 || newHeight <= 0) {
                return null;
            }

            //Finally, we create a new bitmap of the specified size and draw our new, scaled bitmap onto it.
            Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
            Canvas canvas = new Canvas(dest);
            canvas.drawARGB(0, 0, 0, 0);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(0xffffffff);
            if(makeRounded) {
                RectF rect = new RectF(0, 0, newWidth, newHeight);
                canvas.drawRoundRect(rect, newWidth / 2.0f, newHeight / 2.0f, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            }
            canvas.drawBitmap(destination, null, targetRect, paint);

            return dest;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap cropMapSnapShot(Bitmap source, int newWidth, int newHeight, int marginTop) {
        try {

            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            Bitmap dstBmp = Bitmap.createBitmap(source, 0,sourceHeight/2 - marginTop, sourceWidth, newHeight);
            return dstBmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getByteFromBitmap(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            return displaymetrics;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    public static String getRequestStatus(long statusCode) {
        if(statusCode == CommonConstants.USER_REQUEST_PENDING) {
            return "Pending";
        } else if (statusCode == CommonConstants.USER_REQUEST_RECEIVED_BLOOD) {
            return "Blood Received";
        } else if (statusCode ==CommonConstants.USER_REQUEST_DO_NOT_RECEIVED_BLOOD) {
            return "Blood Not Received";
        } else if (statusCode == CommonConstants.USER_REQUEST_CANCELED) {
            return "Canceled";
        } else if (statusCode == CommonConstants.USER_REQUEST_NO_DONOR_AVAILABLE) {
            return "No Donor Available";
        }
        return null;
    }

    public static String getDonateStatus(long statusCode) {
        if(statusCode == CommonConstants.USER_DONATE_PENDING) {
            return "Pending";
        } else if (statusCode == CommonConstants.USER_DONATE_BLOOD) {
            return "Blood Donated";
        } else if (statusCode ==CommonConstants.USER_DO_NOT_DONATE_BLOOD) {
            return "Blood Not Donated";
        }
        return null;
    }

    public static String validateSpinnerText(Context context, Fragment fragment, Spinner spinnerText, int hintId) {
        if(context == null || fragment == null || spinnerText == null) {
            return null;
        }
        try {
            String input = spinnerText.getSelectedItem().toString();
            if(input.equals(context.getString(R.string.gender_text))) {
                String message = fragment.getString(hintId).toUpperCase() + " " + fragment.getString(R.string.not_valid);
                showDialog(context, fragment.getString(R.string.dialog_title_error), message, android.R.drawable.ic_dialog_alert, fragment.getString(R.string.dialog_button_ok), null, null);
                return null;
            }
            if(input.equals(context.getString(R.string.blood_type_text))) {
                String message = fragment.getString(hintId).toUpperCase() + " " + fragment.getString(R.string.not_valid);
                showDialog(context, fragment.getString(R.string.dialog_title_error), message, android.R.drawable.ic_dialog_alert, fragment.getString(R.string.dialog_button_ok), null, null);
                return null;
            }
            return input;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String validateInputText(Context context, Fragment fragment, EditText editText, int hintId, int minLen, boolean checkEmail, boolean checkCellNo) {
        if(context == null || fragment == null || editText == null) {
            return null;
        }
        try {
            Editable editable = editText.getText();
            if(editable == null) {
                showDialog(context, fragment.getString(R.string.dialog_title_error), fragment.getString(R.string.error_occurred), android.R.drawable.ic_dialog_alert, fragment.getString(R.string.dialog_button_ok), null, null);
                return null;
            }

            String input = checkAndTrimString(editable.toString());
            if(input == null) {
                String message = fragment.getString(hintId).toUpperCase() + " " + fragment.getString(R.string.can_not_empty);
                showDialogAndSetFocus(context, fragment, editText, message);
                return null;
            }

            if(input.length() < minLen) {
                String message = String.format(fragment.getString(R.string.not_less_then), minLen);
                message = fragment.getString(hintId).toUpperCase() + " " + message;
                showDialogAndSetFocus(context, fragment, editText, message);
                return null;
            }

            if(checkCellNo) {
                if(!isGlobalPhoneNumber(input)) {
                    String message = fragment.getString(hintId).toUpperCase() + " " + fragment.getString(R.string.not_valid);
                    showDialogAndSetFocus(context, fragment, editText, message);
                    return null;
                }
            }

            if(checkEmail) {
                if(!checkEmail(input)) {
                    String message = fragment.getString(hintId).toUpperCase() + " " + fragment.getString(R.string.not_valid);
                    showDialogAndSetFocus(context, fragment, editText, message);
                    return null;
                }
            }

//            if(checkBloodGroup) {
//
//            }

            return input;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String checkAndTrimString(String string) {
        if(string == null) {
            return null;
        }
        try {
            string = string.trim();
            if(string.length() == 0) {
                return null;
            }
            return string;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkEmail(String email) {
        if(email == null) {
            return false;
        }
        try {
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            return pattern.matcher(email).matches();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showDialogAndSetFocus(Context context, Fragment fragment, EditText editText, String message) {
        if(context == null || fragment == null || editText == null || message == null) {
            return;
        }
        try {
            showDialog(context, fragment.getString(R.string.dialog_title_error), message, android.R.drawable.ic_dialog_alert, fragment.getString(R.string.dialog_button_ok), null, null);
            editText.clearFocus();
            editText.requestFocus();
            editText.setFocusable(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setViewBackground(View view, Drawable bitmapDrawable) {
        if(view == null) {
            return;
        }
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(bitmapDrawable);

            } else {
                view.setBackground(bitmapDrawable);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public static void showProgressDialog(Context context, String message, boolean show) {
        if (context == null) {
            return;
        }
        try {
            if (show) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                mProgressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_title_wait), message, true);
                mProgressDialog.setCancelable(false);
            } else {
                if (mProgressDialog == null) {
                    return;
                }
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap drawNumberInsideMarker(Bitmap markerBitmap, String distance, int textColor, float textSize, float x, float y) {
        try {
            Bitmap mutableBitmap = markerBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(textColor);
            paint.setTextSize(textSize);//26.0f
            paint.setFakeBoldText(true);
            canvas.drawText(distance, x, y, paint); //8, 100 paint defines the text color, stroke width, size
            return mutableBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getIntegerResource(Context context, int id) {
        if(context == null) {
            return 0;
        }
        try {
            Resources resources = context.getResources();
            if(resources == null) {
                return 0;
            }
            return resources.getInteger(id);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public interface AlertDialogOnClickListener {
        public void onClick(DialogInterface dialog, int which);
    }

}
