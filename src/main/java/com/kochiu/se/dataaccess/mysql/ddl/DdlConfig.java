package com.kochiu.se.dataaccess.mysql.ddl;

import com.kochiu.se.common.util.poi.ExcelField;

/**
 * 
 * @author zhihongp
 *
 */
public class DdlConfig {

	@ExcelField(order = 0, header = "表名")
	private String table;

	@ExcelField(order = 1, header = "分表字段")
	private String column;

	private String test;

	@ExcelField(order = 2, header = "表个数")
	private Integer tableNum;

	@ExcelField(order = 3, header = "数据库")
	private String db;

	@ExcelField(order = 4, header = "数据库个数")
	private Integer dbNum;

	@ExcelField(order = 5, header = "是否支持事务")
	private Boolean isTransaction;

	public DdlConfig() {

	}

	public DdlConfig(String table, String column, Integer tableNum, String db, Integer dbNum) {
		this.table = table;
		this.column = column;
		this.tableNum = tableNum;
		this.db = db;
		this.dbNum = dbNum;
	}

	public DdlConfig(String table, String column, Integer tableNum, String db, Integer dbNum, Boolean isTransaction) {
		this.table = table;
		this.column = column;
		this.tableNum = tableNum;
		this.db = db;
		this.dbNum = dbNum;
		this.isTransaction = isTransaction;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Integer getTableNum() {
		return tableNum;
	}

	public void setTableNum(Integer tableNum) {
		this.tableNum = tableNum;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public Integer getDbNum() {
		return dbNum;
	}

	public void setDbNum(Integer dbNum) {
		this.dbNum = dbNum;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((db == null) ? 0 : db.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DdlConfig other = (DdlConfig) obj;
		if (db == null) {
			if (other.db != null)
				return false;
		} else if (!db.equals(other.db))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DdlConfig [table=" + table + ", column=" + column + ", test=" + test + ", tableNum=" + tableNum + ", db=" + db + ", dbNum=" + dbNum
				+ ", isTransaction=" + isTransaction + "]";
	}

}
