package com.emotibot.robotvision.game.trexrun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.View;

import com.emotibot.intellieyecoreaar.InferResult;
import com.emotibot.robotvision.game.trexrun.R;
import com.emotibot.robotvision.game.trexrun.SinglePlayerResult;
import com.emotibot.robotvision.game.trexrun.Utility;
import com.emotibot.robotvision.game.trexrun.factory.GameObjectFactory;
import com.emotibot.robotvision.game.trexrun.factory.Obstacle;
import com.emotibot.robotvision.game.trexrun.model.Player;
import com.emotibot.robotvision.game.trexrun.sound.GameSoundPool;

import java.util.ArrayList;
import java.util.List;

import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.CHECK_GET_SCORE_JUDGE_TIME;
import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.PRODUCE_DRAGON_TIME_GAP;
import static com.emotibot.robotvision.game.trexrun.Constants.GameConstant.PRODUCE_OBSTACLE_COUNT_IN_ONE_GAP;

public class GameView extends View {
    public static String TAG = GameView.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private GameObjectFactory factory;
    private Paint paint;

    private Player playerLeft, playerRight;
    private List<Obstacle> obstacleList;

    private long prevProduceObstacleTime = 0;
    private long prevCheckPlayerScoreTime = 0;

    private int rightObstacleCount = 0;
    private int leftObstacleCount = 0;
    private int totalObstacleCount = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (GameView.class) {
            if (System.currentTimeMillis() - prevProduceObstacleTime > PRODUCE_DRAGON_TIME_GAP) {
                produceNewObstacle(PRODUCE_OBSTACLE_COUNT_IN_ONE_GAP);
                prevProduceObstacleTime = System.currentTimeMillis();
            }
        }
        List<Obstacle> release = new ArrayList();

        for (int i = 0; i < obstacleList.size(); i++) {
            Obstacle obstacle = obstacleList.get(i);
            obstacle.drawSelf(canvas);
            if (obstacle.isObjectRecyclable()) {
                release.add(obstacle);
//                obstacle.release();
            }
        }
        obstacleList.removeAll(release);
    }

    public Player getPlayerLeft() {
        return playerLeft;
    }

    public void setPlayerLeft(Player playerLeft) {
        this.playerLeft = playerLeft;
    }

    public Player getPlayerRight() {
        return playerRight;
    }

    public void setPlayerRight(Player playerRight) {
        this.playerRight = playerRight;
    }


    private synchronized void produceNewObstacle(int num) {
        for (int i = 0; i < num; i++) {
            totalObstacleCount++;
            int group = (int) totalObstacleCount % 2;



//            Log.d(TAG, "produce dragon: w,h:" + dragon.getObject_width() + "*" + dragon.getObject_height()
//                    + ",group:" + dragon.getGroup() + ",type:" + dragonType + ",total leftObstacleCount:" + leftObstacleCount
//                    + ",rightObstacleCount:" + rightObstacleCount + ", total:" + totalObstacleCount);
        }
    }

    public GameView(Context context, GameSoundPool sounds) {
        super(context);
        init(context, sounds);
        this.setBackgroundResource(R.drawable.bg_game);
    }

    private void init(Context context, GameSoundPool sounds) {
        paint = new Paint();

        initBitmap();

        playerLeft = new Player();
        playerRight = new Player();

        mMediaPlayer = MediaPlayer.create(context, R.raw.game);
        mMediaPlayer.setLooping(true);
//        if (!mMediaPlayer.isPlaying()) {
//            mMediaPlayer.start();
//        }

        factory = new GameObjectFactory();
        obstacleList = new ArrayList<>();
    }


    public void setAnalyzeResult(InferResult inferResult) {

        if (System.currentTimeMillis() - prevCheckPlayerScoreTime > CHECK_GET_SCORE_JUDGE_TIME) {
            prevCheckPlayerScoreTime = System.currentTimeMillis();
            checkPlayerScore(inferResult);
        }

        invalidate();
    }

    private synchronized void checkPlayerScore(InferResult inferResult) {
        // nine emotion 0 angry 1 disgust 2 happy 3 sad 4 surprise 5 fear 6 neutral 7 contempt 8 confused
        if (inferResult == null) {
            return;
        }
        List<SinglePlayerResult> playerResults = Utility.splitInferResultToPlayer(inferResult);

        for (int j = 0; j < playerResults.size(); j++) {
            SinglePlayerResult player = playerResults.get(j);
            float maxNeutralY = 0;
            float maxAngryY = 0;
            float maxHappyY = 0;
            int saveIndex = -1;
            for (int i = 0; i < obstacleList.size(); i++) {
                Obstacle obstacle = obstacleList.get(i);
                if (player.getEmotionIndex() == 6) {
                    if (maxNeutralY < obstacle.getObject_y()) {
                        maxNeutralY = obstacle.getObject_y();
                        saveIndex = i;
                    }
                }
            }

            if (saveIndex != -1 && !obstacleList.get(saveIndex).isAlive()) {
                obstacleList.get(saveIndex).setKeepScoreTime(System.currentTimeMillis());
                obstacleList.get(saveIndex).setAlive(true);
                if (obstacleList.get(saveIndex).getGroup() == 0) {
                    playerLeft.setScore(playerLeft.getScore() + 1);
                } else {
                    playerRight.setScore(playerRight.getScore() + 1);
                }
            }
        }
    }

    public void initBitmap() {

    }


}
