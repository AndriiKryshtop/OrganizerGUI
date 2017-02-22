package ua.sumdu.j2se.kryshtop.tasks.util;

import ua.sumdu.j2se.kryshtop.tasks.model.ArrayTaskList;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;

import java.util.*;

public class Tasks {
    /**
     * To find tasks that is going to be executed at least one time in established time distance
     *
     * @param tasks collection of the tasks
     * @param start start of a time distance
     * @param end   end of a time distance
     * @return subset of tasks that are planed for performance in established time distance at least one time
     */
    @SuppressWarnings("unused")
    public static Iterable<Task> incoming(Iterable<Task> tasks, Date start, Date end) {
        ArrayTaskList bufferArray = new ArrayTaskList();

        for (Task task : tasks) {
            if (task.nextTimeAfter(start).compareTo(end) != 1) {
                bufferArray.add(task);
            }
        }

        return bufferArray;
    }

    public static SortedMap<Date, Set<Task>> calendar(Iterable<Task> tasks, Date start, Date end) {
        SortedMap<Date, Set<Task>> sortedMap = new TreeMap<>();

        for (Task task : tasks) {
            Date bufferDate = start;

            if (task.nextTimeAfter(bufferDate) == null) {
                continue;
            }

            while (task.nextTimeAfter(bufferDate).compareTo(end) != 1) {
                if (!sortedMap.containsKey(task.nextTimeAfter(bufferDate))) {
                    Set<Task> set = new HashSet<>();
                    set.add(task);
                    sortedMap.put(task.nextTimeAfter(bufferDate), set);
                } else {
                    sortedMap.get(task.nextTimeAfter(bufferDate)).add(task);
                }

                //in order not to get an NullPointerException in nextTimeAfter()
                if (task.nextTimeAfter(bufferDate) == null) {
                    break;
                }
                if (task.nextTimeAfter(task.nextTimeAfter(bufferDate)) == null) {
                    break;
                }

                bufferDate = task.nextTimeAfter(bufferDate);
            }
        }

        return sortedMap;
    }
}

