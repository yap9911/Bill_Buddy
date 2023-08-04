package my.edu.utar.bill_splitting_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity2 extends AppCompatActivity {

    private int buddiesCount;
    private double billAmount;
    private double eachBuddyAmount;
    private final List<EditText> buddiesNameList = new ArrayList<>();   // List to store the name of each buddy
    private StringBuilder strFileContent = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Check if the Intent is not null and contains extras
        if (intent != null) {
            // Retrieve extras from the Intent
            buddiesCount = intent.getIntExtra("buddiesCountExtra", 0);
            billAmount = intent.getDoubleExtra("billAmountExtra", 0.0);
            eachBuddyAmount = billAmount / buddiesCount;

            TextView buddiesCountDisplay = findViewById(R.id.tv_buddiesCountDisplay);
            TextView billAmountDisplay = findViewById(R.id.tv_billAmountDisplay);
            TextView result = findViewById(R.id.tv_result);
            buddiesCountDisplay.setText(buddiesCount + " buddies are sharing the bill");
            billAmountDisplay.setText("The total bill amount is " + billAmount);
            result.setText("Each buddy has to pay " + String.format("%.2f", eachBuddyAmount));

            TableLayout tableLayout = findViewById(R.id.table);

            // Create and add the rows based on the buddiesCount
            for (int i = 0; i < buddiesCount; i++) {
                TableRow BuddyRow = new TableRow(this);
                BuddyRow.setPadding(16, 16, 16, 16);
                EditText buddyNameEt = new EditText(this);
                buddyNameEt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                buddiesNameList.add(buddyNameEt);
                BuddyRow.addView(buddyNameEt);
                tableLayout.addView(BuddyRow);
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.done_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (noEmptyInput()) {
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
                    String buddyPayment = String.format("%.2f", eachBuddyAmount);
                    String buddyName = buddiesNameList.get(i).getText().toString();
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
        }
        return super.onOptionsItemSelected(item);
    }

    // method to open MainActivity
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean noEmptyInput(){

        for (int i = 0; i < buddiesCount; i++) {
            String Name = buddiesNameList.get(i).getText().toString().trim();

            if (TextUtils.isEmpty(Name)) {
                Toast.makeText(MainActivity2.this, "Please make sure each row is filled up.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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
                Toast.makeText(MainActivity2.this, "Bill recorded.",
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
            Toast.makeText(MainActivity2.this, "Bill recorded.",
                    Toast.LENGTH_SHORT).show();
            openMainActivity();
        }
    }

}
