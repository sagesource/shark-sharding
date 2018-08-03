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
	/**
	 * 原子名称，分库时，用作分库的 key
	 */
	private String  atomName;
	/**
	 * 数据库 HOST
	 */
	private String  host;
	/**
	 * 数据库 PORT
	 */
	private int     port;
	/**
	 * 数据库用户名
	 */
	private String  username;
	/**
	 * 数据库密码
	 */
	private String  password;
	/**
	 * 数据库名称
	 */
	private String  dbName;
	/**
	 * 数据库连接参数
	 */
	private String  param;
	/**
	 * 是否为主库
	 */
	private boolean isMaster;

	public String getAtomName() {
		return atomName;
	}

	public void setAtomName(String atomName) {
		this.atomName = atomName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public boolean getIsMaster() {
		return isMaster;
	}

	public void setIsMaster(boolean master) {
		isMaster = master;
	}
}
