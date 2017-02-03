package com.warships;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;

public class GameActivity extends AppCompatActivity {

    static int height = 8;
    static int width = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GridLayout gl = (GridLayout) findViewById(R.id.battlefieldLayout);
        gl.setRowCount(height);
        gl.setColumnCount(width);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < width; y++) {
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams(gl.getLayoutParams());
                lp.rowSpec = GridLayout.spec(x, 1);
                lp.columnSpec = GridLayout.spec(y, 1);
                gl.addView(new Button(this), lp);
            }
    }
}
