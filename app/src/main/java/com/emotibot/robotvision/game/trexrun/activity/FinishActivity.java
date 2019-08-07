package com.emotibot.robotvision.game.trexrun.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.emotibot.robotvision.game.trexrun.R;
import com.emotibot.robotvision.game.trexrun.Utility;
import com.emotibot.robotvision.game.trexrun.model.MainPlayerDataSource;
import com.emotibot.robotvision.game.trexrun.model.Player;
import com.emotibot.robotvision.game.trexrun.model.PlayerDataSource;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

import static com.emotibot.robotvision.game.trexrun.activity.StartActivity.BUNDLE_MATCH_ID;

public class FinishActivity extends AppCompatActivity {
    private static final String TAG = FinishActivity.class.getSimpleName();

    @BindView(R.id.textViewMatchId)
    TextView textviewMatchId;
    @BindView(R.id.textViewConclusion)
    TextView textViewConclusion;
    @BindView(R.id.buttonHome)
    Button buttonHome;
    @BindView(R.id.textViewScan)
    TextView textViewScan;
    @BindView(R.id.imageViewQRCode)
    GifImageView imageViewQRCode;

    @BindView(R.id.imageViewWinnerCenter)
    CircleImageView imageViewWinnerCenter;

    @BindView(R.id.imageViewWinnerLeft)
    ImageView imageViewWinnerLeft;
    @BindView(R.id.imageViewWinnerRight)
    ImageView imageViewWinnerRight;
    @BindView(R.id.linearLayoutWinner)
    LinearLayout linearLayoutWinner;

