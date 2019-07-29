package com.emotibot.robotvision.game.trexrun.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.emotibot.robotvision.game.trexrun.R;
import com.emotibot.robotvision.game.trexrun.model.MainPlayerDataSource;
import com.emotibot.robotvision.game.trexrun.model.Player;
import com.emotibot.robotvision.game.trexrun.model.PlayerDataSource;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.rcyRank)
    RecyclerView rcyRank;
    @BindView(R.id.btnStart)
    Button btnStart;
    private final static String TAG = StartActivity.class.getSimpleName();
    public static final String BUNDLE_PLAY_LEFT = "BUNDLE_PLAY_LEFT";
    public static final String BUNDLE_PLAY_RIGHT = "BUNDLE_PLAY_RIGHT";
    public static final String BUNDLE_MATCH_ID = "BUNDLE_MATCH_ID";
    private PlayerDataSource mainPlayerDataSource;

    private RankAdapter rankAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        rankAdapter = new RankAdapter(new ArrayList<Player>());
        mainPlayerDataSource = MainPlayerDataSource.getInstance(this);
        initRankList();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Give first an explanation, if needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1);
            }
        }

    }


    @OnClick({R.id.btnStart})
    public void gotoGameActivity(View view) {
        Intent intent = new Intent(this, PlayerCheckActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void initRankList() {
        rcyRank.setVisibility(View.VISIBLE);
        layoutManager = new LinearLayoutManager(this);
        rcyRank.setLayoutManager(layoutManager);
        rcyRank.setAdapter(rankAdapter);

        mainPlayerDataSource.getPlayerList(6, new PlayerDataSource.PlayerListCallback() {
            @Override
            public void onSuccess(final List<Player> playerList) {
                if (playerList != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "playerList:" + playerList.size());
                            rankAdapter.setPlayerList(playerList);
                            rankAdapter.notifyDataSetChanged();
                            rcyRank.invalidate();
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

}
