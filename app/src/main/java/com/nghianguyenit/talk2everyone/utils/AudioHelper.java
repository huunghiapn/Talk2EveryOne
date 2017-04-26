package com.nghianguyenit.talk2everyone.utils;

import android.media.MediaPlayer;

/**
 * Created by NghiaNH on 4/25/2017.
 */

public class AudioHelper {

    public static MediaPlayer mediaPlayer;

    public static void playAudio(String url) throws Exception
    {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    public static void killMediaPlayer() {
        if(mediaPlayer!=null) {
            try {
                mediaPlayer.release();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
