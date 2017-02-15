package com.warships;

import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.warships.db.DbHelper;
import com.warships.model.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.loginButton).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = ((EditText) findViewById(R.id.editName)).getText().toString();
                    String pass = ((EditText) findViewById(R.id.editPass)).getText().toString();
                    if (((CheckBox) findViewById(R.id.newUserCheckBox)).isChecked()) {
                        DbHelper dbHelper = new DbHelper(view.getContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        EditText repeatPass = (EditText) findViewById(R.id.repeatPass);
                        if (!pass.equals(repeatPass.getText().toString())) {
                            repeatPass.setError(getString(R.string.error_passwords_are_not_equal));
                            return;
                        }
                        User.writeNewUserToDB(db, name, pass);
                        db = dbHelper.getReadableDatabase();
                        User.getUserFromDB(db, name, pass);
                        if (User.getCurrentUser() == null) {
                            Snackbar sb = Snackbar.make(view.getRootView(), R.string.error_while_creating_user, Snackbar.LENGTH_SHORT);
                            sb.show();
                        } else {
                            finish();
                        }
                    } else {
                        DbHelper dbHelper = new DbHelper(view.getContext());
                        SQLiteDatabase db = dbHelper.getReadableDatabase();

                        User.getUserFromDB(db, name, pass);
                        if (User.getCurrentUser() == null) {
                            Snackbar sb = Snackbar.make(view.getRootView(), R.string.error_while_login, Snackbar.LENGTH_SHORT);
                            sb.show();
                        } else {
                            finish();
                        }
                    }
                }
            }
        );
    }

    public void onCheckboxClick(View v) {
        boolean isChecked = ((CheckBox) v).isChecked();

        findViewById(R.id.repeatPass).setEnabled(isChecked);
    }
}
