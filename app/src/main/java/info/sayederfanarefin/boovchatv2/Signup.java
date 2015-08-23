package info.sayederfanarefin.boovchatv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

public class Signup extends AppCompatActivity {
    Button signupButton;

    EditText name_edittext, email_edittext, phone_edittext, pass_edittext, confirm_pass_edittext;
    String name, email, phone, pass, sex, confirm_pass;
    RadioGroup radioSexGroup;
    RadioButton radioSexButton;
    private MobileServiceClient mClient;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progress= new ProgressDialog(this);
        signupButton = (Button)findViewById(R.id.buttonCreateAccount);
        name_edittext = (EditText) findViewById(R.id.editTextNameSignup);
        email_edittext = (EditText) findViewById(R.id.editTextEmailSignup);
        phone_edittext = (EditText) findViewById(R.id.editTextPhonenumberSignup);
        pass_edittext = (EditText) findViewById(R.id.editTextPasswordSignup);
        confirm_pass_edittext = (EditText) findViewById(R.id.editTextConfirmPasswordSignup);
        radioSexGroup = (RadioGroup) findViewById(R.id.sex_signup);

        // Initialize the progress bar
        try{
            mClient = new MobileServiceClient(
                     /*credentials from azure mobile services*/,
                    this
            );
        }catch(java.net.MalformedURLException e){
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Something went wrong! Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();

        }

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_edittext.getText().toString();
                email = email_edittext.getText().toString();
                phone = phone_edittext.getText().toString();
                pass = pass_edittext.getText().toString();
                confirm_pass = confirm_pass_edittext.getText().toString();
                int temp = radioSexGroup.getCheckedRadioButtonId();

                if(name.length() >0 && email.length() >0 && phone.length() >0 && pass.length() >0 && temp>0 && confirm_pass.length()>0){
                    if(confirm_pass.equals(pass)){
                        progress.setMessage("Creating account . . . ");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(true);
                        progress.show();

                        radioSexButton = (RadioButton) findViewById(radioSexGroup.getCheckedRadioButtonId());
                        sex = radioSexButton.getText().toString();
                        users u = new users();
                        u.email = email;
                        u.name = name;
                        u.sex = sex;
                        u.phone_number = phone;
                        u.password = pass;
                        mClient.getTable(users.class).insert(u, new TableOperationCallback<users>() {
                            public void onCompleted(users entity, Exception exception, ServiceFilterResponse response) {
                                if (exception == null) {
                                    // Insert succeeded
                                    progress.dismiss();
                                    Intent myIntent = new Intent(Signup.this, MainActivity.class);
                                    startActivity(myIntent);
                                } else {
                                    // Insert failed
                                    progress.dismiss();
                                    Context context = getApplicationContext();
                                    Toast toast = Toast.makeText(context, "Something went wrong! Sign up was unsucessful. Please try again.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        });
                        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("isChecked", true);
                        editor.putString("user_email", email);
                        editor.commit();
                    }else{

                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context, "Please confirm password properly!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }else{
                    //toast for leaving blank fields
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "some fields may be blank! Please complete all the fields", Toast.LENGTH_SHORT);
                    toast.show();
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
