package com.emotibot.robotvision.game.trexrun.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emotibot.intellieyecoreaar.InferResult;
import com.emotibot.intellieyecoreaar.IntelliEyeCoreManager;
import com.emotibot.robotvision.game.trexrun.R;
import com.emotibot.robotvision.game.trexrun.Utility;
import com.emotibot.robotvision.game.trexrun.model.Player;
import com.emotibot.robotvision.game.trexrun.model.RemoteLightService;
import com.emotibot.robotvision.game.trexrun.sound.GameSoundPool;
import com.emotibot.robotvision.game.trexrun.view.GameView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.FRAME_TIIME;
import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.GAME_TIME_LIMIT;
import static com.emotibot.robotvision.game.trexrun.activity.StartActivity.BUNDLE_MATCH_ID;
import static com.emotibot.robotvision.game.trexrun.activity.StartActivity.BUNDLE_PLAY_LEFT;
import static com.emotibot.robotvision.game.trexrun.activity.StartActivity.BUNDLE_PLAY_RIGHT;
import static java.lang.Thread.sleep;
import static org.opencv.core.Core.flip;

public class GameActivity extends AppCompatActivity {

    private static final String LIB_NAME_OPEN_CV = "opencv_java3";
    private static final String TAG = GameActivity.class.getSimpleName();

