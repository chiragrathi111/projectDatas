package com.pipra.ve.rest.main;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.jackson.JacksonFeature;

import com.pipra.ve.rest.auth.impl.AuthServiceImpl;
import com.pipra.ve.rest.filter.RequestFilter;
import com.pipra.ve.rest.filter.ResponseFilter;
import com.pipra.ve.rest.service.VeServiceImpl;


/**
 * @author Mahendhar Reddy
 *
 */
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
        classes.add(VeServiceImpl.class);
        return classes;
    }	
}

