package com.ylw.ykeyboard;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import androidx.arch.core.util.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class KeyboardData {
    private static Row _bottomRow = null;
    private static final Map<Integer, KeyboardData> _layoutCache = new HashMap<Integer, KeyboardData>();
    public final List<Row> rows;
    /* Total width of the keyboard. Unit is abstract. */
    public final float keysWidth;
    /* Total height of the keyboard. Unit is abstract. */
    public final float keysHeight;

    protected KeyboardData(List<Row> rows_, float kw) {
        float kh = 0.f;
        for (Row r : rows_)
            kh += r.height + r.shift;
        rows = rows_;
        keysWidth = kw;
        keysHeight = kh;
    }

    public static KeyboardData load(Resources res, int id) {
        KeyboardData l = _layoutCache.get(id);
        if (l == null) {
            try {
                if (_bottomRow == null)
                    _bottomRow = parse_bottom_row(res.getXml(R.xml.bottom_row));
                l = parse_keyboard(res.getXml(id));
                _layoutCache.put(id, l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return l;
    }

    private static KeyboardData parse_keyboard(XmlResourceParser parser) throws Exception {
        if (!expect_tag(parser, "keyboard"))
            throw new Exception("Empty layout file");
        boolean bottom_row = parser.getAttributeBooleanValue(null, "bottom_row", true);
        ArrayList<Row> rows = new ArrayList<>();
        while (expect_tag(parser, "row"))
            rows.add(Row.parse(parser));
        float kw = compute_max_width(rows);
        if (bottom_row)
            rows.add(_bottomRow.updateWidth(kw));
        return new KeyboardData(rows, kw);
    }

    private static float compute_max_width(List<Row> rows) {
        float w = 0.f;
        for (Row r : rows)
            w = Math.max(w, r.keysWidth);
        return w;
    }

    private static Row parse_bottom_row(XmlResourceParser parser) throws Exception {
        if (!expect_tag(parser, "row"))
            throw new Exception("Failed to parse bottom row");
        return Row.parse(parser);
    }

    /**
     * Returns [false] on [END_DOCUMENT] or [END_TAG], [true] otherwise.
     */
    private static boolean expect_tag(XmlResourceParser parser, String name) throws Exception {
        int status;
        do {
            status = parser.next();
            if (status == XmlResourceParser.END_DOCUMENT || status == XmlResourceParser.END_TAG)
                return false;
        }
        while (status != XmlResourceParser.START_TAG);
        if (!parser.getName().equals(name))
            throw new Exception("Unknow tag: " + parser.getName());
        return true;
    }

    public KeyboardData replaceKeys(MapKeys f) {
        ArrayList<Row> rows_ = new ArrayList<Row>();
        for (Row r : rows)
            rows_.add(r.replaceKeys(f));
        return new KeyboardData(rows_, keysWidth);
    }

    public interface MapKeys extends Function<KeyValue, KeyValue> {
    }

    public static class Row {
        public final List<Key> keys;
        /* Height of the row, without 'shift'. Unit is abstract. */
        public final float height;
        /* Extra empty space on the top. */
        public final float shift;
        /* Total width of the row. Unit is abstract. */
        private final float keysWidth;

        protected Row(List<Key> keys_, float h, float s) {
            float kw = 0.f;
            for (Key k : keys_) kw += k.width + k.shift;
            keys = keys_;
            height = h;
            shift = s;
            keysWidth = kw;
        }

        public static Row parse(XmlResourceParser parser) throws Exception {
            ArrayList<Key> keys = new ArrayList<Key>();
            int status;
            float h = parser.getAttributeFloatValue(null, "height", 1f);
            float shift = parser.getAttributeFloatValue(null, "shift", 0f);
            while (expect_tag(parser, "key"))
                keys.add(Key.parse(parser));
            return new Row(keys, h, shift);
        }

        public Row replaceKeys(MapKeys f) {
            ArrayList<Key> keys_ = new ArrayList<Key>();
            for (Key k : keys)
                keys_.add(k.replaceKeys(f));
            return new Row(keys_, height, shift);
        }

        /**
         * Change the width of every keys so that the row is 's' units wide.
         */
        public Row updateWidth(float newWidth) {
            float s = newWidth / keysWidth;
            ArrayList<Key> keys_ = new ArrayList<Key>();
            for (Key k : keys)
                keys_.add(k.scaleWidth(s));
            return new Row(keys_, height, shift);
        }
    }

    /**
     * Parsing utils
     */

    public static class Key {
        /*
         ** 1   2
         **   0
         ** 3   4
         */
        public final KeyValue key0;
        public final KeyValue key1;
        public final KeyValue key2;
        public final KeyValue key3;
        public final KeyValue key4;

        /* Key width in relative unit. */
        public final float width;
        /* Extra empty space on the left of the key. */
        public final float shift;
        /* Put keys 1 to 4 on the edges instead of the corners. */
        public final boolean edgekeys;

        protected Key(KeyValue k0, KeyValue k1, KeyValue k2, KeyValue k3, KeyValue k4, float w, float s, boolean e) {
            key0 = k0;
            key1 = k1;
            key2 = k2;
            key3 = k3;
            key4 = k4;
            width = w;
            shift = s;
            edgekeys = e;
        }

        public static Key parse(XmlResourceParser parser) throws Exception {
            KeyValue k0 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key0"));
            KeyValue k1 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key1"));
            KeyValue k2 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key2"));
            KeyValue k3 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key3"));
            KeyValue k4 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key4"));
            float width = parser.getAttributeFloatValue(null, "width", 1f);
            float shift = parser.getAttributeFloatValue(null, "shift", 0.f);
            boolean edgekeys = parser.getAttributeBooleanValue(null, "edgekeys", false);
            while (parser.next() != XmlResourceParser.END_TAG) {
                continue;
            }
            return new Key(k0, k1, k2, k3, k4, width, shift, edgekeys);
        }

        public Key replaceKeys(MapKeys f) {
            return new Key(f.apply(key0), f.apply(key1), f.apply(key2),
                    f.apply(key3), f.apply(key4), width, shift, edgekeys);
        }

        /**
         * New key with the width multiplied by 's'.
         */
        public Key scaleWidth(float s) {
            return new Key(key0, key1, key2, key3, key4, width * s, shift, edgekeys);
        }

        public KeyValue getValue(int index) {
            switch (index) {
                case 1:
                    return key1;
                case 2:
                    return key2;
                case 3:
                    return key3;
                case 4:
                    return key4;
                default:
                case 0:
                    return key0;
            }
        }
    }
}
