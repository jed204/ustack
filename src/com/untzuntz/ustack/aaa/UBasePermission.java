package com.untzuntz.ustack.aaa;

public enum UBasePermission implements UStackPermissionEnum {

	Login("Login to resource"),
	LoginAs("Login to resource as another user"),
	ManageRoles("Managed Roles"),
	DummyPermission("Dummy permission for test cases");
	
	private String desc;
	private UBasePermission(String d) {
		desc = d;
	}

	public String getPermission() {
		return super.toString();
	}
	
	public String getDesc() {
		return desc;
	}

}
