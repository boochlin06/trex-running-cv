package com.emotibot.robotvision.game.trexrun.factory;

import android.content.res.Resources;

public class GameObjectFactory {


    public Dragon createNeutralDragon(Resources resources, int group) {
        return new PlayerDragon(resources, group);
    }

    public Obstacle createRandomObstacle(Resources resources, int group) {


        return new Obstacle(resources, group);
    }
}
