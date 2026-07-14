package com.emotibot.robotvision.game.trexrun.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emotibot.intellieyecoreaar.InferResult;
import com.emotibot.intellieyecoreaar.IntelliEyeCoreManager;
import com.emotibot.robotvision.game.trexrun.Constants.GameConstant;
import com.emotibot.robotvision.game.trexrun.R;
import com.emotibot.robotvision.game.trexrun.SinglePlayerResult;
import com.emotibot.robotvision.game.trexrun.Utility;
import com.emotibot.robotvision.game.trexrun.model.Player;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.emotibot.robotvision.game.trexrun.activity.StartActivity.BUNDLE_MATCH_ID;
import static com.emotibot.robotvision.game.trexrun.activity.StartActivity.BUNDLE_PLAY_LEFT;
import static com.emotibot.robotvision.game.trexrun.activity.StartActivity.BUNDLE_PLAY_RIGHT;
import static org.opencv.core.Core.flip;

public class PlayerCheckActivity extends AppCompatActivity {

    private static final String TAG = PlayerCheckActivity.class.getSimpleName();
    @BindView(R.id.camera_impl)
    JavaCameraView cameraImpl;
    @BindView(R.id.txtPlayerUp)
    TextView txtPlayerLeft;
    @BindView(R.id.txtPlayerDown)
    TextView txtPlayerRight;
    @BindView(R.id.txtGameId)
    TextView txtGameId;

    private Mat mCameraBuffer = null;
    boolean isCheckLeftDone = false;
    boolean isCheckRightDone = true;
    private Mat doneFrame;
    private Player playerLeft, playerRight;
    private static final String LIB_NAME_OPEN_CV = "opencv_java3";

    public static final long PLAYER_CHECK_GAP = 300;
    private int matchId;

    private long prevProcessorTime = 0;

    static {
        try {
            System.loadLibrary(LIB_NAME_OPEN_CV);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check);
        ButterKnife.bind(this);
        if (matchId == 0)
            matchId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        txtGameId.setText(getString(R.string.match_id, matchId));
        txtPlayerLeft.setText(getString(R.string.player_icon_checking));
        txtPlayerRight.setText(getString(R.string.player_icon_checking));

        IntelliEyeCoreManager.getInstance().init(this, true, true, false, false, false);
        cameraImpl.setCvCameraViewListener(new CameraFrameProcessor());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != cameraImpl) {
            cameraImpl.setVisibility(View.VISIBLE);
            cameraImpl.enableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != cameraImpl) {
            cameraImpl.disableView();
            cameraImpl.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != cameraImpl) {
            cameraImpl.disableView();
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
            Mat cameraFrame = inputFrame.rgba();
            flip(cameraFrame, cameraFrame, 1);
            Imgproc.cvtColor(cameraFrame, mCameraBuffer, Imgproc.COLOR_RGBA2BGR);

            final InferResult mInferResult = IntelliEyeCoreManager.getInstance().processFrame(mCameraBuffer, cameraFrame
                    , true);

            if (System.currentTimeMillis() - prevProcessorTime > PLAYER_CHECK_GAP && (!isCheckLeftDone || !isCheckRightDone)) {
                prevProcessorTime = System.currentTimeMillis();

                checkPlayerStatus(mInferResult, cameraFrame.cols());

                if (isCheckLeftDone && isCheckRightDone) {
                    doneFrame = cameraFrame;

                    final Bitmap bmpPreview = Bitmap.createBitmap(cameraFrame.cols(),
                            cameraFrame.rows(), Bitmap.Config.RGB_565);
                    Utils.matToBitmap(cameraFrame, bmpPreview);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initPlayer(bmpPreview);
                            gotoGameActivity();
                        }
                    }).start();
                }
            }
            if (isCheckLeftDone && isCheckRightDone)
                return null;
            else
                return cameraFrame;
        }
    }

    private void gotoGameActivity() {
        cameraImpl.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_PLAY_LEFT, playerLeft);
                bundle.putParcelable(BUNDLE_PLAY_RIGHT, playerRight);
                bundle.putInt(BUNDLE_MATCH_ID, matchId);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private void checkPlayerStatus(InferResult mInferResult, int previewWidth) {
        List<SinglePlayerResult> playerResults = Utility.splitInferResultToPlayer(mInferResult);
        for (int i = 0; i < playerResults.size(); i++) {
            SinglePlayerResult result = playerResults.get(i);
            if (result.getPositionIndex() == 0) {
                if (result.getWidth() > GameConstant.PLAY_LEFT_CHECK_WIDTH && result.getHeight() > GameConstant.PLAY_LEFT_CHECK_HEIGHT
                        && result.getPos_x() > GameConstant.PLAY_LEFT_CHECK_X && result.getPos_y() > GameConstant.PLAY_LEFT_CHECK_Y
                        && result.getPos_x() + result.getWidth() < previewWidth / 2 - 50) {
                    isCheckLeftDone = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtPlayerLeft.setText(R.string.player_icon_check_done);
                        }
                    });
                } else {
                    isCheckLeftDone = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtPlayerLeft.setText(R.string.player_icon_checking);
                        }
                    });
                }
            } else if (result.getPositionIndex() == 1) {
                if (result.getWidth() > GameConstant.PLAY_RIGHT_CHECK_WIDTH && result.getHeight() > GameConstant.PLAY_RIGHT_CHECK_HEIGHT
                        && result.getPos_x() > GameConstant.PLAY_RIGHT_CHECK_X && result.getPos_y() > GameConstant.PLAY_RIGHT_CHECK_Y
                        && result.getPos_x() + result.getWidth() < previewWidth - 50) {
                    isCheckRightDone = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtPlayerRight.setText(R.string.player_icon_check_done);
                        }
                    });
                } else {
                    isCheckRightDone = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtPlayerRight.setText(R.string.player_icon_checking);
                        }
                    });
                }
            }
//            Log.d(TAG, result.getPositionIndex() + ",result x:" + result.getPos_x() + ",y:" + result.getPos_y()
//                    + ",w,h:" + result.getWidth() + "*" + result.getHeight());
        }
    }

    private void initPlayer(Bitmap bmpPreview) {
        Bitmap bmpPlayerLeft = Bitmap.createBitmap(bmpPreview, (int) (GameConstant.PLAY_LEFT_CHECK_X / 2)
                , 0, (int) (bmpPreview.getWidth() / 2 - GameConstant.PLAY_LEFT_CHECK_X / 2), bmpPreview.getHeight() - 10);
        Bitmap bmpPlayerRight = Bitmap.createBitmap(bmpPreview, bmpPreview.getWidth() / 2 + 5, 0, bmpPreview.getWidth() / 2 - 10, bmpPreview.getHeight() - 10);
        playerLeft = new Player();
        playerRight = new Player();
        playerRight.setGroup(1);
        playerLeft.setName("Left " + ((int) (Math.random() * 100)));
        playerRight.setName("Right " + ((int) (Math.random() * 100)));
        playerLeft.setGroup(0);
        File leftFile = new File(getApplicationContext().getCacheDir(), matchId + "-left.jpg");
        File rightFile = new File(getApplicationContext().getCacheDir(), matchId + "-right.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(leftFile);
            bmpPlayerLeft.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            playerLeft.setImageHead(leftFile.getPath());
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        try {
            FileOutputStream fos = new FileOutputStream(rightFile);
            bmpPlayerRight.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            playerRight.setImageHead(rightFile.getPath());
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        Log.d(TAG, "Player image path:" + playerRight.getImageHead() + "," + playerLeft.getImageHead());

    }
}
