package naknikim.evilord.Towers;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Tal on 3/8/2017.
 */

public class TowerAnimationManager {
    private TowerAnimation[] animations;
    private int animationIndex = 0;


    public  TowerAnimationManager(TowerAnimation[] animations) {
        this.animations = animations;
    }

    public void playAnim(int index) {
        for(int i = 0 ; i < animations.length ; i++) {
            if(i == index) {
                if (!animations[index].isPlaying()) {
                    animations[i].play();
                }
            }
        }
        animationIndex = index;
    }

    public void draw(Canvas canvas , Rect rect) {
        if(animations[animationIndex].isPlaying())
            animations[animationIndex].draw(canvas, rect);
    }

    public void update() {
        if(animations[animationIndex].isPlaying())
            animations[animationIndex].update();
    }

}
