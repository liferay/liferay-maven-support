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
public enum EnumSearchJspTemplates
{

	JSP_INIT_SEARCH
	{

		@Override
		public String templateKey()
		{
			return "search_init_jsp";
		}

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
			// TODO Auto-generated method stub
			return null;
		}

	},

	JSP_SEARCH
	{

		@Override
		public String templateKey()
		{
			return "search_jsp";
		}

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
			// TODO Auto-generated method stub
			return null;
		}

	},

	JSP_SEARCH_FORM
	{

		@Override
		public String templateKey()
		{
			return "search_form_jsp";
		}

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
			// TODO Auto-generated method stub
			return null;
		}

	},
	JSP_SEARCH_RESULTS
	{

		@Override
		public String templateKey()
		{
			return "search_results_jsp";
		}

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
			// TODO Auto-generated method stub
			return null;
		}

	},
	JSP_ROW
	{

		@Override
		public String templateKey()
		{
			return "search_rows_jsp";
		}

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
			// TODO Auto-generated method stub
			return null;
		}

	},
	JSP_ROW_ACTIONS
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String templateKey()
		{
			return "search_row_actions_jsp";
		}

	};

	public abstract String templateKey();

	public abstract String prefix();

	public abstract String suffix();
}
