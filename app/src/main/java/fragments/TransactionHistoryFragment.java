package fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import Adapters.TransactionAdapter;
import Utils.Globals;
import models.Transaction;

public class TransactionHistoryFragment extends Fragment {
    private ArrayList<Transaction> transactions = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private RecyclerView recyclerView;
    private RelativeLayout buttonLeft,  buttonRight;
    private int pageNumber;

    final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    long timeSelected = Globals.getInstance().getDayMidnight(Calendar.getInstance().getTimeInMillis()), timeSelectedEnd = timeSelected + 86399999L;
    private TextView pageHeader;
    private TransactionAdapter.OnTransactionClickListener transactionClickListener;
    long dayDiff = 0;

    public static TransactionHistoryFragment newInstance(int page) {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public TransactionHistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt("num") : 1;

        transactionClickListener = (transaction, position) -> {
            Toast.makeText(getContext(), transaction.getCategory().name,
                    Toast.LENGTH_SHORT).show();
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_data, container, false);
        pageHeader = view.findViewById(R.id.date);
        recyclerView = view.findViewById(R.id.list);

        transactions = Globals.getInstance().getUser().getTransactions();

        pageHeader.setOnClickListener(DateSelect);

        if (pageNumber == 0) {
            setAdapter(timeSelected, timeSelectedEnd);
        }
        if (pageNumber == 1){
            pageHeader.performClick();
        }

        return view;
    }

    View.OnClickListener DateSelect = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (pageNumber) {
                case 0:
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            (view, year1, monthOfYear, dayOfMonth) -> {
                                calendar.set(Calendar.YEAR, year1);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                // Display a Toast message with the chosen date
                                timeSelected = Globals.getInstance().getDayMidnight(calendar.getTimeInMillis());
                                timeSelectedEnd = timeSelected + 86399999L;
                                calendar.setTimeInMillis(Globals.getInstance().getDayMidnight(calendar.getTimeInMillis()));



                                setAdapter(timeSelected, timeSelectedEnd);
                            }, year, month, day);
                    datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis());
                    datePickerDialog.show();
                    break;
                case 1:
                    CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                    CalendarConstraints.DateValidator dateValidator = DateValidatorPointBackward.before(Calendar.getInstance().getTimeInMillis());
                    constraintsBuilder.setValidator(dateValidator);
                    CalendarConstraints constraints = constraintsBuilder.build();


                    MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                    builder.setTitleText("Select a date range");
                    builder.setCalendarConstraints(constraints);

                    // Building the date picker dialog
                    MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
                    datePicker.addOnPositiveButtonClickListener(selection -> {

                        // Retrieving the selected start and end dates
                        timeSelected = selection.first;
                        timeSelectedEnd = selection.second + 86399999L;
                        dayDiff = timeSelectedEnd - timeSelected + 1L;

                        // Formating the selected dates as strings


                        setAdapter(timeSelected, timeSelectedEnd);
                    });

                    // Showing the date picker dialog
                    datePicker.show(getActivity().getSupportFragmentManager(), "DATE_PICKER");
                    break;
            }

        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dayDiff = timeSelectedEnd - timeSelected + 1L;

        buttonLeft = view.findViewById(R.id.prevDate);
        buttonLeft.setOnClickListener(v -> {
            setAdapter(timeSelected - dayDiff, timeSelectedEnd - dayDiff);
        });

        buttonRight = view.findViewById(R.id.nextDate);
        buttonRight.setOnClickListener(v -> {
            setAdapter(timeSelected + dayDiff, timeSelectedEnd + dayDiff);
        });
    }

    private void setAdapter(long start, long end){
        if (buttonRight != null){
            if (end + dayDiff > Globals.getInstance().getDayMidnight(Calendar.getInstance().getTimeInMillis()) + 86399999L){
                buttonRight.setVisibility(View.INVISIBLE);
            } else {
                buttonRight.setVisibility(View.VISIBLE);
            }
        }

        String startDateString = sdf.format(new Date(start));
        if (end - start < 86400000){
            pageHeader.setText(startDateString);
        } else {
            String endDateString = sdf.format(new Date(end));

            // Creating the date range string
            String selectedDateRange = startDateString + " - " + endDateString;

            // Displaying the selected date range in the TextView
            pageHeader.setText(selectedDateRange);
        }
        ArrayList<Transaction> sortedTransactions = new ArrayList<>();
        for (Transaction t : transactions){
            if (t.getTime() >= start && t.getTime() <= end){
                sortedTransactions.add(t);
            }
        }
        TransactionAdapter adapter = new TransactionAdapter(getContext(), sortedTransactions, transactionClickListener);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);

        timeSelected = start;
        timeSelectedEnd = end;
    }
}
