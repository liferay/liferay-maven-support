/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.maven.plugins.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author Brian Wing Shun Chan
 */
public class ContextReplace implements Cloneable {

	public ContextReplace() {
		this(null);
	}

	public ContextReplace(Map<String, String> context) {
		if (context != null) {
			_context.putAll(context);

			_updateArrays();
		}
	}

	public void addValue(String key, String value) {
		if ((key != null) && (value != null)) {
			_context.put(key, value);

			_updateArrays();
		}
	}

	@Override
	public Object clone() {
		return new ContextReplace(_context);
	}

	public String replace(String text) {
		if (text == null) {
			return null;
		}

		if (_keys.length == 0) {
			return text;
		}

		return _replace(text, _keys, _values);
	}

	private String _replace(String s, String[] oldSubs, String[] newSubs) {
		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		for (int i = 0; i < oldSubs.length; i++) {
			s = StringUtils.replace(s, oldSubs[i], newSubs[i]);
		}

		return s;
	}

	private void _updateArrays() {
		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();

		for (Map.Entry<String, String> entry : _context.entrySet()) {
			String entryKey = entry.getKey();
			String entryValue = entry.getValue();

			keys.add("${" + entryKey + "}");
			values.add(entryValue);
		}

		_keys = keys.toArray(new String[keys.size()]);
		_values = values.toArray(new String[values.size()]);
	}

	private Map<String, String> _context = new LinkedHashMap<String, String>();
	private String[] _keys = new String[0];
	private String[] _values = new String[0];

}