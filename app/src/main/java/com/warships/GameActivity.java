package com.warships;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.warships.db.DbHelper;
import com.warships.model.Battle;
import com.warships.model.BattleRecord;
import com.warships.model.User;

import java.util.Date;

public class GameActivity extends AppCompatActivity {

    static final int height = 8;
    static final int width = 8;
    static final int MAGIC_BUTTON_ID_PREFIX = 12345;
    private int shootsLeft;
    private Battle ongoingBattle;

    private int calcMagicId(int x, int y) {
        return MAGIC_BUTTON_ID_PREFIX + x + y*100;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GridLayout gl = (GridLayout) findViewById(R.id.battlefield_layout);
        ongoingBattle = User.getCurrentUser().getCurrentSettings().initBattle();
        gl.setRowCount(height);
        gl.setColumnCount(width);
        shootsLeft = ongoingBattle.getConfig().getMaxShootsNumber();
        updateShootsCounter();
        ImageButton buf;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < width; y++) {
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams(gl.getLayoutParams());
                lp.rowSpec = GridLayout.spec(x, 1);
                lp.columnSpec = GridLayout.spec(y, 1);
                buf = new ImageButton(this);
                buf.setId(calcMagicId(x, y));
                buf.setMaxWidth(32);
                buf.setMinimumWidth(32);
                buf.setMaxHeight(32);
                buf.setMinimumHeight(32);
                buf.setOnClickListener(new MyOnClikcListener(x, y));
                buf.setImageResource(android.R.drawable.presence_invisible);
                gl.addView(buf, lp);
            }
        findViewById(R.id.surrender_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkSurrenderDecision();
                    }
                }
        );
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

            if (ongoingBattle.getGameState() != Battle.GameState.Ongoing)
                finishBattle(ongoingBattle.getGameState() == Battle.GameState.Win);
            shootsLeft--;
            updateShootsCounter();
        }
    };

    private void finishBattle(boolean win) {
        BattleRecord record = new BattleRecord(
                -1,
                BattleRecord.format.format(new Date()),
                win,
                User.getCurrentUser().getId(),
                ongoingBattle.getShootsNumber(),
                ongoingBattle.getConfig().getMaxShootsNumber() - ongoingBattle.getShootsNumber(),
                ongoingBattle.getDamage(),
                ongoingBattle.getTotalShipsCellsNumber() - ongoingBattle.getDamage()
        );
        DbHelper dbHelper = new DbHelper(getBaseContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        record.writeRecordToDB(db);
        db = dbHelper.getReadableDatabase();

        ongoingBattle.showBattlefield();
        Battle.CellState[][] battlefield = ongoingBattle.getBattlefield();
        ImageButton buf;
        for (int x = 0; x < ongoingBattle.getConfig().getFieldSize(); x++) {
            for (int y = 0; y < ongoingBattle.getConfig().getFieldSize(); y++) {
                buf = (ImageButton) findViewById(calcMagicId(x, y));
                if (battlefield[x][y] == Battle.CellState.Miss)
                    buf.setImageResource(android.R.drawable.presence_offline);
                if (battlefield[x][y] == Battle.CellState.Hit)
                    buf.setImageResource(android.R.drawable.presence_online);
            }
        }

        User.reloadCurrentUserData(db);

        Button leaveButton = (Button) findViewById(R.id.surrender_button);
        leaveButton.setText("Proceed");
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Snackbar.make(
                findViewById(R.id.activity_game),
                win ? "Win!" : "Lose!",
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void checkSurrenderDecision() {
        new AlertDialog.Builder(findViewById(R.id.surrender_button).getContext())
                .setTitle("Surrender")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishBattle(false);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        checkSurrenderDecision();
    }

    private void updateShootsCounter() {
        ((TextView) findViewById(R.id.shoots_counter)).setText(Integer.toString(shootsLeft));
    }
}
