package com.diodesoftware.scb.clipboard;

import org.apache.log4j.Logger;

public class ClipSqlException
	extends RuntimeException
{
	private Throwable cause;
	private String sql;
	private Logger log = Logger.getLogger(ClipSqlException.class);
	public ClipSqlException(Throwable cause, String sql)
	{
		this.cause = cause;
		this.sql = sql;
		log.error("SQL Error SQL:" + sql, cause);
		
	}
	
	public Throwable getCause()
	{
		return cause;
	}
	
	public String getSql()
	{
		return sql;
	}
}
