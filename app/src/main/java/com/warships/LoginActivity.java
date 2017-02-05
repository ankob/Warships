package com.warships;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.warships.db.DbHelper;
import com.warships.db.UserContract;
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
                    if (((CheckBox) findViewById(R.id.newUserCheckBox)).isChecked()) {
                        DbHelper dbHelper = new DbHelper(getBaseContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        String name = ((EditText) findViewById(R.id.editName)).getText().toString();
                        String pass = ((EditText) findViewById(R.id.editPass)).getText().toString();

                        if (!pass.equals(((EditText) findViewById(R.id.repeatPass)).getText().toString())) {
                            return;
                        }

                        ContentValues cv = new ContentValues();

                        cv.put(UserContract.User.NAME, name);
                        cv.put(UserContract.User.HASH, User.calcHash(name, pass));

                        db.insert(UserContract.User.TABLE_NAME, null, cv);
                    } else {

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
