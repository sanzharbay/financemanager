package fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.financemanager.AuthActivity;
import com.example.financemanager.MainActivity;
import com.example.financemanager.OpenActivity;
import com.example.financemanager.R;

import java.io.InputStream;
import java.util.Locale;

import Utils.FirebaseUtil;
import Utils.Globals;

/**
 * ProfileFragment is responsible for handling user profile operations and UI interactions within the fragment.
 */
public class ProfileFragment extends Fragment {

    private ActivityResultLauncher<Intent> chooseImageFromGalleryLauncher, takeImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private int REQUEST_PERMISSION = -1;
    private AppCompatImageView profileImage;
    private AppCompatTextView fullName;
    private AppCompatTextView phoneNumber;
    private AppCompatButton biometrics;
    private SwitchCompat biometricsSwitch;
    private AppCompatButton language;
    private AppCompatButton changePIN;
    private AppCompatButton logOut;

    /**
     * Default constructor for ProfileFragment.
     * Required empty public constructor.
     */
    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        initLaunchers();
        fill();

        SharedPreferences sharedPref = getActivity().getSharedPreferences("Auth", Context.MODE_PRIVATE);
        biometricsSwitch.setChecked(sharedPref.getBoolean("biometrics", true));

        biometrics.setOnClickListener(v -> biometricsSwitch.setChecked(!biometricsSwitch.isChecked()));

        biometricsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("biometrics", isChecked);
            editor.apply();
        });

        profileImage.setOnClickListener(v -> onAvatarClicked());

        language.setOnClickListener(v -> {
            showLanguageSelectionDialog();
        });

        changePIN.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.putExtra("activity", "checkPIN");
            intent.putExtra("destiny", "toProfileCheck");
            startActivity(intent);
            getActivity().finish();
        });

        logOut.setOnClickListener(v -> {
            FirebaseUtil.logout();
            Intent intent = new Intent(getActivity(), OpenActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    /**
     * Handle the event when the avatar is clicked.
     */
    public void onAvatarClicked() {
        String[] options = new String[]{"Take photo", "Choose from gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your avatar");

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                REQUEST_PERMISSION = 2;
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else if (which == 1) {
                REQUEST_PERMISSION = 1;
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * Initialize all views.
     *
     * @param view the root view of the fragment.
     */
    private void init(View view) {
        fullName = view.findViewById(R.id.fullName);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        language = view.findViewById(R.id.language);
        changePIN = view.findViewById(R.id.pinCode);
        logOut = view.findViewById(R.id.signOut);
        biometrics = view.findViewById(R.id.biometrics);
        biometricsSwitch = view.findViewById(R.id.switchBiometrics);
        profileImage = view.findViewById(R.id.profileImage);
    }

    /**
     * Initialize ActivityResultLaunchers for handling permissions and activity results.
     */
    private void initLaunchers() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        if (REQUEST_PERMISSION == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            chooseImageFromGalleryLauncher.launch(intent);
                        } else if (REQUEST_PERMISSION == 2) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takeImageLauncher.launch(intent);
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.access_denied, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        chooseImageFromGalleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                            Bitmap bitmap = Globals.resizeBitmap(
                                    Globals.createSquareBitmap(
                                            BitmapFactory.decodeStream(is)));
                            FirebaseUtil.saveAvatar(bitmap);
                            profileImage.setImageBitmap(bitmap);
                        } catch (Exception ignored) {
                        }
                    }
                }
        );

        takeImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap bitmap = Globals.resizeBitmap(
                                Globals.createSquareBitmap(
                                        (Bitmap) data.getExtras().get("data")));
                        FirebaseUtil.saveAvatar(bitmap);
                        profileImage.setImageBitmap(bitmap);
                    }
                }
        );
    }

    /**
     * Fill the profile information with user details.
     */
    private void fill() {
        fullName.setText(Globals.getInstance().getUser().getFullName());
        phoneNumber.setText(Globals.getInstance().getUser().getPhoneNumber());
        Bitmap bitmap = Globals.decodeBase64ToBitmap(Globals.getInstance().getUser().getEncodedAvatar());
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap);
        }
    }

    /**
     * Show the language selection dialog.
     */
    private void showLanguageSelectionDialog() {
        String[] languages = {"English", "Russian", "Kazakh"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Language");
        builder.setItems(languages, (dialog, which) -> {
            switch (which) {
                case 0:
                    setLocale("en");
                    break;
                case 1:
                    setLocale("ru");
                    break;
                case 2:
                    setLocale("kk");
                    break;
            }
        });
        builder.show();
    }

    /**
     * Set the application locale.
     *
     * @param locale the locale to be set.
     */
    private void setLocale(String locale) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(locale.toLowerCase()));
        resources.updateConfiguration(config, dm);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("My_Lang", locale);
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("bottomNav", "profile");
        startActivity(intent);
        getActivity().finish();
    }
}
