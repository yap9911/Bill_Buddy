package my.edu.utar.bill_splitting_app;

// Combination breakdown activity

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity5 extends AppCompatActivity {

    private final List<RadioGroup> RadioGroupList = new ArrayList<>();              // List to store the radioGroup
    private final List<Integer> amountRadioButtonIdList = new ArrayList<>();        // List to store the amountRadioButton id
    private final List<Integer> percentRadioButtonIdList = new ArrayList<>();       // List to store the percentRadioButton id
    private final List<EditText> amountOrPercentEditTextList = new ArrayList<>();   // List to store the amountOrPercentEditText views
    private final List<TextView> resultViewList = new ArrayList<>();                // List to store the result TextViews
    private final List<EditText> buddiesNameList = new ArrayList<>();               // List to store the name of each buddy
    private double billAmount;
    private int buddiesCount;
    private boolean isCalculatedAndTotalSumMatches = false;
    private StringBuilder strFileContent = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Check if the Intent is not null and contains extras
        if (intent != null) {
            // Retrieve extras from the Intent
            buddiesCount = intent.getIntExtra("buddiesCountExtra", 0);
            billAmount = intent.getDoubleExtra("billAmountExtra", 0.0);
            TextView billAmountDisplay = findViewById(R.id.tv_billAmountDisplay);
            billAmountDisplay.setText("The total bill amount is " + billAmount);

            TableLayout tableLayout = findViewById(R.id.table);


            // Create and add the rows based on the buddiesCount
            for (int i = 0; i < buddiesCount; i++) {
                TableRow BuddyRow = new TableRow(this);
                BuddyRow.setPadding(16, 16, 16, 16);
                EditText buddyNameEt = new EditText(this);
                buddyNameEt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                buddiesNameList.add(buddyNameEt);

                // Store the generated IDs when creating RadioButtons
                int amountRadioButtonId = View.generateViewId();
                int percentRadioButtonId = View.generateViewId();

                // Create the RadioGroup and two RadioButtons
                RadioGroup radioGroup = new RadioGroup(this);
                RadioButton amountRadioButton = new RadioButton(this);
                amountRadioButton.setId(amountRadioButtonId);
                amountRadioButtonIdList.add(amountRadioButtonId);
                amountRadioButton.setText("Amount");
                amountRadioButton.setTextSize(14);

                RadioButton percentRadioButton = new RadioButton(this);
                percentRadioButton.setId(percentRadioButtonId);
                percentRadioButtonIdList.add(percentRadioButtonId);
                percentRadioButton.setText("Percentage");
                percentRadioButton.setTextSize(14);

                // Add the RadioButtons to the RadioGroup
                radioGroup.addView(amountRadioButton);
                radioGroup.addView(percentRadioButton);
                radioGroup.setPadding(16, 0, 0, 0);
                RadioGroupList.add(radioGroup);

                EditText amountOrPercent = new EditText(this);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // Check which RadioButton is selected and update the InputType of the EditText
                        if (checkedId == amountRadioButtonId) {
                            // If amount is selected, allow decimal input with 2 decimal places
                            amountOrPercent.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                            amountOrPercent.setFilters(new InputFilter[]{new MainActivity.DecimalDigitsInputFilter(2)});
                            amountOrPercent.setText(""); // Clear the current text when switching to "Amount"
                        } else if (checkedId == percentRadioButtonId) {
                            // If percentage is selected, allow integer input only
                            amountOrPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
                            amountOrPercent.setFilters(new InputFilter[]{new MainActivity.DecimalDigitsInputFilter(0)});
                            amountOrPercent.setText(""); // Clear the current text when switching to "Percentage"
                        }
                    }
                });

                // Set the default selection
                amountRadioButton.setChecked(true);

                // Add the amount or percent to the list
                amountOrPercentEditTextList.add(amountOrPercent);

                TextView resultView = new TextView(this);
                resultViewList.add(resultView);
                resultView.setPadding(32, 0, 0, 0);
                BuddyRow.addView(buddyNameEt);
                BuddyRow.addView(radioGroup);
                BuddyRow.addView(amountOrPercent);
                BuddyRow.addView(resultView);
                tableLayout.addView(BuddyRow);
            }

            TableRow sumRow = new TableRow(this);

            TextView sumView = new TextView(this);

            // Right align the text view
            sumView.setGravity(Gravity.END);
            sumView.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            ));

            sumView.setText("Total: 0.00");

            sumRow.addView(sumView);
            tableLayout.addView(sumRow);

            // Create and add a new TableRow for the button
            TableRow buttonRow = new TableRow(this);

            // Create the button
            Button myButton = new Button(this);
            // Center the button
            myButton.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            ));

            myButton.setText("Calculate");

            // Add the button to the TableRow
            buttonRow.addView(myButton);

            // Add the TableRow to the TableLayout
            tableLayout.addView(buttonRow);

            // Calculate and set the results when the button is clicked
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isCalculatedAndTotalSumMatches = calculateBillForEachBuddyAndCheckTotalSum(billAmount, resultViewList, sumView);
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.done_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (isCalculatedAndTotalSumMatches) {

            FileOutputStream fos;
            String filename = "Records.txt";

            try {
                // Use MODE_APPEND to open the file in append mode
                fos = openFileOutput(filename, MODE_APPEND);

                // Get the current date and time
                Calendar calendar = Calendar.getInstance();
                Date currentTime = calendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String formattedDateTime = formatter.format(currentTime);

                // Create a formatted string to store date and time, the billAmount and buddy payments
                strFileContent = new StringBuilder();
                strFileContent.append("---------------------------------------------").append("\n");
                strFileContent.append(formattedDateTime).append("\n");
                strFileContent.append("Bill Amount: ").append(billAmount).append("\n");

                for (int i = 0; i < buddiesCount; i++) {
                    String buddyName = buddiesNameList.get(i).getText().toString();
                    String buddyPayment = resultViewList.get(i).getText().toString();
                    strFileContent.append(buddyName).append(" ").append(buddyPayment).append("\n");
                }

                // Write the formatted string to the file
                fos.write(strFileContent.toString().getBytes());

                // Close the FileOutputStream after writing
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            PopUpWindow();

        } else {
            Toast.makeText(MainActivity5.this, "Please make sure each row is filled up" +
                            ",the result is calculated and the total amount input matches the bill amount .",
                    Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // A method to calculate the total bill amount to be paid by each buddy and check the total sum of amount input by user
    public boolean calculateBillForEachBuddyAndCheckTotalSum(double billAmount, List<TextView> resultViewList, TextView totalView) {

        double totalAmountInput = 0;


        // convert the string into double or integer
        for (int i = 0; i < buddiesCount; i++) {

            String amountOrPercent = amountOrPercentEditTextList.get(i).getText().toString();
            String Name = buddiesNameList.get(i).getText().toString();

            // check if each buddy's name is entered
            if (TextUtils.isEmpty(Name)) {
                Toast.makeText(MainActivity5.this, "Please make sure each buddy's name is filled up.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (RadioGroupList.get(i).getCheckedRadioButtonId() == amountRadioButtonIdList.get(i)) {    // if amount radio button is chosen, convert the string into double

                try {
                    double convertedAmount = Double.parseDouble(amountOrPercent);
                    totalAmountInput += convertedAmount;      // add to the totalAmountInput
                    resultViewList.get(i).setText(String.format("%.2f", convertedAmount));
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity5.this, "Please make sure all the amount or percent are filled up.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (RadioGroupList.get(i).getCheckedRadioButtonId() == percentRadioButtonIdList.get(i)) {    // if percentage radio button is chosen, convert the string into integer
                try {
                    int convertedPercent = Integer.parseInt(amountOrPercent);
                    double result = billAmount * ((double) convertedPercent / 100);

                    // Check if there are exactly three decimal places
                    String resultStr = String.format("%.3f", result);
                    int decimalIndex = resultStr.indexOf(".");
                    int numDecimalPlaces = resultStr.length() - decimalIndex - 1;

                    if (numDecimalPlaces == 3) {
                        // If there are exactly three decimal places, round up to two decimal places
                        result = Math.ceil(result * 100) / 100;
                    }

                    totalAmountInput += result;     // add to the totalAmountInput
                    resultViewList.get(i).setText(String.format("%.2f", result));

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity5.this, "Please make sure amount or percent are filled up.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        totalView.setText("Total: " + totalAmountInput);

        return totalAmountInput == billAmount;
    }

    // method to open MainActivity
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Pop-up window for sharing purpose
    public void PopUpWindow(){

        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popupwindowlayout, null);

        // Create a PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Find the buttons within the popup layout
        Button btnYes = popupView.findViewById(R.id.btnYes);
        Button btnNo = popupView.findViewById(R.id.btnNo);

        // Set click listeners for the buttons
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Yes" button click
                shareContent(strFileContent);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "No" button click
                Toast.makeText(MainActivity5.this, "Bill recorded.",
                        Toast.LENGTH_SHORT).show();
                openMainActivity();
            }
        });

        // Show the popup window centered on the screen
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }

    // Define a request code
    private static final int SHARE_REQUEST_CODE = 123;

    // Method to share content
    public void shareContent(StringBuilder strFileContent) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = strFileContent.toString();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Start the sharing activity for result
        startActivityForResult(Intent.createChooser(shareIntent, "Share via"), SHARE_REQUEST_CODE);

    }

    // Return to main page after user interact with the popup window
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SHARE_REQUEST_CODE) {
            Toast.makeText(MainActivity5.this, "Bill recorded.",
                    Toast.LENGTH_SHORT).show();
            openMainActivity();
        }
    }
}