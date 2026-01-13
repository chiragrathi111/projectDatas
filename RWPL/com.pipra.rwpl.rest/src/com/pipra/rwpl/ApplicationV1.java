package com.pipra.rwpl;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.pipra.rwpl.auth.impl.AuthServiceImpl;
import com.pipra.rwpl.filter.RequestFilter;
import com.pipra.rwpl.filter.ResponseFilter;
import com.pipra.rwpl.service.impl.RwplServiceImpl;

public class ApplicationV1 extends Application {

	public ApplicationV1() {
	}

	@Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        
        classes.add(AuthServiceImpl.class);
        classes.add(RequestFilter.class);
        classes.add(ResponseFilter.class);
        classes.add(JacksonFeature.class);
        classes.add(MultiPartFeature.class);
        classes.add(RwplServiceImpl.class);
        
        return classes;
    }	
}

