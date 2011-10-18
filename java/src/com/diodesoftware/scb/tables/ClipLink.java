package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.*;

import java.util.*;

public class ClipLink
        implements DatabaseEntry {
    private int number;
    private int clipId;
    private int toClipId;
    private boolean blocked;
    private String uri;
    private Calendar created = Calendar.getInstance();

    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("ClipId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("ToClipId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("Blocked", DatabaseColumnType.BOOLEAN),
            new DatabaseColumn("Created", DatabaseColumnType.DATE12),
            new DatabaseColumn("Uri", DatabaseColumnType.BLOB)
    };

    public DatabaseColumn[] columns() {
        return columns;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int i) {
        number = i;
    }

    public int getClipId() {
        return clipId;
    }

    public void setClipId(int i) {
        clipId = i;
    }

    public int getToClipId() {
        return toClipId;
    }

    public void setToClipId(int i) {
        toClipId = i;
    }

    public boolean getBlocked() {
        return isBlocked();
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean b) {
        blocked = b;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar c) {
        created = c;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String toLink(){
        String s = "<a href='" + uri + "'>" + uri + "</a>";
        return s;
    }
}
