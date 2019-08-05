package com.emotibot.robotvision.game.trexrun.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.emotibot.robotvision.game.trexrun.Constants.GameConstant;
import com.emotibot.robotvision.game.trexrun.R;
import com.emotibot.robotvision.game.trexrun.model.remotePlayerDataSource.RemotePlayerDataSource;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.edtServerIp)
    EditText edtServerIp;
    @BindView(R.id.edtGameTime)
    EditText edtGameTime;
    @BindView(R.id.btnExit)
    Button btnExit;

    SharedPreferences setting;

    public static final String GAME_IP = "GAME_IP";
    public static final String GAME_TIME = "GAME_TIME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        String ip = setting.getString(GAME_IP, RemotePlayerDataSource.DATA_BASE_URL);
        long gameTime = setting.getLong(GAME_TIME, GameConstant.GAME_TIME_LIMIT);
        edtServerIp.setText(ip);
        edtGameTime.setText(Long.toString(gameTime));
    }

    @OnClick(R.id.btnExit)
    public void onExitClick(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (!TextUtils.isEmpty(edtGameTime.getText())) {
            setting.edit().putLong(GAME_TIME, Long.valueOf(edtGameTime.getText().toString()))
                    .putString(GAME_IP, edtServerIp.getText().toString()).apply();
            GameConstant.GAME_TIME_LIMIT = Long.valueOf(edtGameTime.getText().toString());
            RemotePlayerDataSource.clearInstance();
        }
        super.onDestroy();
    }
}
