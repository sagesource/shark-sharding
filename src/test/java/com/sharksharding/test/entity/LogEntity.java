package com.sharksharding.test.entity;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/1
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class LogEntity {

	private Long   id;
	private String action;

	@Override
	public String toString() {
		return "id:" + id + ",action:" + action;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
