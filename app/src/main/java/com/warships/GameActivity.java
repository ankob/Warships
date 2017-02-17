package com.warships;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.warships.db.DbHelper;
import com.warships.model.Battle;
import com.warships.model.BattleRecord;
import com.warships.model.User;

import java.util.Collections;
import java.util.Date;

public class GameActivity extends AppCompatActivity {

    static final int height = 8;
    static final int width = 8;
    private Battle ongoingBattle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GridLayout gl = (GridLayout) findViewById(R.id.battlefield_layout);
        ongoingBattle = User.getCurrentUser().getCurrentSettings().initBattle();
        gl.setRowCount(height);
        gl.setColumnCount(width);
        ImageButton buf;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < width; y++) {
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams(gl.getLayoutParams());
                lp.rowSpec = GridLayout.spec(x, 1);
                lp.columnSpec = GridLayout.spec(y, 1);
                buf = new ImageButton(this);
                buf.setMaxWidth(32);
                buf.setMinimumWidth(32);
                buf.setMaxHeight(32);
                buf.setMinimumHeight(32);
                buf.setOnClickListener(new MyOnClikcListener(x, y));
                buf.setImageResource(android.R.drawable.presence_invisible);
                gl.addView(buf, lp);
            }
    }

    private class MyOnClikcListener implements View.OnClickListener {

        public MyOnClikcListener(int xPosition, int yPosition) {
            this.xPosition = xPosition;
            this.yPosition = yPosition;
        }

        private int xPosition;
        private int yPosition;

        @Override
        public void onClick(View view) {
            ImageButton cellButton = (ImageButton) view;

            ongoingBattle.shoot(xPosition, yPosition);

            if (ongoingBattle.getBattlefield()[xPosition][yPosition] == Battle.CellState.Miss)
                cellButton.setImageResource(android.R.drawable.presence_offline);
            if (ongoingBattle.getBattlefield()[xPosition][yPosition] == Battle.CellState.Hit)
                cellButton.setImageResource(android.R.drawable.presence_online);

            if (ongoingBattle.getGameState() != Battle.GameState.Ongoing) {
                BattleRecord record = new BattleRecord(
                        -1,
                        BattleRecord.format.format(new Date()),
                        ongoingBattle.getGameState() == Battle.GameState.Win,
                        User.getCurrentUser().getId(),
                        ongoingBattle.getShootsNumber(),
                        ongoingBattle.getConfig().getMaxShootsNumber() - ongoingBattle.getShootsNumber(),
                        ongoingBattle.getDamage(),
                        ongoingBattle.getTotalShipsCellsNumber() - ongoingBattle.getDamage()
                );
                DbHelper dbHelper = new DbHelper(view.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                record.writeRecordToDB(db);
                db = dbHelper.getReadableDatabase();
                User.reloadCurrentUserData(db);
                finish();
            }
        }
    };
}
