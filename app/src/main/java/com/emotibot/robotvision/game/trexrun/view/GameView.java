package com.emotibot.robotvision.game.trexrun.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.emotibot.intellieyecoreaar.InferResult;
import com.emotibot.robotvision.game.trexrun.Constants.GameConstant;
import com.emotibot.robotvision.game.trexrun.R;
import com.emotibot.robotvision.game.trexrun.SinglePlayerResult;
import com.emotibot.robotvision.game.trexrun.Utility;
import com.emotibot.robotvision.game.trexrun.factory.GameObjectFactory;
import com.emotibot.robotvision.game.trexrun.factory.Obstacle;
import com.emotibot.robotvision.game.trexrun.factory.PlayerDragon;
import com.emotibot.robotvision.game.trexrun.model.Player;

import java.util.ArrayList;
import java.util.List;

import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.CHECK_GET_SCORE_JUDGE_TIME;
import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.PRODUCE_OBSTACLE_COUNT_IN_ONE_GAP;
import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.PRODUCE_OBSTACLE_TIME_GAP;

public class GameView extends View {
    public static String TAG = GameView.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private GameObjectFactory factory;
    private Paint paint;

    private Player playerUp, playerDown;
    private PlayerDragon playerDragonUp, playerDragonDown;
    private List<Obstacle> obstacleUpList;
    private List<Obstacle> obstacleDownList;

    private long prevProduceObstacleTime = 0;
    private long prevCheckPlayerScoreTime = 0;

    private int rightObstacleCount = 0;
    private int leftObstacleCount = 0;
    private int totalObstacleCount = 0;
    private Bitmap backgroundCloudTop;
    private Bitmap backgroundCloudDown;
    private int cloudUpX1 = 0, cloudUpX2;
    private int cloudDownX1 = 0, cloudDownX2;
    private int sceenHeight, screenWidth = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCloud(canvas);

        synchronized (GameView.class) {
            if (System.currentTimeMillis() - prevProduceObstacleTime > PRODUCE_OBSTACLE_TIME_GAP) {
                produceNewObstacle(PRODUCE_OBSTACLE_COUNT_IN_ONE_GAP);
                prevProduceObstacleTime = System.currentTimeMillis();
            }
        }
        List<Obstacle> release = new ArrayList();

        if (obstacleUpList.size() > 0
                && obstacleUpList.get(0).getObject_x() < playerDragonUp.getObject_x() + playerDragonUp.getObject_width() / 2
                && !obstacleUpList.get(0).isFired()) {
            if (playerDragonUp.isFiring()) {
                obstacleUpList.get(0).setFired(true);
                for (int i = 0; i < obstacleUpList.size(); i++) {
                    Obstacle obstacle = obstacleUpList.get(i);
                    obstacle.setMoving(true);
                }
                playerDragonUp.setMoving(true);
            } else {
                for (int i = 0; i < obstacleUpList.size(); i++) {
                    Obstacle obstacle = obstacleUpList.get(i);
                    obstacle.setMoving(false);
                }
                playerDragonUp.setMoving(false);
            }
        } else {
            for (int i = 0; i < obstacleUpList.size(); i++) {
                Obstacle obstacle = obstacleUpList.get(i);
                obstacle.setMoving(true);
            }
            playerDragonUp.setMoving(true);
        }

        for (int i = 0; i < obstacleUpList.size(); i++) {
            Obstacle obstacle = obstacleUpList.get(i);
            obstacle.drawSelf(canvas);
            if (obstacle.isObjectRecyclable()) {
                release.add(obstacle);
                obstacle.release();
            }
        }
        obstacleUpList.removeAll(release);
        release = new ArrayList();
        if (obstacleDownList.size() > 0
                && obstacleDownList.get(0).getObject_x() < playerDragonDown.getObject_x() + playerDragonDown.getObject_width() / 2
                || playerDragonDown.isFiring()) {
            for (int i = 0; i < obstacleDownList.size(); i++) {
                Obstacle obstacle = obstacleDownList.get(i);
                obstacle.setMoving(false);
            }
            playerDragonDown.setMoving(false);
        } else {
            for (int i = 0; i < obstacleDownList.size(); i++) {
                Obstacle obstacle = obstacleDownList.get(i);
                obstacle.setMoving(true);
            }
            playerDragonDown.setMoving(true);
        }

