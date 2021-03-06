package com.ylw.ykeyboard;

import android.util.SparseArray;
import android.view.KeyCharacterMap;

import java.util.HashMap;

class KeyModifier {
    /**
     * Represents a removed key, because a cache entry can't be [null].
     */
    private static final KeyValue removed_key = KeyValue.getKeyByName("removed");
    /**
     * Cache key is KeyValue's name
     */
    private static final HashMap<String, SparseArray<KeyValue>> _cache =
            new HashMap<String, SparseArray<KeyValue>>();

    /**
     * Modify a key according to modifiers.
     */
    public static KeyValue handleFlags(KeyValue k, int flags) {
        if (k == null)
            return null;
        SparseArray<KeyValue> ks = cacheEntry(k);
        KeyValue r = ks.get(flags);
        if (r == null) // Cold cache
        {
            r = k;
            r = handleFn(r, flags);
            r = handleShift(r, flags);
            r = handleAccents(r, flags);
            ks.put(flags, r);
        }
        return (r == removed_key) ? null : r;
    }

    private static KeyValue handleAccents(KeyValue k, int flags) {
        if (k.char_ == KeyValue.CHAR_NONE || (flags & KeyValue.FLAGS_ACCENTS) == 0)
            return k;
        char c = handleAccentChar(k.char_, flags);
        if (c == 0 || c == k.char_)
            return k;
        return k.withCharAndSymbol(c);
    }

    private static KeyValue handleShift(KeyValue k, int flags) {
        if ((flags & KeyValue.FLAG_SHIFT) == 0)
            return k;
        char c = k.char_;
        if (k.char_ != KeyValue.CHAR_NONE)
            c = Character.toUpperCase(c);
        if (c == k.char_) // Used to have more rules if toUpperCase() did nothing
            return k;
        return k.withCharAndSymbol(c);
    }

    private static char handleAccentChar(char c, int flags) {
        switch ((flags & KeyValue.FLAGS_ACCENTS)) {
            case KeyValue.FLAG_ACCENT1:
                return (char) KeyCharacterMap.getDeadChar('\u02CB', c);
            case KeyValue.FLAG_ACCENT2:
                return (char) KeyCharacterMap.getDeadChar('\u00B4', c);
            case KeyValue.FLAG_ACCENT3:
                return (char) KeyCharacterMap.getDeadChar('\u02C6', c);
            case KeyValue.FLAG_ACCENT4:
                return (char) KeyCharacterMap.getDeadChar('\u02DC', c);
            case KeyValue.FLAG_ACCENT5:
                return (char) KeyCharacterMap.getDeadChar('\u00B8', c);
            case KeyValue.FLAG_ACCENT6:
                return (char) KeyCharacterMap.getDeadChar('\u00A8', c);
            case KeyValue.FLAG_ACCENT_CARON:
                return (char) KeyCharacterMap.getDeadChar('\u02C7', c);
            case KeyValue.FLAG_ACCENT_RING:
                return (char) KeyCharacterMap.getDeadChar('\u02DA', c);
            case KeyValue.FLAG_ACCENT_MACRON:
                return (char) KeyCharacterMap.getDeadChar('\u00AF', c);
            case KeyValue.FLAG_ACCENT_ORDINAL:
                switch (c) {
                    case 'a':
                        return '??';
                    case 'o':
                        return '??';
                    case '1':
                        return '??';
                    case '2':
                        return '??';
                    case '3':
                        return '???';
                    case '4':
                        return '???';
                    case '5':
                        return '???';
                    case '6':
                        return '??';
                    case '7':
                        return '??';
                    case '8':
                        return '???';
                    case '9':
                        return '??';
                    case '*':
                        return '??';
                    default:
                        return c;
                }
            case KeyValue.FLAG_ACCENT_SUPERSCRIPT:
                switch (c) {
                    case '1':
                        return '??';
                    case '2':
                        return '??';
                    case '3':
                        return '??';
                    case '4':
                        return '???';
                    case '5':
                        return '???';
                    case '6':
                        return '???';
                    case '7':
                        return '???';
                    case '8':
                        return '???';
                    case '9':
                        return '???';
                    case '0':
                        return '???';
                    case 'i':
                        return '???';
                    case '+':
                        return '???';
                    case '-':
                        return '???';
                    case '=':
                        return '???';
                    case '(':
                        return '???';
                    case ')':
                        return '???';
                    case 'n':
                        return '???';
                    default:
                        return c;
                }
            case KeyValue.FLAG_ACCENT_SUBSCRIPT:
                switch (c) {
                    case '1':
                        return '???';
                    case '2':
                        return '???';
                    case '3':
                        return '???';
                    case '4':
                        return '???';
                    case '5':
                        return '???';
                    case '6':
                        return '???';
                    case '7':
                        return '???';
                    case '8':
                        return '???';
                    case '9':
                        return '???';
                    case '0':
                        return '???';
                    case '+':
                        return '???';
                    case '-':
                        return '???';
                    case '=':
                        return '???';
                    case '(':
                        return '???';
                    case ')':
                        return '???';
                    case 'e':
                        return '???';
                    case 'a':
                        return '???';
                    case 'x':
                        return '???';
                    case 'o':
                        return '???';
                    default:
                        return c;
                }
            case KeyValue.FLAG_ACCENT_ARROWS:
                if ((flags & KeyValue.FLAG_SHIFT) == 0) {
                    switch (c) {
                        case '1':
                            return '???';
                        case '2':
                            return '???';
                        case '3':
                            return '???';
                        case '4':
                            return '???';
                        case '6':
                            return '???';
                        case '7':
                            return '???';
                        case '8':
                            return '???';
                        case '9':
                            return '???';
                        default:
                            return c;
                    }
                } else {
                    switch (c) {
                        case '1':
                            return '???';
                        case '2':
                            return '???';
                        case '3':
                            return '???';
                        case '4':
                            return '???';
                        case '6':
                            return '???';
                        case '7':
                            return '???';
                        case '8':
                            return '???';
                        case '9':
                            return '???';
                        default:
                            return c;
                    }
                }
            case KeyValue.FLAG_ACCENT_BOX:
                if ((flags & KeyValue.FLAG_SHIFT) == 0) {
                    switch (c) {
                        case '1':
                            return '???';
                        case '2':
                            return '???';
                        case '3':
                            return '???';
                        case '4':
                            return '???';
                        case '5':
                            return '???';
                        case '6':
                            return '???';
                        case '7':
                            return '???';
                        case '8':
                            return '???';
                        case '9':
                            return '???';
                        case '0':
                            return '???';
                        case '.':
                            return '???';
                        default:
                            return c;
                    }
                } else {
                    switch (c) {
                        case '1':
                            return '???';
                        case '2':
                            return '???';
                        case '3':
                            return '???';
                        case '4':
                            return '???';
                        case '5':
                            return '???';
                        case '6':
                            return '???';
                        case '7':
                            return '???';
                        case '8':
                            return '???';
                        case '9':
                            return '???';
                        case '0':
                            return '???';
                        case '.':
                            return '???';
                        default:
                            return c;
                    }
                }
            default:
                return c; // Can't happen
        }
    }

