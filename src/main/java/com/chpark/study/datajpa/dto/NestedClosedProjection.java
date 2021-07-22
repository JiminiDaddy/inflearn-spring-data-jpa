package com.chpark.study.datajpa.dto;

public interface NestedClosedProjection {
	String getName();
	TeamInfo getTeam();

	interface TeamInfo {
		String getName();
	}
}
