package net.epsilony.mf.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

public class MFUtilsTest {

    int dataNum = 10;
    int mapSize = 20;

    /**
     * @author Man YUAN <epsilon@epsilony.net>
     * 
     */
    public static class SampleDataComparator implements Comparator<SampleData> {
        @Override
        public int compare(SampleData o1, SampleData o2) {
            return o1.getId() - o2.getId();
        }
    }

    public static class SampleData implements Serializable {
        public static int maxId = 1;
        int               id;

        public SampleData() {
            id = maxId++;
        }

        public int getId() {
            return id;
        }
    }

    @Test
    public void testCloneMapWithSameKeys() {
        Map<Integer, SampleData> toBeClonedMap = genSampleMap();

        SampleDataComparator comparator = new SampleDataComparator();
        Map<Integer, SampleData> clonedMap = MFUtils.cloneMapWithSameKeys(toBeClonedMap);

        for (Entry<Integer, SampleData> entry : clonedMap.entrySet()) {
            SampleData oriValue = toBeClonedMap.get(entry.getKey());
            Assert.assertEquals(0, comparator.compare(oriValue, entry.getValue()));
        }

        Assert.assertTrue(toBeClonedMap.keySet().equals(clonedMap.keySet()));

        TreeSet<SampleData> toBeClonedValueSet = new TreeSet<>(comparator);
        toBeClonedValueSet.addAll(toBeClonedMap.values());
        TreeSet<SampleData> clonedSet = new TreeSet<>(comparator);
        clonedSet.addAll(clonedMap.values());

        Assert.assertTrue(toBeClonedValueSet.equals(clonedSet));
    }

    private Map<Integer, SampleData> genSampleMap() {
        ArrayList<SampleData> datas = new ArrayList<>(dataNum);
        for (int i = 0; i < dataNum; i++) {
            datas.add(new SampleData());
        }

        Random rand = new Random();

        Map<Integer, SampleData> sampleMap = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            sampleMap.put(rand.nextInt(), datas.get(rand.nextInt(dataNum)));
        }

        return sampleMap;
    }

    @Test
    public void testLockablyWrapValues() {
        Map<Integer, SampleData> toBeWrappedMap = genSampleMap();
        Map<Integer, LockableHolder<SampleData>> result = MFUtils.lockablyWrapValues(toBeWrappedMap);
        Assert.assertEquals(toBeWrappedMap.size(), result.size());
        for (Entry<Integer, SampleData> entry : toBeWrappedMap.entrySet()) {
            SampleData exp = entry.getValue();
            SampleData act = result.get(entry.getKey()).getData();
            Assert.assertEquals(exp, act);
        }
    }

}
