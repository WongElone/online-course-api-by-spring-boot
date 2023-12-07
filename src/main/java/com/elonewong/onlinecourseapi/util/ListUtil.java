package com.elonewong.onlinecourseapi.util;

import java.util.HashSet;
import java.util.List;

public class ListUtil {

    public static <T> boolean containsDuplicatedElem(List<T> list) {
        HashSet<T> hs = new HashSet<>();
        for (T t : list) {
            if (hs.contains(t)) return true;
            hs.add(t);
        }
        return false;
    }

}
