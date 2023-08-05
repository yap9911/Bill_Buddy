package my.edu.utar.bill_splitting_app;

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

public class MainActivity3 extends AppCompatActivity {

    private final List<EditText> amountPaidEditTextList = new ArrayList<>(); // List to store the amountPaid by each buddy
    private final List<EditText> buddiesNameList = new ArrayList<>();        // List to store the name of each buddy
    private double billAmount;
    private int buddiesCount;
    private TextView totalView;
    private StringBuilder strFileContent = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

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
                EditText amountPaid = new EditText(this);
                amountPaid.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                amountPaid.setFilters(new InputFilter[]{new MainActivity.DecimalDigitsInputFilter(2)});  // prevent the user from inputting bill amount with more than 2 d.p.
                                                                                                                    // by using method setFilters()
                amountPaid.setPadding(32, 16, 16, 16);
                amountPaidEditTextList.add(amountPaid); // Add the amountPaid to the list
                BuddyRow.addView(buddyNameEt);

                BuddyRow.addView(amountPaid);
                tableLayout.addView(BuddyRow);
            }

            TableRow sumRow = new TableRow(this);

            TextView sumView = new TextView(this);
            totalView = sumView;

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

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.done_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        double sumInTable = calculateTotalSum();    // calculate the total amount input by user
        totalView.setText("Total: " + sumInTable);  // display the total amount

        if (sumInTable > 0) {                    // Only proceed if all rows are filled up
            if (sumInTable == billAmount) {     // to check if the sum in table matches the total amount entered
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
                        double eachBuddyAmount = Double.parseDouble(amountPaidEditTextList.get(i).getText().toString());
                        String buddyPayment = String.format("%.2f", eachBuddyAmount);
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
                Toast.makeText(MainActivity3.this, "Total sum in table does not match the bill amount.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // A method to calculate the total sum of amountPaid values and retrieve the bill amounts of each row
    public double calculateTotalSum() {
        double totalSum = 0;
        for (int i = 0; i < buddiesCount; i++) {
            String amount = amountPaidEditTextList.get(i).getText().toString();
            String Name = buddiesNameList.get(i).getText().toString().trim();

            // check if each buddy's name is entered
            if (TextUtils.isEmpty(Name)) {
                Toast.makeText(MainActivity3.this, "Please make sure each buddy's name is filled up.",
                        Toast.LENGTH_SHORT).show();
                return 0;
            }

            // check if all amount is entered
            try {
                double billAmount = Double.parseDouble(amount);
                totalSum += billAmount;
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity3.this, "Please make sure all amount are filled up.",
                        Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        return totalSum;
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
                Toast.makeText(MainActivity3.this, "Bill recorded.",
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
            Toast.makeText(MainActivity3.this, "Bill recorded.",
                    Toast.LENGTH_SHORT).show();
            openMainActivity();
        }
    }
}