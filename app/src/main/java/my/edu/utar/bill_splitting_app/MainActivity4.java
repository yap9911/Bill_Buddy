package my.edu.utar.bill_splitting_app;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
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

public class MainActivity4 extends AppCompatActivity {

    private final List<EditText> percentageOrRatioEditTextList = new ArrayList<>(); // List to store the percentage or ratio
    private final List<TextView> resultViewList = new ArrayList<>();                // List to store the result TextViews
    private int buddiesCount;
    private double billAmount;
    private boolean isCalculated = false;
    private StringBuilder strFileContent = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

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
                TextView buddyView = new TextView(this);
                buddyView.setText("Buddies " + (i + 1));
                EditText PercentOrRatio = new EditText(this);
                PercentOrRatio.setInputType(InputType.TYPE_CLASS_NUMBER);
                PercentOrRatio.setFilters(new InputFilter[]{new MainActivity.DecimalDigitsInputFilter(0)}); // allow user to enter integer only.
                percentageOrRatioEditTextList.add(PercentOrRatio);
                TextView resultView = new TextView(this);
                resultViewList.add(resultView);
                resultView.setPadding(96, 0, 0, 0);
                BuddyRow.addView(buddyView);
                BuddyRow.addView(PercentOrRatio);
                BuddyRow.addView(resultView);
                tableLayout.addView(BuddyRow);
            }

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
                    isCalculated = calculateBillForEachBuddy(billAmount, resultViewList);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.done_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (isCalculated) {
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
                strFileContent.append("-------------------------------------------------").append("\n");
                strFileContent.append(formattedDateTime).append("\n");
                strFileContent.append("Bill Amount: ").append(billAmount).append("\n");

                for (int i = 0; i < buddiesCount; i++) {
                    String buddyPayment = resultViewList.get(i).getText().toString();
                    strFileContent.append("Buddy ").append(i + 1).append(": ").append(buddyPayment).append("\n");
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
            Toast.makeText(MainActivity4.this, "Please make sure each row is filled up " +
                            "and the result is calculated.",
                    Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // A method to calculate the total bill amount to be paid by each buddy
    public boolean calculateBillForEachBuddy(double billAmount, List<TextView> resultViewList) {

        int totalPercentOrRatio = 0;
        List<Integer> individualPercentOrRatio = new ArrayList<>();

        // Get the individual total percentage or ratio input by user and sum them up
        for (EditText editText : percentageOrRatioEditTextList) {
            String percentOrRatio = editText.getText().toString();

            try {
                int convertedPercentOrRatio = Integer.parseInt(percentOrRatio);
                individualPercentOrRatio.add(convertedPercentOrRatio);
                totalPercentOrRatio += convertedPercentOrRatio;
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity4.this, "Please make sure each row is filled up.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Calculate results and set them on the result views
        for (int i = 0; i < individualPercentOrRatio.size(); i++) {

            double result = (billAmount * individualPercentOrRatio.get(i)) / totalPercentOrRatio;
            resultViewList.get(i).setText(String.format("%.2f", result));
        }
        return true;
    }

    // method to open MainActivity
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

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
                Toast.makeText(MainActivity4.this, "Bill recorded.",
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SHARE_REQUEST_CODE) {
            Toast.makeText(MainActivity4.this, "Bill recorded.",
                    Toast.LENGTH_SHORT).show();
            openMainActivity();
        }
    }
}


