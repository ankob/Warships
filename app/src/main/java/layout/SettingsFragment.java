package layout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.warships.R;
import com.warships.db.DbHelper;
import com.warships.model.BattleConfig;
import com.warships.model.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private static BattleConfig currentEditingConfig = null;
    private SeekBar[] shipsLengthes = new SeekBar[5];

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        shipsLengthes[0] = (SeekBar) v.findViewById(R.id.ship_length_bar_0);
        shipsLengthes[1] = (SeekBar) v.findViewById(R.id.ship_length_bar_1);
        shipsLengthes[2] = (SeekBar) v.findViewById(R.id.ship_length_bar_2);
        shipsLengthes[3] = (SeekBar) v.findViewById(R.id.ship_length_bar_3);
        shipsLengthes[4] = (SeekBar) v.findViewById(R.id.ship_length_bar_4);
        ((SeekBar) v.findViewById(R.id.ships_number_bar)).setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        for (int x = 0; x < shipsLengthes.length; x++)
                            shipsLengthes[x].setEnabled(x <= i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        return;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        return;
                    }
                }
        );
        ((Spinner) v.findViewById(R.id.setting_spinner)).setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String name = (String) adapterView.getItemAtPosition(i);
                        for (BattleConfig bc: User.getCurrentUser().getSettings())
                            if (bc.getName().equals(name)) currentEditingConfig = bc;
                        List<Integer> sizes = currentEditingConfig.getShipsPlacement();
                        ((SeekBar) view.findViewById(R.id.ships_number_bar)).setProgress(sizes.size());
                        ((EditText) view.findViewById(R.id.config_name_input)).setText(currentEditingConfig.getName());
                        ((EditText) view.findViewById(R.id.shoots_number_input)).setText(currentEditingConfig.getMaxShootsNumber());
                        for (int cnt = 0; cnt < shipsLengthes.length; cnt ++) {
                            if (cnt <= sizes.size()) {
                                shipsLengthes[cnt].setEnabled(true);
                                shipsLengthes[cnt].setProgress(sizes.get(cnt));
                            } else
                                shipsLengthes[cnt].setEnabled(false);
                        }
                    }
                }
        );
        initButtonsBehavior(v);
        return v;
    }

    @Override
    public void onResume() {

        View v = getView();
        Spinner settingsSpinner = (Spinner) v.findViewById(R.id.setting_spinner);
        LinkedList<CharSequence> names = new LinkedList<>();
        for (BattleConfig bc: User.getCurrentUser().getSettings()) {
            names.add(bc.getName());
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                v.getContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        settingsSpinner.setAdapter(adapter);
        if (currentEditingConfig != null)
            settingsSpinner.setSelection(names.indexOf(currentEditingConfig.getName()));
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initButtonsBehavior(final View v) {
        v.findViewById(R.id.delete_settings_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentEditingConfig != null) {
                            DbHelper dbHelper = new DbHelper(view.getContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            currentEditingConfig.deleteConfig(db);
                            db = dbHelper.getReadableDatabase();
                            User.reloadCurrentUserData(db);
                        }
                    }
                }
        );
        v.findViewById(R.id.save_settings_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentEditingConfig != null) {
                            boolean nameFound = false;
                            for (BattleConfig bc: User.getCurrentUser().getSettings())
                                nameFound = nameFound || (currentEditingConfig.getName().equals(bc.getName()) && bc != currentEditingConfig);
                            if (nameFound) {
                                Snackbar.make(view.getRootView(), R.string.settings_name_duplicate_error, Snackbar.LENGTH_SHORT);
                            }
                            currentEditingConfig.setMaxShootsNumber(
                                    Integer.parseInt(
                                            ((EditText) v.findViewById(R.id.shoots_number_input)).getText().toString()
                                    )
                            );
                            currentEditingConfig.setName(
                                    ((EditText) v.findViewById(R.id.config_name_input)).getText().toString()
                            );
                            LinkedList<Integer> placement = new LinkedList<Integer>();
                            for (SeekBar sb: shipsLengthes) {
                                if (sb.isEnabled())
                                    placement.add(sb.getProgress());
                            }
                            currentEditingConfig.setShipsPlacement(
                                    placement
                            );
                            DbHelper dbHelper = new DbHelper(view.getContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            currentEditingConfig.writeConfigToDB(db);
                            db = dbHelper.getReadableDatabase();
                            User.reloadCurrentUserData(db);
                        }
                    }
                }
        );
        v.findViewById(R.id.set_current_settings_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DbHelper dbHelper = new DbHelper(view.getContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        for (BattleConfig bc: User.getCurrentUser().getSettings()) {
                            bc.setCurrent(bc == currentEditingConfig);
                            bc.writeConfigToDB(db);
                        }
                        db = dbHelper.getReadableDatabase();
                        User.reloadCurrentUserData(db);
                    }
                }
        );
        v.findViewById(R.id.create_settings_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentEditingConfig = new BattleConfig(
                                0,
                                new ArrayList<Integer>(),
                                100,
                                false,
                                "New Settings"
                        );
                        ((SeekBar) v.findViewById(R.id.ships_number_bar)).setProgress(0);
                        ((EditText) v.findViewById(R.id.config_name_input)).setText("NewSettings");
                        ((EditText) v.findViewById(R.id.shoots_number_input)).setText("100");
                    }
                }
        );
    }
}
