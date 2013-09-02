/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.util.Constants;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFProcessorSettings {

    private MFProcessorSettings() {
    }

    public static Map<String, Object> defaultSettings() {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.KEY_ENABLE_MULTI_THREAD, Constants.DEFAULT_ENABLE_MULTITHREAD);
        return result;
    }
}
