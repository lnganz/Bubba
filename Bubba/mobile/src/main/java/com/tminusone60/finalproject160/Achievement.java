package com.tminusone60.finalproject160;

/**
 * Created by lennon on 4/28/16.
 */
public class Achievement {

    public String name;
    public String flavorText;
    public int imageResourceId;

    public Achievement(String name, int imageResourceId, String flavorText) {
        this.name = name;
        this.imageResourceId = imageResourceId;
        this.flavorText = flavorText;
    }


    public static final Achievement TROPHY = new Achievement("Trophy", R.drawable.achievement_trophy, "You opened the app");
    public static final Achievement GIFT = new Achievement("Gift", R.drawable.achievement_gift, "You have successfully earned the title of \"Gifted\" by feeding Bubba a total of 2 times.");
    public static final Achievement PLUS1 = new Achievement("Plus1", R.drawable.achievement_plus1, "You have successfully earned the achievement of \"Anotha one\" by feeding Bubba a total of 5 times.");
    public static final Achievement CHECK = new Achievement("Check", R.drawable.achievement_check, "You reached level 3. Keep up the good work!");
    public static final Achievement STAR = new Achievement("Star", R.drawable.achievement_star, "You have successfully earned the title of \"Star Performer\" by feeding Bubba a total of 8 times.");
    public static final Achievement LOCK = new Achievement("Lock", R.drawable.achievement_lock, "You have not unlocked this achievement yet");

    public static int getImageResourceId(String name) {
        if (name.equals(TROPHY.name)) {
            return TROPHY.imageResourceId;
        } else if (name.equals(GIFT.name)) {
            return GIFT.imageResourceId;
        } else if (name.equals(PLUS1.name)) {
            return PLUS1.imageResourceId;
        } else if (name.equals(CHECK.name)) {
            return CHECK.imageResourceId;
        } else if (name.equals(STAR.name)) {
            return STAR.imageResourceId;
        } else if (name.equals(LOCK.name)) {
            return LOCK.imageResourceId;
        } else {
            return LOCK.imageResourceId;
        }
    }
}