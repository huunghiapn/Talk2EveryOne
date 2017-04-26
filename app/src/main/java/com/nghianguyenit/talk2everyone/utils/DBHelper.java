package com.nghianguyenit.talk2everyone.utils;

import com.google.firebase.database.DatabaseReference;
import com.nghianguyenit.talk2everyone.models.History;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NghiaNH on 4/24/2017.
 */

public class DBHelper {

    public static void writeHistory(DatabaseReference mDatabase, String userId, String username, String sourceText, String targetText){
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("historys").push().getKey();
        History history = new History(userId, username, sourceText, targetText);
        Map<String, Object> postValues = history.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/historys/" + key, postValues);
        childUpdates.put("/user-historys/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
