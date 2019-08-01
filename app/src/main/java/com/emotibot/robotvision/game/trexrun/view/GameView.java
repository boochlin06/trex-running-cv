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
import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.PRODUCE_OBSTACLE_DISTANCE_GAP;

public class GameView extends View {
    public static String TAG = GameView.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private GameObjectFactory factory;
    private Paint paint;

    private Player playerUp, playerDown;
    private PlayerDragon playerDragonUp, playerDragonDown;
    private List<Obstacle> obstacleUpList;
    private List<Obstacle> obstacleDownList;

    private long prevCheckPlayerScoreTime = 0;

    private int rightObstacleCount = 0;
    private int leftObstacleCount = 0;
    private int totalObstacleCount = 0;
    private Bitmap backgroundCloudTop;
    private Bitmap backgroundCloudDown;
    private int cloudUpX1 = 0, cloudUpX2;
    private int cloudDownX1 = 0, cloudDownX2;
    private int sceenHeight, screenWidth = 0;
    private int speed = GameConstant.GAMESPEED;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCloud(canvas);
        if (playerDragonUp.isMoving() && playerUp.getScore() % PRODUCE_OBSTACLE_DISTANCE_GAP == 0) {
            produceNewObstacle(0);
        }
        if (playerDragonDown.isMoving() && playerDown.getScore() % PRODUCE_OBSTACLE_DISTANCE_GAP == 0) {
            produceNewObstacle(1);
        }
        obstacleViewLogic();

