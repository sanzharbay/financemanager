package fragments;

import android.Manifest;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.financemanager.AuthActivity;
import com.example.financemanager.TransactionActivity;
import com.example.financemanager.R;
import com.example.financemanager.TransactionHistoryActivity;
import com.example.financemanager.TransferActivity;
import com.example.financemanager.TransferHistoryActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.ArrayList;

import Utils.FirebaseUtil;
import Utils.Globals;
import models.Account;
import models.Transfer;

/**
 * MainFragment is responsible for handling the main operations and UI interactions within the fragment.
 */
public class MainFragment extends Fragment {

    private ActivityResultLauncher<Intent> pdfFileLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private int REQUEST_PERMISSION = -1;
    private TextView amount, account;
    private CardView newTransaction, newTransfer, scanPDF, transactionHistory, transferHistory, scanButton;
    private Uri uri;
    int count = 0;
    private ArrayList<String> accountsName = new ArrayList<>();

    /**
     * Default constructor for MainFragment.
     * Required empty public constructor.
     */
    public MainFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view); // Initializing views
        initLaunchers(); // Initializing ActivityResultLaunchers
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    /**
     * Initialize all views and set up event listeners.
     *
     * @param view the root view of the fragment.
     */
    private void init(View view) {
        account = view.findViewById(R.id.account);
        amount = view.findViewById(R.id.amount);

        newTransaction = view.findViewById(R.id.newTransaction);
        newTransfer = view.findViewById(R.id.newTransfer);
        scanPDF = view.findViewById(R.id.openPDF);
        transactionHistory = view.findViewById(R.id.transactionHistory);
        transferHistory = view.findViewById(R.id.transferHistory);
        scanButton = view.findViewById(R.id.scanQR);

        account.setText(Globals.getInstance().getUser().getAccounts().get(0).getName());
        amount.setText(Globals.getInstance().getUser().getAccounts().get(0).getAmount() + "");

        account.setOnClickListener(v -> {
            accountsName.clear();
            for (Account singleAccount : Globals.getInstance().getUser().getAccounts()) {
                accountsName.add(singleAccount.getName());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select account");

            String[] accountNamesArray = accountsName.toArray(new String[0]);
            builder.setItems(accountNamesArray, (dialog, which) -> {
                account.setText(accountNamesArray[which]);
                amount.setText(Globals.getInstance().getUser().getAccounts().get(which).getAmount() + "");
            });
            builder.create().show();
        });

        newTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransactionActivity.class);
            startActivity(intent);
        });

        newTransfer.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransferActivity.class);
            startActivity(intent);
        });

        scanPDF.setOnClickListener(v -> {
            REQUEST_PERMISSION = 1;
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        });

        transactionHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
            startActivity(intent);
        });

        transferHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransferHistoryActivity.class);
            startActivity(intent);
        });

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.putExtra("activity", "transfer");
            startActivity(intent);
        });
    }

    /**
     * Initialize ActivityResultLaunchers for handling permissions and activity results.
     */
    private void initLaunchers() {
        pdfFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uri = data.getData();
                            uri.getPath();
                        }
                    }
                });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        if (REQUEST_PERMISSION == 1) {
                            Toast.makeText(getContext(), R.string.auth_error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Get the file path from a given URI.
     *
     * @param uri the URI of the file.
     * @return the file path.
     */
    public static String getFilePathFromUri(Uri uri) {//todo
        // Get the file ID from the content URI
        String documentId = uri.getLastPathSegment().split(":")[1];

        // Build the full content URI for the file
        Uri contentUri = MediaStore.Files.getContentUri("external");
        Uri fullUri = ContentUris.withAppendedId(contentUri, Long.parseLong(documentId));

        // Return the file path
        return fullUri.getPath();
    }
}
