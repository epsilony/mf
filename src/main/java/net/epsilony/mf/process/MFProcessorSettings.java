/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.util.MFConstants;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFProcessorSettings {

    private MFProcessorSettings() {
    }

    public static Map<String, Object> defaultSettings() {
        Map<String, Object> result = new HashMap<>();
        result.put(MFConstants.KEY_ENABLE_MULTI_THREAD, MFConstants.DEFAULT_ENABLE_MULTITHREAD);
        result.put(MFConstants.KEY_FORCIBLE_THREAD_NUMBER, MFConstants.DEFAULT_FORCIBLE_THREAD_NUMBER);
        return result;
    }
}