    static {
        try {
            System.loadLibrary(LIB_NAME_OPEN_CV);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    JavaCameraView mCameraView = null;
    @BindView(R.id.txtRawData)
    TextView txtRawData;
    @BindView(R.id.cameraBlock)
    RelativeLayout camerBlock;
    @BindView(R.id.imgUser)
    ImageView imgPreview;
    @BindView(R.id.rootRel)
    RelativeLayout rootRel;
    @BindView(R.id.imgReady)
    ImageView imgReady;
    @BindView(R.id.txtTime)
    TextView txtTime;
    @BindView(R.id.imgEnd)
    ImageView imgEnd;
    @BindView(R.id.txtPlayerLeft)
    TextView txtPlayerLeft;
    @BindView(R.id.txtPlayerRight)
    TextView txtPlayerRight;

    private Mat mCameraBuffer = null;

    private RemoteLightService remoteLightService;
    private GameSoundPool sounds;
    private GameView gameView;
    private Bitmap bmpPreview;
    private InferResult mInferResult;
    private boolean isPlaying;
    private boolean isReady;
    private boolean isScreenTaked = false;
    private long startTime = 0;
    private long readyTime = 0;

    private Player playerLeft, playerRight;
    private int matchId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        playerLeft = bundle.getParcelable(StartActivity.BUNDLE_PLAY_LEFT);
        playerRight = bundle.getParcelable(StartActivity.BUNDLE_PLAY_RIGHT);
        matchId = bundle.getInt(BUNDLE_MATCH_ID);

        camerBlock.setVisibility(View.INVISIBLE);

        sounds = new GameSoundPool(this);
        sounds.initGameSound();

        remoteLightService = RemoteLightService.getInstance();

        mCameraView = (JavaCameraView) findViewById(R.id.camera_impl);

        IntelliEyeCoreManager.getInstance().init(this, false, true, false, false, false);

        mCameraView.setCvCameraViewListener(new CameraFrameProcessor());
        startReadyGame();
        startGame();
    }

    private void startReadyGame() {
        isReady = true;
        imgEnd.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                readyTime = System.currentTimeMillis();
                while (isReady) {
                    try {
                        sleep(FRAME_TIIME);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                int reTime = (int) (System.currentTimeMillis() - readyTime) / 1000;
                                if (isReady) {
                                    if (reTime >= 4) {
                                        imgReady.setVisibility(View.INVISIBLE);
                                        isReady = false;
                                    } else {
                                        imgReady.setVisibility(View.VISIBLE);
                                        if (reTime == 1) {
                                            imgReady.setImageResource(R.drawable.ic_time_3);
                                        } else if (reTime == 2) {
                                            imgReady.setImageResource(R.drawable.ic_time_2);
                                        } else if (reTime == 3) {
                                            imgReady.setImageResource(R.drawable.ic_time_1);
                                        }
                                    }
                                    imgPreview.setImageBitmap(bmpPreview);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        String min = Long.toString(GAME_TIME_LIMIT / 60);
        String sec = Long.toString(GAME_TIME_LIMIT % 60);
        txtTime.setText(min + ":" + sec);
    }


    private void showEndGame() {
        imgReady.setVisibility(View.INVISIBLE);
        imgEnd.setVisibility(View.VISIBLE);
    }

    private void startGame() {
        imgEnd.setVisibility(View.INVISIBLE);
        imgReady.setVisibility(View.INVISIBLE);

        gameView = new GameView(this, sounds);
        gameView.setPlayerLeft(playerLeft);
        gameView.setPlayerRight(playerRight);
        rootRel.addView(gameView, 1);
        isPlaying = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startTime = System.currentTimeMillis();
                while (isPlaying) {
                    try {
                        sleep(FRAME_TIIME);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                int playTime = (int) (System.currentTimeMillis() - startTime) / 1000;
                                if (playTime >= GAME_TIME_LIMIT && isPlaying) {
                                    isPlaying = false;
                                    showEndGame();
                                    String min = Long.toString(GAME_TIME_LIMIT / 60);
                                    String sec = Long.toString(GAME_TIME_LIMIT % 60);
                                    txtTime.setText(min + ":" + sec);
                                    gotoFinishActivity();
                                } else {

                                    String min = Long.toString((GAME_TIME_LIMIT - playTime) / 60);
                                    String sec = Long.toString((GAME_TIME_LIMIT - playTime) % 60);
                                    txtTime.setText(min + ":" + sec);
                                    if (gameView != null)
                                        gameView.setAnalyzeResult(mInferResult);
                                    if (playTime == GAME_TIME_LIMIT / 2 && !isScreenTaked) {
                                        isScreenTaked = true;
                                        playerRight.setImageInPlayingPath(getCacheDir() + "public-screenshot-1.jpg");
                                        playerLeft.setImageInPlayingPath(getCacheDir() + "public-screenshot-1.jpg");
                                        Utility.takeScreenshot(playerRight.getImageInPlayingPath(), GameActivity.this);
                                        Utility.takeScreenshot(playerRight.getImageInPlayingPath(), GameActivity.this);

                                    }
                                    imgPreview.setImageBitmap(bmpPreview);
                                    txtPlayerLeft.setText("X" + gameView.getPlayerLeft().getScore());
                                    txtPlayerRight.setText("X" + gameView.getPlayerRight().getScore());
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void gotoFinishActivity() {
        gameView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), FinishActivity.class);
                Bundle bundle = new Bundle();
                playerLeft.setScore(gameView.getPlayerLeft().getScore());
                playerRight.setScore(gameView.getPlayerRight().getScore());
                bundle.putParcelable(BUNDLE_PLAY_LEFT, playerLeft);
                bundle.putParcelable(BUNDLE_PLAY_RIGHT, playerRight);
                bundle.putInt(BUNDLE_MATCH_ID, matchId);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mCameraView) {
            mCameraView.setVisibility(View.VISIBLE);
            mCameraView.enableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mCameraView) {
            mCameraView.disableView();
            mCameraView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCameraView) {
            mCameraView.disableView();
        }
    }


    private class CameraFrameProcessor implements CameraBridgeViewBase.CvCameraViewListener2 {
        @Override
        public void onCameraViewStarted(int width, int height) {
            mCameraBuffer = new Mat(width, height, CvType.CV_8UC1);
        }

        @Override
        public void onCameraViewStopped() {
            if (null != mCameraBuffer) {
                mCameraBuffer.release();
                mCameraBuffer = null;
            }
        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            final Mat cameraFrame = inputFrame.rgba();
            if (isReady || isPlaying) {
                flip(cameraFrame, cameraFrame, 1);
                Imgproc.cvtColor(cameraFrame, mCameraBuffer, Imgproc.COLOR_RGBA2BGR);
                mInferResult = IntelliEyeCoreManager.getInstance().processFrame(mCameraBuffer, cameraFrame
                        , true);
                bmpPreview = Bitmap.createBitmap(cameraFrame.cols(),
                        cameraFrame.rows(), Bitmap.Config.RGB_565);
                Utils.matToBitmap(cameraFrame, bmpPreview);
            }
            return cameraFrame;
        }
    }

}