        List<Obstacle> release = new ArrayList();
        for (int i = 0; i < obstacleUpList.size(); i++) {
            Obstacle obstacle = obstacleUpList.get(i);
            obstacle.drawSelf(canvas);
            if (obstacle.isObjectRecyclable()) {
                release.add(obstacle);
                obstacle.release();
            }
        }
        release = new ArrayList();
        obstacleUpList.removeAll(release);
        for (int i = 0; i < obstacleDownList.size(); i++) {
            Obstacle obstacle = obstacleDownList.get(i);
            obstacle.drawSelf(canvas);
            if (obstacle.isObjectRecyclable()) {
                release.add(obstacle);
                obstacle.release();
            }
        }
        obstacleDownList.removeAll(release);
        playerDragonUp.drawSelf(canvas);
        playerDragonDown.drawSelf(canvas);
        playerUp.setScore(playerDragonUp.isMoving() ? playerUp.getScore() + 1 : playerUp.getScore());
        playerDown.setScore(playerDragonDown.isMoving() ? playerDown.getScore() + 1 : playerDown.getScore());
    }

    private synchronized void obstacleViewLogic() {
        for (int i = 0; i < obstacleUpList.size(); i++) {
            Obstacle obstacle = obstacleUpList.get(i);
            // In fire range
            if (obstacle.getObject_x() < playerDragonUp.getObject_x() + playerDragonUp.getObject_width() * 2 / 3) {
                if (!obstacle.isFired()) {
                    if (playerDragonUp.isFiring()) {
                        obstacle.setFired(true);
                        obstacle.setMoving(false);
                        setPlayerStatus(obstacleUpList, playerDragonUp, false);
                        break;
                    } else {
                        setPlayerStatus(obstacleUpList, playerDragonUp, false);
                        break;
                    }
                } else {
                    setPlayerStatus(obstacleUpList, playerDragonUp, true);
                }
            } else {
                if (playerDragonUp.isFiring()) {
                    setPlayerStatus(obstacleUpList, playerDragonUp, false);
                } else {

                    setPlayerStatus(obstacleUpList, playerDragonUp, true);
                }
                break;
            }
        }

        for (int i = 0; i < obstacleDownList.size(); i++) {
            Obstacle obstacle = obstacleDownList.get(i);
            // In fire range
            if (obstacle.getObject_x() < playerDragonDown.getObject_x() + playerDragonDown.getObject_width() * 2 / 3) {
                if (!obstacle.isFired()) {
                    if (playerDragonDown.isFiring()) {
                        obstacle.setFired(true);
                        obstacle.setMoving(false);
                        setPlayerStatus(obstacleDownList, playerDragonDown, false);
                        break;
                    } else {
                        setPlayerStatus(obstacleDownList, playerDragonDown, false);
                        break;
                    }
                } else {
                    setPlayerStatus(obstacleDownList, playerDragonDown, true);
                }
            } else {
                if (playerDragonDown.isFiring()) {
                    setPlayerStatus(obstacleDownList, playerDragonDown, false);
                } else {
                    setPlayerStatus(obstacleDownList, playerDragonDown, true);
                }
                break;
            }
        }
    }

    private void setPlayerStatus(List<Obstacle> obstacleList, PlayerDragon dragon, boolean move) {
        for (int i = 0; i < obstacleList.size(); i++) {
            Obstacle obstacle = obstacleList.get(i);
            obstacle.setMoving(move);
        }
        dragon.setMoving(move);
    }

    private void drawCloud(Canvas canvas) {
        canvas.drawBitmap(backgroundCloudTop, cloudUpX1, 200, paint);
        canvas.drawBitmap(backgroundCloudTop, cloudUpX2, 200, paint);
        canvas.drawBitmap(backgroundCloudDown, cloudDownX1, 900, paint);
        canvas.drawBitmap(backgroundCloudDown, cloudDownX2, 900, paint);
        viewCloudLogic();
    }

    public synchronized void viewCloudLogic() {
        speed = playerDragonDown.isMoving() ? GameConstant.GAMESPEED : 2;
        if (cloudDownX1 > cloudDownX2) {
            cloudDownX1 -= speed;
            cloudDownX2 = cloudDownX1 + backgroundCloudDown.getWidth();
        } else {
            cloudDownX2 -= speed;
            cloudDownX1 = cloudDownX2 + backgroundCloudDown.getWidth();
        }
        if (cloudDownX1 >= backgroundCloudTop.getWidth()) {
            cloudDownX1 = cloudDownX2 - backgroundCloudDown.getWidth();
        } else if (cloudDownX2 >= backgroundCloudTop.getWidth()) {
            cloudDownX2 = cloudDownX1 - backgroundCloudDown.getWidth();
        }

        speed = playerDragonUp.isMoving() ? GameConstant.GAMESPEED : 2;
        if (cloudUpX1 > cloudUpX2) {
            cloudUpX1 -= speed;
            cloudUpX2 = cloudUpX1 + backgroundCloudTop.getWidth();
        } else {
            cloudUpX2 -= speed;
            cloudUpX1 = cloudUpX2 + backgroundCloudTop.getWidth();
        }
        if (cloudUpX1 >= backgroundCloudTop.getWidth()) {
            cloudUpX1 = cloudUpX2 - backgroundCloudTop.getWidth();
        } else if (cloudUpX2 >= backgroundCloudTop.getWidth()) {
            cloudUpX2 = cloudUpX1 - backgroundCloudTop.getWidth();
        }
    }

    private synchronized void produceNewObstacle(int group) {
        Obstacle obstacle = factory.createRandomObstacle(getResources(), group);
        obstacle.setObject_width(getWidth());
        obstacle.setGroup(group);
        obstacle.setObject_x((float) (getWidth() - Math.random() * 150));
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

    private synchronized void setPlayerFired(InferResult inferResult) {
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

    public void setAnalyzeResult(InferResult inferResult) {

        if (System.currentTimeMillis() - prevCheckPlayerScoreTime > CHECK_GET_SCORE_JUDGE_TIME) {
            prevCheckPlayerScoreTime = System.currentTimeMillis();
            setPlayerFired(inferResult);
        }

        invalidate();
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

    public void initBitmap() {
        backgroundCloudTop = BitmapFactory.decodeResource(getResources(), R.drawable.bg_cloud);
        backgroundCloudDown = BitmapFactory.decodeResource(getResources(), R.drawable.bg_cloud);
    }

}
