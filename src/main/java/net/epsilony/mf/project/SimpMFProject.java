/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.util.MFKey;
import net.epsilony.mf.util.MFKeys;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFProject implements MFProject {

    Map<MFKey, Object> datas = new HashMap<>();

    @Override
    public Map<MFKey, Object> getDatas() {
        return datas;
    }

    public Object put(MFKey key, Object value) {
        MFKeys.checkTypeAvailable(key, value);
        return datas.put(key, value);
    }

    @Override
    public Object get(MFKey key) {
        return datas.get(key);
    }

}