    @BindView(R.id.buttonSubmitName)
    Button buttonSubmitName;
    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.textViewConclusionRank)
    TextView textViewConclusionRank;
    @BindView(R.id.rootConstraint)
    ConstraintLayout rootConstraint;


    private MainPlayerDataSource mMainPlayerDataSource;
    private int matchId;
    private Handler handler;
    private InputMethodManager imm;
    private List<Player> rankList;

    private Player playerLeft, playerRight, singleWinner;
    public static final String QR_CODE_URL = "http://poc1.emotibot.com:8115/huawei-dev-conf-2019-game/index.html?match_id=";

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        ButterKnife.bind(this);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mMainPlayerDataSource = MainPlayerDataSource.getInstance(getApplicationContext());
        handler = new Handler();
        editTextName.addTextChangedListener(mTextWatcher);
        editTextName.setOnEditorActionListener(mOnEditorActionListener);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            playerRight = new Player();
            playerRight.setGroup(1);
            playerRight.setScore(121);
            playerLeft = new Player();
            playerLeft.setGroup(0);
            playerLeft.setScore(121);
            matchId = 9527;
        } else {
            playerLeft = bundle.getParcelable(StartActivity.BUNDLE_PLAY_LEFT);
            playerRight = bundle.getParcelable(StartActivity.BUNDLE_PLAY_RIGHT);
            matchId = bundle.getInt(BUNDLE_MATCH_ID);
            Log.d(TAG, "match id:" + matchId);
        }

        rankList = new ArrayList<>();

        textviewMatchId.setText(getString(R.string.match_id, matchId));
        insertPlayer();

        if (playerLeft.getScore() == playerRight.getScore()) {
            int rank = mMainPlayerDataSource.getPlayerRank(playerLeft.getScore()) + 1;
            playerLeft.setRank(rank);
            playerRight.setRank(rank);
            displayDoubleWinner(playerLeft, playerRight);
        } else if (playerLeft.getScore() > playerRight.getScore()) {
            int rank = mMainPlayerDataSource.getPlayerRank(playerLeft.getScore()) + 1;
            playerLeft.setRank(rank);
            displaySingleWinner(playerLeft);
        } else {
            int rank = mMainPlayerDataSource.getPlayerRank(playerRight.getScore()) + 1;
            playerRight.setRank(rank);
            displaySingleWinner(playerRight);
        }

        textViewScan.setVisibility(View.INVISIBLE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File savePath = new File(getCacheDir(), (matchId + "_1.jpg"));
                playerRight.setImageInFinalPath(savePath.getPath());
                playerLeft.setImageInFinalPath(savePath.getPath());
                Utility.takeScreenshot(playerLeft.getImageInFinalPath(), FinishActivity.this);
            }
        }, 100);
        textViewScan.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(playerRight.getImageHead()) || TextUtils.isEmpty(playerLeft.getImageHead())) {
                    return;
                }
                mMainPlayerDataSource.uploadPublicFile(new String[]{playerLeft.getImageInPlayingPath()
                        , playerLeft.getImageInFinalPath()}, Integer.toString(matchId), new PlayerDataSource.uploadPublicFileCallback() {
                    @Override
                    public void onSuccess(String qrCodeB64String) {
                        Log.d(TAG, "imageViewQRCode SUCCESS");
                        imageViewQRCode.setImageBitmap(genCode(QR_CODE_URL + matchId));
                        textViewScan.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "imageViewQRCode ERROR");
                    }
                });
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(playerLeft.getImageHead());
        if (file.exists()) {
            if (file.delete()) {
                Log.d(TAG, "playerLeft header image delete :" + file.getPath());
            } else {
                Log.d(TAG, "playerLeft header image not delete :" + file.getPath());
            }
        }
        file = new File(playerRight.getImageHead());
        if (file.exists()) {
            if (file.delete()) {
                Log.d(TAG, "playerRight header image delete :" + file.getPath());
            } else {
                Log.d(TAG, "playerRight header image not delete :" + file.getPath());
            }
        }
    }

    public Bitmap genCode(String content) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 600, 600);
            return bitmap;
        } catch (Exception e) {

        }
        return null;
    }

    private void printPlayer(Player player) {
        Log.d(TAG, String.format("player id = %d, name = %s, score = %d, rank = %d\n", player.getId(), player.getName(), player.getScore(), player.getRank()));
    }

    private void printPlayerList(List<Player> playerList) {
        Iterator<Player> iterator = playerList.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            printPlayer(player);
        }
    }


    private void displaySingleWinner(Player winer) {
        Log.d(TAG, "displaySingleWinner winer player" + winer.getRank() + ",path:" + winer.getImageHead());
        rootConstraint.setBackgroundResource(R.drawable.bg_gameresult_win);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(winer.getImageHead(), options);
        imageViewWinnerCenter.setImageBitmap(bitmap);
        imageViewWinnerCenter.setVisibility(View.VISIBLE);
        imageViewWinnerLeft.setVisibility(View.GONE);
        imageViewWinnerRight.setVisibility(View.GONE);
        buttonSubmitName.setVisibility(View.VISIBLE);
        editTextName.setVisibility(View.VISIBLE);
        linearLayoutWinner.setBackground(ContextCompat.getDrawable(this, R.drawable.border_winner_less));
        singleWinner = winer;
        printPlayer(winer);
        textViewConclusionRank.setText(winer.getRank() != 0 ? getString(R.string.game_conclusion_single_winner_in_rank, winer.getRank())
                : "");
        textViewConclusion.setText(winer.getScore() + getString(R.string.meter));

    }


    private void displayDoubleWinner(Player playerLeft, Player playerRight) {
        printPlayer(playerLeft);
        rootConstraint.setBackgroundResource(R.drawable.bg_gameresult);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(playerLeft.getImageHead(), options);
        imageViewWinnerLeft.setImageBitmap(bitmap);
        bitmap = BitmapFactory.decodeFile(playerRight.getImageHead(), options);
        imageViewWinnerRight.setImageBitmap(bitmap);
        imageViewWinnerCenter.setVisibility(View.GONE);
        imageViewWinnerLeft.setVisibility(View.VISIBLE);
        imageViewWinnerRight.setVisibility(View.VISIBLE);
        buttonSubmitName.setVisibility(View.INVISIBLE);
        editTextName.setVisibility(View.INVISIBLE);
        linearLayoutWinner.setBackground(ContextCompat.getDrawable(this, R.drawable.border_winner_less));
        textViewConclusionRank.setText(playerLeft.getRank() != 0 ? getString(R.string.game_conclusion_single_winner_in_rank, playerLeft.getRank())
                : "");
        textViewConclusion.setText(playerLeft.getScore() + getString(R.string.meter));
    }

    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendName();
                handled = true;
            }
            return handled;
        }
    };

    private void sendName() {
        singleWinner.setName(editTextName.getText().toString());
        mMainPlayerDataSource.updatePlayer(singleWinner, new PlayerDataSource.PlayerCallback() {
            @Override
            public void onSuccess(Player player) {

            }

            @Override
            public void onFailure() {

            }
        });
        buttonSubmitName.setVisibility(View.INVISIBLE);
        editTextName.setVisibility(View.INVISIBLE);
        mMainPlayerDataSource.getAllPlayerList(new PlayerDataSource.PlayerListCallback() {
            @Override
            public void onSuccess(List<Player> playerList) {
//                printPlayerList(playerList);
                imm.hideSoftInputFromWindow(FinishActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @OnClick(R.id.buttonHome)
    public void onGoToHomeClick(View v) {
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }

    @OnClick(R.id.buttonSubmitName)
    public void onButtonSubmitClick(View view) {
        sendName();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String name = s.toString();
            name = name.replace(" ", "");
            if (name.length() != 0) {
                buttonSubmitName.setEnabled(true);
            } else {
                buttonSubmitName.setEnabled(false);
            }
        }
    };

    private void insertPlayer() {

        mMainPlayerDataSource.insertPlayer(playerLeft, new PlayerDataSource.PlayerCallback() {
            @Override
            public void onSuccess(Player player) {
                playerLeft = player;

            }

            @Override
            public void onFailure() {

            }
        });
        mMainPlayerDataSource.insertPlayer(playerRight, new PlayerDataSource.PlayerCallback() {
            @Override
            public void onSuccess(Player player) {
                playerRight = player;
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "initPlayer onFailure");

            }

        });
    }
}
