package layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.warships.GameActivity;
import com.warships.R;
import com.warships.model.BattleConfig;
import com.warships.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreenFragment extends Fragment {

    public MainScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainScreenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainScreenFragment newInstance() {
        MainScreenFragment fragment = new MainScreenFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        if (User.getCurrentUser() != null) {
            TextView userGreetings = (TextView) getView().findViewById(R.id.player_greetings_label);
            TextView currentSettings = (TextView) getView().findViewById(R.id.current_settings_label);
            TextView currentWinrate = (TextView) getView().findViewById(R.id.current_winrate_label);
            userGreetings.setText(String.format((String) getText(R.string.player_greetings), User.getCurrentUser().getName()));
            BattleConfig currentUserSettings = User.getCurrentUser().getCurrentSettings();
            TextView playLabel = (TextView) getView().getRootView().findViewById(R.id.text_play_button_label);
            if (currentUserSettings != null) {
                currentSettings.setText(currentUserSettings.getName());
                playLabel.setText(R.string.play_button_label);
                getView().getRootView().findViewById(R.id.play_button).setEnabled(true);
            } else {
                playLabel.setText(R.string.no_current_settings_message);
                getView().getRootView().findViewById(R.id.play_button).setEnabled(false);
            }
            currentWinrate.setText(String.format("%02.2f%%", User.getCurrentUser().getWinRate() * 100));
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_screen, container, false);
        ImageButton playButton = (ImageButton) v.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gameActivity = new Intent(view.getRootView().getContext(), GameActivity.class);
                startActivity(gameActivity);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
