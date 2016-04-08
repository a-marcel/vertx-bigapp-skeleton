package com.weeaar.vertx.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RoutesParser2 implements Iterator< Route >, Iterable< Route > {
	List< Route > routes;

	private int index;

	public RoutesParser2(JsonArray routeJson) {
		routes = new ArrayList< Route >();

		for (Object route : routeJson) {

			if (route instanceof JsonObject) {
				routes.add(new Route((JsonObject) route));
			}
		}
		index = 0;
	}

	@Override
	public boolean hasNext() {
		return !(routes.size() == index);
	}

	@Override
	public Route next() {
		if (hasNext()) {
			return routes.get(index++);
		} else {
			throw new NoSuchElementException("There are no elements size = " + routes.size());
		}
	}

	@Override
	public Iterator< Route > iterator() {
		return routes.iterator();
	}
}
