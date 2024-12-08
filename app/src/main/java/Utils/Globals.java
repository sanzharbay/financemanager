package Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Base64;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Transfer;
import models.User;

/**
 * Utility class for global operations and data storage.
 */
public class Globals {
    private static Globals instance;
    private User user;
    private ArrayList<Transfer> transfers = new ArrayList<>();

    /**
     * Computes the SHA-256 hash of the input string.
     *
     * @param input The input string.
     * @return The SHA-256 hash as a byte array.
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available.
     */
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param hash The byte array.
     * @return The hexadecimal string representation of the hash.
     */
    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    /**
     * Computes the SHA-256 hash of the input string and returns it as a hexadecimal string.
     *
     * @param input The input string.
     * @return The SHA-256 hash as a hexadecimal string.
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available.
     */
    public static String Hashing(String input) throws NoSuchAlgorithmException {
        return toHexString(getSHA(input));
    }

    /**
     * Gets the list of transfers.
     *
     * @return The list of transfers.
     */
    public ArrayList<Transfer> getTransfers() {
        return transfers;
    }

    /**
     * Sets the list of transfers.
     *
     * @param transfers The list of transfers.
     */
    public void setTransfers(ArrayList<Transfer> transfers) {
        this.transfers = transfers;
    }

    /**
     * Sets the current user.
     *
     * @param user The current user.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Constructs a new Globals instance.
     */
    public Globals() { }

    /**
     * Gets the singleton instance of Globals.
     *
     * @return The singleton instance.
     */
    public static synchronized Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }

    /**
     * Checks if the input string is a valid phone number.
     *
     * @param phoneNumber The input phone number.
     * @return True if the input is a valid phone number, false otherwise.
     */
    public boolean isPhoneNumber(String phoneNumber) {
        Pattern p = Pattern.compile("[0-9]+");
        if (phoneNumber == null) {
            return false;
        }
        Matcher m = p.matcher(phoneNumber);
        return m.matches() && phoneNumber.length() == 10;
    }

    /**
     * Gets the time at midnight for the given time in milliseconds.
     *
     * @param timeInMillis The input time in milliseconds.
     * @return The time at midnight in milliseconds.
     */
    public long getDayMidnight(long timeInMillis) {
        return timeInMillis - timeInMillis % 86400000L;
    }

    /**
     * Gets the current user.
     *
     * @return The current user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Creates a square bitmap from the input bitmap by cropping it to a square.
     *
     * @param source The input bitmap.
     * @return The cropped square bitmap.
     */
    public static Bitmap createSquareBitmap(Bitmap source) {
        int originalWidth = source.getWidth();
        int originalHeight = source.getHeight();
        int squareDimension = Math.min(originalWidth, originalHeight);
        int xOffset = (originalWidth - squareDimension) / 2;
        int yOffset = (originalHeight - squareDimension) / 2;
        return Bitmap.createBitmap(source, xOffset, yOffset, squareDimension, squareDimension);
    }

    /**
     * Resizes a bitmap to a resolution of 100x100 pixels.
     *
     * @param source The original bitmap to be resized.
     * @return A new bitmap with 100x100 resolution.
     */
    public static Bitmap resizeBitmap(Bitmap source) {
        int width = 100;
        int height = 100;
        return Bitmap.createScaledBitmap(source, width, height, true);
    }

    /**
     * Decodes a Base64-encoded string to a Bitmap.
     *
     * @param base64String The Base64-encoded string.
     * @return The decoded Bitmap, or null if decoding fails.
     */
    public static Bitmap decodeBase64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Converts a Calendar instance to a string with month and date.
     *
     * @param calendar The Calendar instance.
     * @return A string in the format "MMM/dd".
     */
    public String calendarToMonthAndDate(Calendar calendar) {
        String inputDateString = calendar.getTime().toString();
        String month = inputDateString.substring(4, 7);
        String day = inputDateString.substring(8, 10);
        return month + "/" + day;
    }

    /**
     * Removes the last character from a string.
     *
     * @param str The input string.
     * @return The string without its last character.
     */
    public static String removeLastChar(String str) {
        if (str != null && !str.isEmpty()) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * Adds a stroke to a bitmap.
     *
     * @param bitmap The original bitmap.
     * @param strokeWidth The width of the stroke.
     * @param strokeColor The color of the stroke.
     * @return A new bitmap with the stroke added.
     */
    public static Bitmap addStrokeToBitmap(Bitmap bitmap, int strokeWidth, int strokeColor) {
        int widthWithStroke = bitmap.getWidth() + strokeWidth * 2;
        int heightWithStroke = bitmap.getHeight() + strokeWidth * 2;
        Bitmap bitmapWithStroke = Bitmap.createBitmap(widthWithStroke, heightWithStroke, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithStroke);
        canvas.drawBitmap(bitmap, strokeWidth, strokeWidth, null);

        Paint paint = new Paint();
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);

        canvas.drawRect(strokeWidth / 2, strokeWidth / 2, widthWithStroke - strokeWidth / 2, heightWithStroke - strokeWidth / 2, paint);
        return bitmapWithStroke;
    }
}