    private static KeyValue handleFn(KeyValue k, int flags) {
        if ((flags & KeyValue.FLAG_FN) == 0) {
            switch (k.name) {
                // Remove some keys when Fn is *not* activated
                case "f11_placeholder":
                case "f12_placeholder":
                    return removed_key;
                default:
                    return k;
            }
        }
        String name;
        switch (k.name) {
            case "1":
                name = "f1";
                break;
            case "2":
                name = "f2";
                break;
            case "3":
                name = "f3";
                break;
            case "4":
                name = "f4";
                break;
            case "5":
                name = "f5";
                break;
            case "6":
                name = "f6";
                break;
            case "7":
                name = "f7";
                break;
            case "8":
                name = "f8";
                break;
            case "9":
                name = "f9";
                break;
            case "0":
                name = "f10";
                break;
            case "f11_placeholder":
                name = "f11";
                break;
            case "f12_placeholder":
                name = "f12";
                break;
            case "up":
                name = "page_up";
                break;
            case "down":
                name = "page_down";
                break;
            case "left":
                name = "home";
                break;
            case "right":
                name = "end";
                break;
            case "<":
                name = "??";
                break;
            case ">":
                name = "??";
                break;
            case "{":
                name = "???";
                break;
            case "}":
                name = "???";
                break;
            case "[":
                name = "???";
                break;
            case "]":
                name = "???";
                break;
            case "(":
                name = "???";
                break;
            case ")":
                name = "???";
                break;
            case "'":
                name = "???";
                break;
            case "\"":
                name = "???";
                break;
            case "-":
                name = "???";
                break;
            case "_":
                name = "???";
                break;
            case "^":
                name = "??";
                break;
            case "%":
                name = "???";
                break;
            case "=":
                name = "???";
                break;
            case "u":
                name = "??";
                break;
            case "a":
                name = "??";
                break;
            case "o":
                name = "??";
                break;
            case "esc":
                name = "insert";
                break;
            case "*":
                name = "??";
                break;
            case ".":
                name = "???";
                break;
            case ",":
                name = "??";
                break;
            case "!":
                name = "??";
                break;
            case "?":
                name = "??";
                break;
            case "tab":
                name = "\\t";
                break;
            case "space":
                name = "nbsp";
                break;
            case "???":
                name = "???";
                break;
            case "???":
                name = "???";
                break;
            case "???":
                name = "???";
                break;
            case "???":
                name = "???";
                break;
            case "???":
                name = "???";
                break;
            case "???":
                name = "???";
                break;
            case "???":
                name = "???";
                break;
            case "???":
                name = "???";
                break;
            // Currency symbols
            case "e":
                name = "???";
                break;
            case "l":
                name = "??";
                break;
            case "r":
                name = "???";
                break;
            case "y":
                name = "??";
                break;
            case "c":
                name = "??";
                break;
            case "p":
                name = "???";
                break;
            case "???":
            case "??":
                return removed_key; // Avoid showing these twice
            default:
                return k;
        }
        return KeyValue.getKeyByName(name);
    }

    /* Lookup the cache entry for a key. Create it needed. */
    private static SparseArray<KeyValue> cacheEntry(KeyValue k) {
        SparseArray<KeyValue> ks = _cache.get(k.name);
        if (ks == null) {
            ks = new SparseArray<KeyValue>();
            _cache.put(k.name, ks);
        }
        return ks;
    }
}
