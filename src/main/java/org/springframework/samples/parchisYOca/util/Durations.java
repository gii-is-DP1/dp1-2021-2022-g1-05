package org.springframework.samples.parchisYOca.util;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class Durations {

    public static String listDurationsToString(Collection<Long> durations){
        Long totalDuration = 0L;
        Long average = 0L;
        for(Long l : durations){
            totalDuration = totalDuration + l;
        }

        if(durations.size() != 0){
            average = totalDuration/durations.size();
        }

        String averageStr = String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(average),
            TimeUnit.MILLISECONDS.toMinutes(average) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(average)),
            TimeUnit.MILLISECONDS.toSeconds(average) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(average)));

        return averageStr;
    }
}
