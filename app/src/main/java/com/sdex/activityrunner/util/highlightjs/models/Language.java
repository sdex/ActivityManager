package com.sdex.activityrunner.util.highlightjs.models;

/**
 * This Class was created by Patrick J
 * on 09.06.16. For more Details and Licensing
 * have a look at the README.md
 */

public enum Language {

    XML("xml");

    private final String className;

    Language(String name) {
        this.className = name;
    }

    public String getName() {
        return className;
    }

}
