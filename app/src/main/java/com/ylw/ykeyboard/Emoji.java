package com.ylw.ykeyboard;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Emoji extends KeyValue {
    public static int num_groups = 0;
    private static final HashMap<String, Emoji> emojis_by_name = new HashMap<String, Emoji>();
    private static Emoji[][] emojis_by_group = new Emoji[][]{};
    private final String _desc;

    protected Emoji(String name, String bytecode, String desc) {
        super(name, bytecode, CHAR_NONE, EVENT_NONE, 0);
        _desc = desc;
        emojis_by_name.put(name, this);
    }

    public static Emoji getEmojiByName(String name) {
        return emojis_by_name.get(name);
    }

    public static Emoji[] getEmojisByGroup(int group_id) {
        return (emojis_by_group[group_id]);
    }

    /* Read the list of emojis from a raw file. Will initialize only once. */
    public static void init(Resources res) {
        if (num_groups > 0)
            return;
        try {
            ArrayList<Emoji[]> groups = new ArrayList<Emoji[]>();
            InputStream inputStream = res.openRawResource(R.raw.emojis);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null)
                    break;
                int group_len = Integer.parseInt(line);
                Emoji[] grp = new Emoji[group_len];
                for (int i = 0; i < group_len; i++) {
                    line = reader.readLine();
                    String[] f = line.split(" ", 3);
                    grp[i] = new Emoji(f[0], f[1], f[2]);
                }
                groups.add(grp);
            }
            num_groups = groups.size();
            emojis_by_group = groups.toArray(new Emoji[0][]);
        } catch (IOException e) {
        }
    }

    public String getDescription() {
        return (_desc);
    }
}
