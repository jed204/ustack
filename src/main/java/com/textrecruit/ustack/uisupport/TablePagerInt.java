package com.textrecruit.ustack.uisupport;

public interface TablePagerInt {

	public int getMaxResults();
	public int getResultsPerPage();
	public int getResultCount();
	public void setResultsPerPage(int maxresults);
	public void setPage(int p);
	public void setResultCount(int count);
	public void clear();
	public int getPage();

	
	
}
