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

package com.liferay.maven.plugins.model;

/**
 * @author kamesh.sampath
 */
public class SearchFieldModel
{

	private String columnHeader;
	private String fieldName;
	private String fieldType;
	private String defaultValue;
	private boolean showable;

	public SearchFieldModel()
	{
		this.fieldType = "String";
		this.defaultValue = "";
	}

	/**
	 * @param fieldName
	 */
	public SearchFieldModel( String fieldName )
	{
		this( null, fieldName, null, "", false );
	}

	/**
	 * @param fieldName
	 * @param fieldType
	 */
	public SearchFieldModel( String columnHeader, String fieldName, String fieldType, boolean showable )
	{
		this( columnHeader, fieldName, fieldType, "", showable );
	}

	/**
	 * @param columnHeader
	 * @param fieldName
	 * @param fieldType
	 * @param defaultValue
	 * @param showable
	 */
	public SearchFieldModel(
		String columnHeader, String fieldName, String fieldType, String defaultValue, boolean showable )
	{
		if ( columnHeader == null )
		{
			this.columnHeader = fieldName;
		}
		this.fieldName = fieldName;
		this.fieldType = computeFieldType( fieldType );
		this.defaultValue = defaultValue;
		this.showable = showable;
	}

	/**
	 * @return the columnHeader
	 */
	public String getColumnHeader()
	{
		return columnHeader;
	}

	/**
	 * @param columnHeader
	 *            the columnHeader to set
	 */
	public void setColumnHeader( String columnHeader )
	{
		this.columnHeader = columnHeader;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName()
	{
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            the fieldName to set
	 */
	public void setFieldName( String fieldName )
	{
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldType
	 */
	public String getFieldType()
	{
		return fieldType;
	}

	/**
	 * @param fieldType
	 *            the fieldType to set
	 */
	public void setFieldType( String fieldType )
	{
		this.fieldType = computeFieldType( fieldType );
	}

	/**
	 * @return the defaultValue
	 */
	public Object getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue( String defaultValue )
	{
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the showable
	 */
	public boolean isShowable()
	{
		return showable;
	}

	/**
	 * @param showable
	 *            the showable to set
	 */
	public void setShowable( boolean showable )
	{
		this.showable = showable;
	}

	/**
	 * flush the wrappers around for the primitive types
	 * 
	 * @param strType
	 * @return String value reprenseting the wrapper class names
	 */
	String computeFieldType( String strType )
	{
		String defaultType = "String";
		if ( strType != null )
		{
			if ( "byte".equalsIgnoreCase( strType ) )
			{
				defaultType = "Byte";
			}
			else if ( "char".equalsIgnoreCase( strType ) )
			{
				defaultType = "Character";
			}
			else if ( "short".equalsIgnoreCase( strType ) )
			{
				defaultType = "Short";
			}
			else if ( "int".equalsIgnoreCase( strType ) )
			{
				defaultType = "Integer";
			}
			else if ( "long".equalsIgnoreCase( strType ) )
			{
				defaultType = "Long";
			}
			else if ( "float".equalsIgnoreCase( strType ) )
			{
				defaultType = "Float";
			}
			else if ( "double".equalsIgnoreCase( strType ) )
			{
				defaultType = "Double";
			}
			else if ( "boolean".equalsIgnoreCase( strType ) )
			{
				defaultType = "Boolean";
			}
			else if ( "date".equalsIgnoreCase( strType ) )
			{
				defaultType = "Date";
			}
		}

		return defaultType;
	}

}
