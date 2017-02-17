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
        ((Spinner) v.findViewById(R.id.setting_spinner)).setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String name = (String) adapterView.getItemAtPosition(i);
                        for (BattleConfig bc: User.getCurrentUser().getSettings())
                            if (bc.getName().equals(name)) currentEditingConfig = bc;
                        List<Integer> sizes = currentEditingConfig.getShipsPlacement();
                        ((SeekBar) adapterView.getRootView().findViewById(R.id.ships_number_bar)).setProgress(sizes.size());
                        ((EditText) adapterView.getRootView().findViewById(R.id.config_name_input)).setText(currentEditingConfig.getName());
                        ((EditText) adapterView.getRootView().findViewById(R.id.shoots_number_input)).setText(Integer.toString(currentEditingConfig.getMaxShootsNumber()));
                        for (int cnt = 0; cnt < shipsLengthes.length; cnt ++) {
                            if (cnt < sizes.size()) {
                                shipsLengthes[cnt].setEnabled(true);
                                shipsLengthes[cnt].setProgress(sizes.get(cnt));
                            } else
                                shipsLengthes[cnt].setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
        initButtonsBehavior(v);
        return v;
    }

    private void updateSpinner(View v) {
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
    }

    @Override
    public void onResume() {

        View v = getView();
        updateSpinner(v);
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

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (currentEditingConfig != null) {
                if (view.getId() == R.id.save_settings_button) {
                    boolean nameFound = false;
                    for (BattleConfig bc : User.getCurrentUser().getSettings())
                        nameFound = nameFound || (currentEditingConfig.getName().equals(bc.getName()) && bc != currentEditingConfig);
                    if (nameFound) {
                        Snackbar.make(view, R.string.settings_name_duplicate_error, Snackbar.LENGTH_SHORT);
                        return;
                    }
                }
                DbHelper dbHelper = new DbHelper(view.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                switch (view.getId()) {
                    case R.id.delete_settings_button:
                        currentEditingConfig.deleteConfig(db);
                        Snackbar.make(view, "Deleted successfully", Snackbar.LENGTH_SHORT);
                        break;
                    case R.id.save_settings_button:
                        currentEditingConfig.setMaxShootsNumber(
                                Integer.parseInt(
                                        ((EditText) view.getRootView().findViewById(R.id.shoots_number_input)).getText().toString()
                                )
                        );
                        currentEditingConfig.setName(
                                ((EditText) view.getRootView().findViewById(R.id.config_name_input)).getText().toString()
                        );
                        LinkedList<Integer> placement = new LinkedList<>();
                        for (SeekBar sb : SettingsFragment.this.shipsLengthes) {
                            if (sb.isEnabled())
                                placement.add(sb.getProgress());
                        }
                        currentEditingConfig.setShipsPlacement(
                                placement
                        );
                        currentEditingConfig.writeConfigToDB(db);
                        Snackbar.make(view, "Updated successfully", Snackbar.LENGTH_SHORT);
                        break;
                    case R.id.set_current_settings_button:
                        for (BattleConfig bc : User.getCurrentUser().getSettings()) {
                            bc.setCurrent(bc == currentEditingConfig);
                            bc.writeConfigToDB(db);
                        }
                        break;
                    default:
                        break;
                }
                db = dbHelper.getReadableDatabase();
                User.reloadCurrentUserData(db);
                updateSpinner(getView());
            }
        }
    };

    private void initButtonsBehavior(View v) {
        v.findViewById(R.id.delete_settings_button).setOnClickListener(onClickListener);
        v.findViewById(R.id.save_settings_button).setOnClickListener(onClickListener);
        v.findViewById(R.id.set_current_settings_button).setOnClickListener(onClickListener);
        v.findViewById(R.id.create_settings_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentEditingConfig = new BattleConfig(
                                BattleConfig.NEW_CONFIG_ID,
                                new ArrayList<Integer>(),
                                100,
                                false,
                                "New Settings"
                        );
                        ((SeekBar) view.getRootView().findViewById(R.id.ships_number_bar)).setProgress(0);
                        ((EditText) view.getRootView().findViewById(R.id.config_name_input)).setText("NewSettings");
                        ((EditText) view.getRootView().findViewById(R.id.shoots_number_input)).setText("100");
                    }
                }
        );
    }
}
