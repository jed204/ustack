package com.textrecruit.ustack.uisupport;

import java.util.ArrayList;
import java.util.List;

public class UEntryError {

	/** empty list of error */
	public static List<UEntryError> getEmptyList() {
		return new ArrayList<UEntryError>();
	}	

	/** generate a UEntryError object and add it to the list that is returned */
	public static List<UEntryError> getListInstance(String fieldName, String errorMessage) {
		List<UEntryError> errorList = new ArrayList<UEntryError>();
		errorList.add(getInstance(fieldName, errorMessage));
		return errorList;
	}	

	/** generate a UEntryError object */
	public static UEntryError getInstance(String fieldName, String errorMessage) {
		UEntryError uee = new UEntryError();
		uee.fieldName = fieldName;
		uee.errorMessage = errorMessage;
		return uee;
	}

	public static UEntryError getTopLevelInstance(String errorMessage) {
		UEntryError uee = new UEntryError();
		uee.errorMessage = errorMessage;
		uee.topLevelError = true;
		return uee;
	}

	/** generate a UEntryError object and add it to the list that is returned */
	public static List<UEntryError> getTopLevelError(String errorMessage) {
		List<UEntryError> errorList = new ArrayList<UEntryError>();
		errorList.add(getTopLevelInstance(errorMessage));
		return errorList;
	}	


	private UEntryError() {}
	
	private String fieldName;
	private String errorMessage;
	private boolean topLevelError;
	
	public String getFieldName() { return fieldName; }
	public String getErrorMessage() { return errorMessage; }
	public boolean isTopLevel() { return topLevelError; }
	
}
