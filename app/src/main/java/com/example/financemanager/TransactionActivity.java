package com.example.financemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import models.Account;
import models.Category;
import Adapters.CourseGVAdapter;
import Utils.Globals;
import models.Transaction;
import models.User;


public class TransactionActivity extends AppCompatActivity {
    private User user;
    private Account account;
    private final long time = Globals.getInstance().getDayMidnight(Calendar.getInstance().getTimeInMillis());
    private long timeSelected=-1;
    private int accountID;
    private int categorySelected=-1;
    private View categoryLastSelected;
    private final ArrayList<String> accountsName = new ArrayList<>();
    private final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        GridView categoriesView = findViewById(R.id.gridCategories);
        TextView btnAcc = findViewById(R.id.btn_acc);
        Button newTrans = findViewById(R.id.newTransaction);
        Button today = findViewById(R.id.today);
        Button yesterday = findViewById(R.id.yesterday);
        Button dayBeforeYesterdayOrSelected = findViewById(R.id.dayBeforeYesterday);
        Button setDay = findViewById(R.id.setDay);
        EditText amount = findViewById(R.id.amount);

        defaultDates();

        user = Globals.getInstance().getUser();
        account = user.getAccounts().get(0);
        btnAcc.setText(account.getName());

        btnAcc.setOnClickListener(v -> {
            accountsName.clear();
            for (Account singleAccount : user.getAccounts()){
                accountsName.add(singleAccount.getName());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select account");

            String[] accountNamesArray = accountsName.toArray(new String[0]);
            builder.setItems(accountNamesArray, (dialog, which) -> {
                account = user.getAccounts().get(which);
                accountID = which;
                btnAcc.setText(accountNamesArray[which]);
            });
            builder.create().show();
        });



        ArrayList<Category> categories = user.getCategories();


        CourseGVAdapter adapter = new CourseGVAdapter(this, categories);
        categoriesView.setAdapter(adapter);

        categoriesView.setOnItemClickListener((parent, view, position, id) -> {
            if (categoryLastSelected != null){
                categoryLastSelected.findViewById(R.id.idTVCourse).setBackgroundColor(Color.TRANSPARENT);
            }

            view.findViewById(R.id.idTVCourse).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
            categoryLastSelected=view;
            categorySelected=position;
        });

        today.setOnClickListener(v -> {
            defaultDates();
            today.setBackgroundColor(getColor(R.color.selectedItem));
            timeSelected = Globals.getInstance().getDayMidnight(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis());
        });

        yesterday.setOnClickListener(v -> {
            defaultDates();
            yesterday.setBackgroundColor(getColor(R.color.selectedItem));
            timeSelected = Globals.getInstance().getDayMidnight(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis() - 86400000L);
        });

        dayBeforeYesterdayOrSelected.setOnClickListener(v -> {
            defaultDates();
            dayBeforeYesterdayOrSelected.setBackgroundColor(getColor(R.color.selectedItem));
            if (timeSelected + 86400000L*2 == time){
                return;
            } else if (timeSelected == -1) {
                timeSelected = Globals.getInstance().getDayMidnight(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis() - 86400000L * 2);
            } else if (timeSelected + 86400000L*2 < time){
                dayBeforeYesterdayOrSelected.setText(Globals.getInstance().calendarToMonthAndDate(calendar));
            }

        });

        setDay.setOnClickListener(v -> {
            defaultDates();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerDialogTheme,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year1);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        // Display a Toast message with the chosen date
                        timeSelected = Globals.getInstance().getDayMidnight(calendar.getTimeInMillis());
                        calendar.setTimeInMillis(Globals.getInstance().getDayMidnight(calendar.getTimeInMillis()));
                        Toast.makeText(this, calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
                        if (timeSelected + 86400000 == time) {
                            yesterday.performClick();
                        } else if (timeSelected == time){
                            today.performClick();
                        } else {
                            dayBeforeYesterdayOrSelected.performClick();
                        }
                    }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
            datePickerDialog.show();
        });


        newTrans.setOnClickListener(v -> {
            user.newTransaction(new Transaction(Integer.parseInt(amount.getText().toString()), accountID, user.getCategories().get(categorySelected), timeSelected));
            finish();
        });

    }

    /**
     * sets all buttons of date primary(default) color
     */
    private void defaultDates(){
        Button today = findViewById(R.id.today);
        Button yesterday = findViewById(R.id.yesterday);
        Button dayBeforeYesterdayOrSelected = findViewById(R.id.dayBeforeYesterday);

        Calendar calendar = Calendar.getInstance();

        today.setText(Globals.getInstance().calendarToMonthAndDate(calendar));
        today.setBackgroundColor(getColor(R.color.primaryColor));
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        yesterday.setText(Globals.getInstance().calendarToMonthAndDate(calendar));
        yesterday.setBackgroundColor(getColor(R.color.primaryColor));
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        dayBeforeYesterdayOrSelected.setText(Globals.getInstance().calendarToMonthAndDate(calendar));
        dayBeforeYesterdayOrSelected.setBackgroundColor(getColor(R.color.primaryColor));
    }
}