package com.sharksharding.test.entity;

import java.util.Date;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/8/1
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class InfoEntity {

	private Long   id;
	private String name;
	private String address;
	private Date   createTime;
	private Date   updateTime;

	@Override
	public String toString() {
		return "id:" + id + ",name:" + name + ",address:" + address;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
