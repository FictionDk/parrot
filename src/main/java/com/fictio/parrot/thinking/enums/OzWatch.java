package com.fictio.parrot.thinking.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum OzWatch {
	NORTH("witch of the north"),
	EAST("witch of the east"),
	SOUTH("witch of the south"),
	WEST("witch of the west");
	private String desc;
	private OzWatch (String desc) {
		this.desc = desc;
	}
	// 添加新方法
	public String getDesc() {return this.desc;}
	// 覆盖方法,修改只有首字母大写的名字
	public String toString() {
		String id = name();
		String lower = id.substring(1).toLowerCase();
		return id.charAt(0)+lower;
	}
	public static void main(String[] args) {
		for(OzWatch o : OzWatch.values()) {
			log.debug("OzWatch desc: {}",o.getDesc());
			log.debug("OzWatch string: {}",o);
		}
	}
}
