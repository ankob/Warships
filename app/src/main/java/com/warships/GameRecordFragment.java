package com.warships;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameRecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameRecordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_TIME = "time";
    private static final String ARG_PARAM_WIN = "win";
    private static final String ARG_PARAM_DAMAGE = "damage";
    private static final String ARG_PARAM_MOVES = "moves";
    private static final String ARG_PARAM_MOVES_LEFT = "moves_left";
    private static final String ARG_PARAM_SHIPS_LEFT = "ships_left";

    // TODO: Rename and change types of parameters
    private String mParamTime;
    private boolean mParamWin;
    private int mParamMoves;
    private int mParamMovesLeft;
    private int mParamDamage;
    private int mParamShipseft;

    public GameRecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameRecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameRecordFragment newInstance(
            String paramTime,
            boolean paramWin,
            int paramMoves,
            int paramMovesLeft,
            int paramDamage,
            int paramShipsLeft
    ) {
        GameRecordFragment fragment = new GameRecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_TIME, paramTime);
        args.putBoolean(ARG_PARAM_WIN, paramWin);
        args.putInt(ARG_PARAM_DAMAGE, paramDamage);
        args.putInt(ARG_PARAM_MOVES, paramMoves);
        args.putInt(ARG_PARAM_MOVES_LEFT, paramMovesLeft);
        args.putInt(ARG_PARAM_SHIPS_LEFT, paramShipsLeft);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamTime = getArguments().getString(ARG_PARAM_TIME);
            mParamWin = getArguments().getBoolean(ARG_PARAM_WIN);
            mParamDamage = getArguments().getInt(ARG_PARAM_DAMAGE);
            mParamShipseft = getArguments().getInt(ARG_PARAM_SHIPS_LEFT);
            mParamMoves = getArguments().getInt(ARG_PARAM_MOVES);
            mParamMovesLeft = getArguments().getInt(ARG_PARAM_MOVES_LEFT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game_record, container, false);
        ((TextView) v.findViewById(R.id.game_time_label)).setText(mParamTime);
        ((TextView) v.findViewById(R.id.game_win_lose_label)).setText(mParamWin ? "Win" : "Lose");
        ((TextView) v.findViewById(R.id.game_damage_label)).setText(Integer.toString(mParamDamage));
        ((TextView) v.findViewById(R.id.game_ships_left_label)).setText(Integer.toString(mParamShipseft));
        ((TextView) v.findViewById(R.id.game_moves_label)).setText(Integer.toString(mParamMoves));
        ((TextView) v.findViewById(R.id.game_moves_left_label)).setText(Integer.toString(mParamMovesLeft));
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
