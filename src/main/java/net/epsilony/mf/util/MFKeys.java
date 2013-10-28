/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFKeys {

    private MFKeys() {

    }

    public static boolean isTypeAvailable(MFKey key, Object value) {
        if (null == value) {
            return true;
        }
        return key.getValueType().isInstance(value);
    }

    public static void checkTypeAvailable(MFKey key, Object value) {
        if (isTypeAvailable(key, value)) {
            return;
        }
        throw new IllegalArgumentException(value + " is not an available type of key " + key);
    }
}
