package edu.grinnell.sandb.Model;

import io.realm.RealmObject;

/**
 * Created by albertowusu-asare on 5/1/16.
 */
public class RealmArticleAuthor extends RealmObject {
    private String name;
    public RealmArticleAuthor() {
    }

    public RealmArticleAuthor(String name){
        this.name = name;
    }
    /**
     * @return the name of the Author
     */
    public String getName(){
        return this.name;
    }
    @Override
    public String toString(){
        return getName();
    }

}

