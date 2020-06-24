package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.Sort;

import java.util.Iterator;

public class SortFactory {
    public static Sort get(Iterator<QuerySortOrder> i) {
        if(i.hasNext()) {
            QuerySortOrder order = i.next();
            return Sort.by(Sort.Direction.values()[order.getDirection().ordinal()], order.getSorted())
                    .and(get(i));
        } else
            return Sort.unsorted();
    }
}
