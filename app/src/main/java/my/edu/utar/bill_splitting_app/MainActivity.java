package my.edu.utar.bill_splitting_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.text.InputFilter;
import android.text.Spanned;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText et1 = findViewById(R.id.et_buddiesCount);
        EditText et2 = findViewById(R.id.et_billAmount);
        Button calculateButton = findViewById(R.id.calculateBt);
        et2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)}); // prevent the user from inputting bill amount with more than 2 d.p.
                                                                            // by using method setFilters()
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String buddies = et1.getText().toString();          // obtain the number of buddies and convert to string
                String bill = et2.getText().toString();            // obtain the bill amount and convert to string
                int buddiesCount;
                double billAmount;

                boolean validBuddiesCount = false, validBillAmount = false;

                if (buddies.isEmpty() || bill.isEmpty()) {           // validate that both the no of buddies and bill amount is filled in
                    Toast.makeText(MainActivity.this, "Please make sure both bill amount and no of buddies are filled in.",
                            Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick method to prevent further processing
                }

                try {
                    buddiesCount = Integer.parseInt(buddies);      // convert buddiesCount into integer
                    billAmount = Double.parseDouble(bill);         // convert billAmount into integer
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid input. Please enter valid numbers for buddies and bill amount.",
                            Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick method if parsing fails
                }

                if (buddiesCount <= 1) {
                    Toast.makeText(MainActivity.this, "Stop eating alone, go make some friends XD",               // Display an error message if buddies count is less than 1
                            Toast.LENGTH_SHORT).show();
                } else if (billAmount <= 0) {
                    Toast.makeText(MainActivity.this, "Since the bill amount is 0, there is no need to split it -_-", // Display an error message if bill amount is less than or equal 0
                            Toast.LENGTH_SHORT).show();
                } else {
                    validBuddiesCount = true;
                    validBillAmount = true;
                }

                if (validBuddiesCount && validBillAmount) {
                    // Creating a popup for user to choose the break-down option
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId()) {
                                case R.id.equal_bd:
                                    Toast.makeText(MainActivity.this, "Equal break-down chosen",
                                            Toast.LENGTH_SHORT).show();
                                    openMainActivity2(buddiesCount, billAmount);    // open MainActivity2 if user choose equal break-down
                                    return true;
                                case R.id.amount:
                                    Toast.makeText(MainActivity.this, "Custom break-down by amount chosen",
                                            Toast.LENGTH_SHORT).show();
                                    openMainActivity3(buddiesCount, billAmount);    // open MainActivity3 if user choose Custom break-down by amount
                                    return true;
                                case R.id.percentage:
                                    Toast.makeText(MainActivity.this, "Custom break-down by percentage or ratio chosen",
                                            Toast.LENGTH_SHORT).show();
                                    openMainActivity4(buddiesCount, billAmount);    // open MainActivity4 if user choose Custom break-down by percentage or ratio

                                    return true;
                                case R.id.combination_bd:
                                    Toast.makeText(MainActivity.this, "Combination break-down chosen",
                                            Toast.LENGTH_SHORT).show();
                                    openMainActivity5(buddiesCount, billAmount);    // open MainActivity5 if user choose combination break-down
                                    return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            }
        });
    }

    public void openMainActivity2(int buddiesCount, double billAmount) {       // method to open MainActivity2
        Intent intent = new Intent(this, MainActivity2.class);
        // Put the buddiesCount and billAmount as extras in the Intent
        intent.putExtra("buddiesCountExtra", buddiesCount);
        intent.putExtra("billAmountExtra", billAmount);
        startActivity(intent);
    }

    public void openMainActivity3(int buddiesCount, double billAmount) {       // method to open MainActivity3
        Intent intent = new Intent(this, MainActivity3.class);
        // Put the buddiesCount and billAmount as extras in the Intent
        intent.putExtra("buddiesCountExtra", buddiesCount);
        intent.putExtra("billAmountExtra", billAmount);
        startActivity(intent);
    }

    public void openMainActivity4(int buddiesCount, double billAmount) {       // method to open MainActivity4
        Intent intent = new Intent(this, MainActivity4.class);
        // Put the buddiesCount and billAmount as extras in the Intent
        intent.putExtra("buddiesCountExtra", buddiesCount);
        intent.putExtra("billAmountExtra", billAmount);
        startActivity(intent);
    }

    public void openMainActivity5(int buddiesCount, double billAmount) {       // method to open MainActivity4
        Intent intent = new Intent(this, MainActivity5.class);
        // Put the buddiesCount and billAmount as extras in the Intent
        intent.putExtra("buddiesCountExtra", buddiesCount);
        intent.putExtra("billAmountExtra", billAmount);
        startActivity(intent);
    }

    /**
     * Input filter that limits the number of decimal digits that are allowed to be
     * entered.
     */
    public static class DecimalDigitsInputFilter implements InputFilter {

        private final int decimalDigits;

        /**
         //Constructor.
         *
         * @param decimalDigits maximum decimal digits
         */
        public DecimalDigitsInputFilter(int decimalDigits) {
            this.decimalDigits = decimalDigits;
        }

        @Override
        public CharSequence filter(CharSequence source, // new text user tries to input
                                   int start,           // starting index of "source" text
                                   int end,             // ending index of "source" text
                                   Spanned dest,        // current text before user's input
                                   int dstart,          // starting index of "dest" text
                                   int dend) {          // ending index of "dest" text


            int dotPos = -1; // the position of the dot, "."
            int len = dest.length();
            for (int i = 0; i < len; i++) {
                char c = dest.charAt(i);
                if (c == '.' || c == ',') {
                    dotPos = i; // get the position of the dot and break
                    break;
                }
            }
            if (dotPos >= 0) { // if user enter a dot

                // protects against many dots
                if (source.equals(".") || source.equals(","))
                {
                    return "";
                }
                // if the text is entered before the dot
                if (dend <= dotPos) {
                    return null;
                }
                if (len - dotPos > decimalDigits) {
                    return "";
                }
            }

            return null;
        }

    }

}