package com.sharksharding.model;

/**
 * <p>原子数据源配置</p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MatrixAtomModel {
	// 数据库IP
	private String  host;
	// 数据库PORT
	private String  port;
	// 数据库名称
	private String  dbName;
	// 数据库用户名
	private String  username;
	// 数据库密码
	private String  password;
	// 数据库连接参数
	private String  params;
	// 主库标志位
	private boolean isMaster;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public boolean getIsMaster() {
		return isMaster;
	}

	public void setIsMaster(boolean master) {
		isMaster = master;
	}
}
