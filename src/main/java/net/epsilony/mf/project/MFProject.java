/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import java.util.Map;
import net.epsilony.mf.util.MFKey;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFProject {

    Map<MFKey, Object> getDatas();

    Object get(MFKey key);
}