        for (int i = 0; i < obstacleDownList.size(); i++) {
            Obstacle obstacle = obstacleDownList.get(i);
            obstacle.drawSelf(canvas);
            if (obstacle.isObjectRecyclable()) {
                release.add(obstacle);
                obstacle.release();
            }
        }
        playerDragonUp.drawSelf(canvas);
        playerDragonDown.drawSelf(canvas);
//        Log.d("test", "screen width:" + getWidth() + ",dragon width:" + playerDragonUp.getObject_width() + ",cloud width:" + backgroundCloudTop.getWidth());
    }

    public Player getPlayerUp() {
        return playerUp;
    }

    public void setPlayerUp(Player playerUp) {
        this.playerUp = playerUp;
    }

    public Player getPlayerDown() {
        return playerDown;
    }

    public void setPlayerDown(Player playerDown) {
        this.playerDown = playerDown;
    }

    private void drawCloud(Canvas canvas) {
        canvas.drawBitmap(backgroundCloudTop, cloudUpX1, 200, paint);
        canvas.drawBitmap(backgroundCloudTop, cloudUpX2, 200, paint);
        canvas.drawBitmap(backgroundCloudDown, cloudDownX1, 900, paint);
        canvas.drawBitmap(backgroundCloudDown, cloudDownX2, 900, paint);
        viewCloudLogic();
    }

    public void viewCloudLogic() {
        if (cloudDownX1 > cloudDownX2) {
            if (!playerDragonDown.isFiring() && playerDragonDown.isMoving()) {
                cloudDownX1 -= 10;
                cloudDownX2 = cloudDownX1 + backgroundCloudDown.getWidth();
            }
        } else {
            if (!playerDragonDown.isFiring() && playerDragonDown.isMoving()) {
                cloudDownX2 -= 10;
                cloudDownX1 = cloudDownX2 + backgroundCloudDown.getWidth();
            }
        }
        if (cloudDownX1 >= backgroundCloudTop.getWidth()) {
            cloudDownX1 = cloudDownX2 - backgroundCloudDown.getWidth();
        } else if (cloudDownX2 >= backgroundCloudTop.getWidth()) {
            cloudDownX2 = cloudDownX1 - backgroundCloudDown.getWidth();
        }

        if (cloudUpX1 > cloudUpX2) {
            if (!playerDragonUp.isFiring() && playerDragonUp.isMoving()) {
                cloudUpX1 -= 10;
                cloudUpX2 = cloudUpX1 + backgroundCloudTop.getWidth();
            }
        } else {
            if (!playerDragonUp.isFiring() && playerDragonUp.isMoving()) {
                cloudUpX2 -= 10;
                cloudUpX1 = cloudUpX2 + backgroundCloudTop.getWidth();
            }
        }
        if (cloudUpX1 >= backgroundCloudTop.getWidth()) {
            cloudUpX1 = cloudUpX2 - backgroundCloudTop.getWidth();
        } else if (cloudUpX2 >= backgroundCloudTop.getWidth()) {
            cloudUpX2 = cloudUpX1 - backgroundCloudTop.getWidth();
        }
    }

    private synchronized void produceNewObstacle(int num) {
        for (int i = 0; i < num; i++) {
            totalObstacleCount++;
            int group = (int) totalObstacleCount % 2;
            Obstacle obstacle = factory.createRandomObstacle(getResources(), group);
            obstacle.setObject_width(getWidth());
            obstacle.setGroup(group);
            obstacle.setObject_x((float) (getWidth() + Math.random() * 100));
            obstacle.setObject_y(group == 0 ? GameConstant.PLAYER1_START_GROUND_Y - obstacle.getObject_height()
                    : GameConstant.PLAYER2_START_GROUND_Y - obstacle.getObject_height());
            if (group == 0) {
                obstacleUpList.add(obstacle);
            } else {
                obstacleDownList.add(obstacle);
            }

            Log.d(TAG, "produce dragon: w,h:" + obstacle.getObject_width() + "*" + obstacle.getObject_height()
                    + ",group:" + obstacle.getGroup() + ",total leftObstacleCount:" + leftObstacleCount
                    + ",rightObstacleCount:" + rightObstacleCount + ", total:" + totalObstacleCount);
        }
    }

    public GameView(Context context) {
        super(context);
        init(context);
        this.setBackgroundResource(R.drawable.bg_game);
    }

    private void init(Context context) {
        paint = new Paint();
        initBitmap();

        sceenHeight = getHeight();
        screenWidth = getWidth();
        playerUp = new Player();
        playerDown = new Player();
        playerDragonUp = new PlayerDragon(context.getResources(), 0);
        playerDragonUp.setObject_y(GameConstant.PLAYER1_START_GROUND_Y - playerDragonUp.getObject_height());
        playerDragonUp.setObject_x(GameConstant.PLAYER_START_X);
        playerDragonDown = new PlayerDragon(context.getResources(), 1);
        playerDragonDown.setObject_y(GameConstant.PLAYER2_START_GROUND_Y - playerDragonDown.getObject_height());
        playerDragonDown.setObject_x(GameConstant.PLAYER_START_X);

        factory = new GameObjectFactory();
        obstacleUpList = new ArrayList<>();
        obstacleDownList = new ArrayList<>();
        cloudDownX1 = 0;
        cloudUpX1 = 0;
        cloudDownX2 = cloudDownX1 - screenWidth;
        cloudUpX2 = cloudUpX1 - screenWidth;

    }


    public void setAnalyzeResult(InferResult inferResult) {

        if (System.currentTimeMillis() - prevCheckPlayerScoreTime > CHECK_GET_SCORE_JUDGE_TIME) {
            prevCheckPlayerScoreTime = System.currentTimeMillis();
            checkPlayerFired(inferResult);
        }

        invalidate();
    }

    private void checkBlock() {

    }

    private synchronized void checkPlayerFired(InferResult inferResult) {
        // nine emotion 0 angry 1 disgust 2 happy 3 sad 4 surprise 5 fear 6 neutral 7 contempt 8 confused
        if (inferResult == null) {
            return;
        }
        List<SinglePlayerResult> playerResults = Utility.splitInferResultToPlayer(inferResult);

        for (int j = 0; j < playerResults.size(); j++) {
            SinglePlayerResult player = playerResults.get(j);
            if (player.getPositionIndex() == 0) {
                playerDragonUp.setFiring(player.isLipOpen());
            } else {
                playerDragonDown.setFiring(player.isLipOpen());
            }
        }
        if (playerResults.size() == 0) {
            playerDragonDown.setFiring(false);
            playerDragonUp.setFiring(false);
        }
    }

    public void initBitmap() {
        backgroundCloudTop = BitmapFactory.decodeResource(getResources(), R.drawable.bg_cloud);
        backgroundCloudDown = BitmapFactory.decodeResource(getResources(), R.drawable.bg_cloud);
    }

}
