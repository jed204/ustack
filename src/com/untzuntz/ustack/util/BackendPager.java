package com.untzuntz.ustack.util;

import com.untzuntz.ustack.uisupport.TablePagerInt;

public class BackendPager implements TablePagerInt {
	
	private int page;
	private int resultCount;
	private int resultsPerPage;

	public int getMaxResults() {
		return 0;
	}

	public int getResultsPerPage() {
		return resultsPerPage;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultsPerPage(int maxresults) {
		resultsPerPage = maxresults;
	}

	public void setPage(int p) {
		page = p;
	}

	public void setResultCount(int count) {
		resultCount = count;
	}

	public void clear() {
		page = 0;
	}

	public int getPage() {
		return page;
	}

	
}
