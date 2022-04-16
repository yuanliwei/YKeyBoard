package com.ylw.ykeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EmojiGridView extends GridView
        implements GridView.OnItemClickListener {
    public static final int GROUP_LAST_USE = -1;

    public static final int COLUMN_WIDTH = 192;

    private static final String LAST_USE_PREF = "emoji_last_use";

    private Emoji[] _emojiArray;
    private HashMap<Emoji, Integer> _lastUsed;

    /*
     ** TODO: adapt column width and emoji size
     ** TODO: use ArraySet instead of Emoji[]
     */
    public EmojiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Emoji.init(context.getResources());
        setOnItemClickListener(this);
        setColumnWidth(COLUMN_WIDTH);
        loadLastUsed();
        setEmojiGroup((_lastUsed.size() == 0) ? 0 : GROUP_LAST_USE);
    }

    public void setEmojiGroup(int group) {
        _emojiArray = (group == GROUP_LAST_USE) ? getLastEmojis() : Emoji.getEmojisByGroup(group);
        setAdapter(new EmojiViewAdpater(getContext(), _emojiArray));
    }

    public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
        Config config = Config.globalConfig();
        Integer used = _lastUsed.get(_emojiArray[pos]);
        _lastUsed.put(_emojiArray[pos], (used == null) ? 1 : used.intValue() + 1);
        config.handler.handleKeyUp(_emojiArray[pos], 0);
        saveLastUsed(); // TODO: opti
    }

    @Override
    public void onMeasure(int wSpec, int hSpec) {
        super.onMeasure(wSpec, hSpec);
        setNumColumns(getMeasuredWidth() / COLUMN_WIDTH);
    }

    private Emoji[] getLastEmojis() {
        final HashMap<Emoji, Integer> map = _lastUsed;
        Emoji[] array = new Emoji[map.size()];

        map.keySet().toArray(array);
        Arrays.sort(array, 0, array.length, new Comparator<Emoji>() {
            public int compare(Emoji a, Emoji b) {
                return (map.get(b).intValue() - map.get(a).intValue());
            }
        });
        return (array);
    }

    private void saveLastUsed() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        HashSet<String> set = new HashSet<String>();

        for (Emoji emoji : _lastUsed.keySet())
            set.add(_lastUsed.get(emoji) + "-" + emoji.name);
        edit.putStringSet(LAST_USE_PREF, set);
        edit.apply();
    }

    private void loadLastUsed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> lastUseSet = prefs.getStringSet(LAST_USE_PREF, null);

        _lastUsed = new HashMap<Emoji, Integer>();
        if (lastUseSet != null)
            for (String emojiData : lastUseSet) {
                String[] data = emojiData.split("-", 2);
                Emoji emoji;

                if (data.length != 2)
                    continue;
                emoji = Emoji.getEmojiByName(data[1]);
                if (emoji == null)
                    continue;
                _lastUsed.put(emoji, Integer.valueOf(data[0]));
            }
    }

    private static class EmojiView extends TextView {
        public EmojiView(Context context) {
            super(context);
            setTextAppearance(context, R.style.emojiGridButton);
            setGravity(Gravity.CENTER);
            setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
        }

        public void setEmoji(Emoji emoji) {
            setText(emoji.symbol);
        }
    }

    private static class EmojiViewAdpater extends BaseAdapter {
        private final Context _context;

        private final Emoji[] _emojiArray;

        public EmojiViewAdpater(Context context, Emoji[] emojiArray) {
            _context = context;
            _emojiArray = emojiArray;
        }

        public int getCount() {
            if (_emojiArray == null)
                return (0);
            return (_emojiArray.length);
        }

        public Object getItem(int pos) {
            return (_emojiArray[pos]);
        }

        public long getItemId(int pos) {
            return (pos);
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            EmojiView view = (EmojiView) convertView;

            if (view == null)
                view = new EmojiView(_context);
            view.setEmoji(_emojiArray[pos]);
            return (view);
        }
    }
}
