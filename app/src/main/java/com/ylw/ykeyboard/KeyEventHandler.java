package com.ylw.ykeyboard;

import android.view.KeyEvent;

class KeyEventHandler implements Config.IKeyEventHandler {
    private final IReceiver _recv;

    public KeyEventHandler(IReceiver recv) {
        _recv = recv;
    }

    public void handleKeyUp(KeyValue key, int flags) {
        if (key == null || (key.flags & KeyValue.FLAG_NOCHAR) != 0)
            return;
        switch (key.eventCode) {
            case KeyValue.EVENT_CONFIG:
                _recv.showKeyboardConfig();
                return;
            case KeyValue.EVENT_SWITCH_TEXT:
                _recv.switchMain();
                return;
            case KeyValue.EVENT_SWITCH_NUMERIC:
                _recv.switchNumeric();
                return;
            case KeyValue.EVENT_SWITCH_EMOJI:
                _recv.setPane_emoji();
                return;
            case KeyValue.EVENT_SWITCH_BACK_EMOJI:
                _recv.setPane_normal();
                return;
            case KeyValue.EVENT_CHANGE_METHOD:
                _recv.switchToNextInputMethod();
                return;
            case KeyValue.EVENT_ACTION:
                _recv.performAction();
                return;
            case KeyValue.EVENT_SWITCH_PROGRAMMING:
                _recv.switchProgramming();
                return;
            default:
                if ((flags & (KeyValue.FLAG_CTRL | KeyValue.FLAG_ALT | KeyValue.FLAG_META)) != 0)
                    handleKeyUpWithModifier(key, flags);
                else if (key.char_ != KeyValue.CHAR_NONE)
                    _recv.commitChar(key.char_);
                else if (key.eventCode != KeyValue.EVENT_NONE)
                    handleKeyUpWithModifier(key, flags);
                else
                    _recv.commitText(key.symbol);
        }
    }

    // private void handleDelKey(int before, int after)
    // {
    //  CharSequence selection = getCurrentInputConnection().getSelectedText(0);

    //  if (selection != null && selection.length() > 0)
    //  getCurrentInputConnection().commitText("", 1);
    //  else
    //  getCurrentInputConnection().deleteSurroundingText(before, after);
    // }

    private int sendMetaKey(int eventCode, int metaFlags, int metaState, boolean down) {
        int action;
        int updatedMetaState;
        if (down) {
            action = KeyEvent.ACTION_DOWN;
            updatedMetaState = metaState | metaFlags;
        } else {
            action = KeyEvent.ACTION_UP;
            updatedMetaState = metaState & ~metaFlags;
        }
        _recv.sendKeyEvent(action, eventCode, metaState);
        return updatedMetaState;
    }

    /* Send key events corresponding to pressed modifier keys. */
    private int sendMetaKeys(int flags, int metaState, boolean down) {
        if ((flags & KeyValue.FLAG_CTRL) != 0)
            metaState = sendMetaKey(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.META_CTRL_LEFT_ON | KeyEvent.META_CTRL_ON, metaState, down);
        if ((flags & KeyValue.FLAG_ALT) != 0)
            metaState = sendMetaKey(KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.META_ALT_LEFT_ON | KeyEvent.META_ALT_ON, metaState, down);
        if ((flags & KeyValue.FLAG_SHIFT) != 0)
            metaState = sendMetaKey(KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.META_SHIFT_LEFT_ON | KeyEvent.META_SHIFT_ON, metaState, down);
        if ((flags & KeyValue.FLAG_META) != 0)
            metaState = sendMetaKey(KeyEvent.KEYCODE_META_LEFT, KeyEvent.META_META_LEFT_ON | KeyEvent.META_META_ON, metaState, down);
        return metaState;
    }

    /*
     * Don't set KeyEvent.FLAG_SOFT_KEYBOARD.
     */
    private void handleKeyUpWithModifier(KeyValue key, int flags) {
        if (key.eventCode == KeyValue.EVENT_NONE)
            return;
        int metaState = sendMetaKeys(flags, 0, true);
        _recv.sendKeyEvent(KeyEvent.ACTION_DOWN, key.eventCode, metaState);
        _recv.sendKeyEvent(KeyEvent.ACTION_UP, key.eventCode, metaState);
        sendMetaKeys(flags, metaState, false);
    }

    public interface IReceiver {
        void switchToNextInputMethod();

        void setPane_emoji();

        void setPane_normal();

        void showKeyboardConfig();

        void performAction();

        void switchMain();

        void switchNumeric();

        void switchProgramming();

        void sendKeyEvent(int eventAction, int eventCode, int meta);

        void commitText(String text);

        void commitChar(char c);
    }
}
