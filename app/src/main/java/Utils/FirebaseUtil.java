package Utils;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

/**
 * Utility class for Firebase operations.
 */
public class FirebaseUtil {

    /**
     * Gets the current user ID from FirebaseAuth.
     *
     * @return The current user ID, or null if not logged in.
     */
    public static String currentUID() {
        return FirebaseAuth.getInstance().getUid();
    }

    /**
     * Checks if the user is currently logged in.
     *
     * @return True if the user is logged in, false otherwise.
     */
    public static boolean isLoggedIn() {
        return currentUID() != null;
    }

    /**
     * Gets the DatabaseReference for the current user's details in Firebase Database.
     *
     * @return A DatabaseReference pointing to the current user's details.
     */
    public static DatabaseReference currentUserDetails() {
        return FirebaseDatabase.getInstance().getReference().child("USERS").child(currentUID());
    }

    /**
     * Gets the DatabaseReference for the collection of all users in Firebase Database.
     *
     * @return A DatabaseReference pointing to the collection of all users.
     */
    public static DatabaseReference allUserCollectionReference() {
        return FirebaseDatabase.getInstance().getReference().child("USERS");
    }

    /**
     * Gets the DatabaseReference for the collection of all transfers in Firebase Database.
     *
     * @return A DatabaseReference pointing to the collection of all transfers.
     */
    public static DatabaseReference allTransferCollectionReference() {
        return FirebaseDatabase.getInstance().getReference().child("TRANSFERS");
    }

    /**
     * Logs out the current user from FirebaseAuth and clears user-specific data.
     */
    public static void logout() {
        FirebaseUtil.currentUserDetails().child("tokenMessage").setValue("");
        Globals.getInstance().setTransfers(null);
        Globals.getInstance().setUser(null);
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Saves the user's avatar image to Firebase Database.
     *
     * @param bitmap The avatar image as a Bitmap.
     */
    public static void saveAvatar(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        currentUserDetails().child("encodedAvatar").setValue(encoded);
        Globals.getInstance().getUser().setEncodedAvatar(encoded);
    }
}
