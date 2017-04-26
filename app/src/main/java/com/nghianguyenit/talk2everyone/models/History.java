package com.nghianguyenit.talk2everyone.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class History {

    public String uid;
    public String author;
    public String source;
    public String target;

    public History() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public History(String uid, String author, String source, String target) {
        this.uid = uid;
        this.author = author;
        this.source = source;
        this.target = target;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("source", source);
        result.put("target", target);

        return result;
    }
    // [END post_to_map]
}