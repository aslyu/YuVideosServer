package com.yu.enums;

public enum VideoStatusEnum {
	
	SUCCESS(1),
	FORBID(2);
	
	private final int value;

	VideoStatusEnum(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
