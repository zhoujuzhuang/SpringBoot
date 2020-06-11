package com.kimleysoft.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

public class RoutingDataSource extends AbstractRoutingDataSource{
	
	@Nullable
    @Override
    protected Object determineCurrentLookupKey(){
        return DBContextHolder.get();
    }
}
