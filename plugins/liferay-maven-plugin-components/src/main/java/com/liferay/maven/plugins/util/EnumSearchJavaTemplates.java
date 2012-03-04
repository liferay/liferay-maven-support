/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

/**
 * @author kamesh.sampath
 */
public enum EnumSearchJavaTemplates
{

	SEARCH_DISPLAY_TERMS
	{

		/*
		 * (non-Javadoc)
		 * @see com.liferay.maven.plugins.util.EnumSearchJspTemplates#prefix()
		 */
		@Override
		public String prefix()
		{
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see com.liferay.maven.plugins.util.EnumSearchJspTemplates#suffix()
		 */
		@Override
		public String suffix()
		{
			return "DisplayTerms";
		}

		@Override
		public String templateKey()
		{
			return "DisplayTerms_java";
		}

	},

	SEARCH_TERMS
	{

		/*
		 * (non-Javadoc)
		 * @see com.liferay.maven.plugins.util.EnumSearchJspTemplates#prefix()
		 */
		@Override
		public String prefix()
		{
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see com.liferay.maven.plugins.util.EnumSearchJspTemplates#suffix()
		 */
		@Override
		public String suffix()
		{
			return "SearchTerms";
		}

		@Override
		public String templateKey()
		{
			return "SearchTerms_java";
		}

	},

	SEARCH
	{

		/*
		 * (non-Javadoc)
		 * @see com.liferay.maven.plugins.util.EnumSearchJspTemplates#prefix()
		 */
		@Override
		public String prefix()
		{
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see com.liferay.maven.plugins.util.EnumSearchJspTemplates#suffix()
		 */
		@Override
		public String suffix()
		{
			return "Search";
		}

		@Override
		public String templateKey()
		{
			return "Search_java";
		}
	};

	public abstract String templateKey();

	public abstract String prefix();

	public abstract String suffix();
}
