package layout;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.warships.GameRecordFragment;
import com.warships.R;
import com.warships.model.BattleRecord;
import com.warships.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (BattleRecord br: User.getCurrentUser().getBattleRecords()) {
            fragmentTransaction.add(
                    R.id.stat_layout,
                    GameRecordFragment.newInstance(
                            br.getTime() == null ? "" : BattleRecord.format.format(br.getTime()),
                            br.isWin(),
                            br.getMoves(),
                            br.getMovesLeft(),
                            br.getDamage(),
                            br.getShipsLeft()
                    ),
                    br.getTime() == null ? "" : BattleRecord.format.format(br.getTime())
            );
        }
        fragmentTransaction.commit();
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
