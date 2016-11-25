
package de.fhg.fokus.streetlife.mmecp.dataaggregator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by csc on 24.09.2015.
 */
public class UidGenerator {

    private final Random random = new Random();
    private Set<Integer> set = new HashSet<>(20);

    private static UidGenerator theGenerator = new UidGenerator();
//    public  static UidGenerator theGenerator() {
//        return UidGenerator.theGenerator;
//    }

    // don't call this more than 512 times!!! :)
    private Integer getaNewUid() {
        Integer snew;
        do {
            snew = this.random.nextInt(1048576);
        } while(set.contains(snew));

        set.add(snew);
        return snew;
    }

    public static Integer getNewUid() {
        return UidGenerator.theGenerator.getaNewUid();
    }
}
