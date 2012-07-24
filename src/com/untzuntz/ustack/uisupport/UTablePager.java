package com.untzuntz.ustack.uisupport;

import java.util.List;
import java.util.Vector;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Label;
import nextapp.echo.app.Row;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.apache.log4j.Logger;

public class UTablePager extends Column implements ActionListener,TablePagerInt {

	static Logger           		logger                  = Logger.getLogger(UTablePager.class);

	final static long serialVersionUID = 1L;

	private int page;
	private int pageCount;
	private int resultsPerPage;
	private int thisCount;
	private Row pagerRow;
	private Row pageNumberRow;
	private List<ActionListener> actions;
	private String actionCommand;
	private Label resultInfoLabel;
	private Grid grid;
	private int maxPagesShown;
	
	public void setActionCommand(String cmd) {
		actionCommand = cmd;
	}
	
	public UTablePager(ActionListener al, String actionName, int maxResults)
	{
		maxPagesShown = 10;
		
		actions = new Vector<ActionListener>();
		actions.add(al);
		
		actionCommand = actionName;
		
		page = 0;
		resultsPerPage = maxResults;

		grid = new Grid(2);
		grid.setBackground(UStackStatics.LIGHT_GRAY);
		grid.setInsets(UStackStatics.IN_5);
		grid.setWidth(UStackStatics.EX_100P);
		grid.setColumnWidth(1, UStackStatics.EX_150);
		grid.setVisible(false);
		add(grid);
		
		pagerRow = new URow(UStackStatics.EX_10);
		pagerRow.setLayoutData(UStackStatics.GRID_CENTER);
		grid.add(pagerRow);
		
		resultInfoLabel = new ULabel("", UStackStatics.FONT_SMALL);
		resultInfoLabel.setLayoutData(UStackStatics.GRID_RIGHT);
		grid.add(resultInfoLabel);
	}
	
	public int getMaxResults()
	{
		return resultsPerPage;
	}
	
	private Button prevBtn;
	private Button nextBtn;
	
	public int getResultsPerPage()
	{
		return resultsPerPage;
	}
	
	public void setMaxPagesShown(int pg)
	{
		maxPagesShown = pg;
	}
	
	public int getResultCount()
	{
		return thisCount;
	}
	
	public void setResultsPerPage(int maxresults)
	{
		resultsPerPage = maxresults;
	}
	
	public void setPage(int p)
	{
		page = p;
	}
	
	public void setResultCount(int count)
	{
		grid.setVisible(true);
		
		pageCount = count / resultsPerPage;
		pageCount = (count % resultsPerPage == 0 ? pageCount : pageCount + 1);
		thisCount = count;
		
		page = Math.max(0, Math.min(page, pageCount - 1));
		
		if (prevBtn == null)
		{
			prevBtn = new UButton("Previous", UStackStatics.WEB_BUTTON, this, "Previous");
			pagerRow.add(prevBtn);
		}

		// Previous
		if (page > 0)
			prevBtn.setVisible(true);
		else
			prevBtn.setVisible(false);

//		// Page Select
//		int start = page - 10;
//		if (start < 0)
//			start = 0;
//		
//		int end = start + 14;
//		if (end > pageCount)
//			end = pageCount;
		
		// Page Select
		int start = page - (int)(maxPagesShown / 2);
		if (start < 0)
			start = 0;
		
		int end = page + (int)(maxPagesShown / 2);
		if (end > pageCount)
			end = pageCount;
		else if ((end - start) < maxPagesShown)
			end = start + maxPagesShown;

		if (end > pageCount)
			end = pageCount;

		if (pageNumberRow == null)
		{
			pageNumberRow = new URow(UStackStatics.EX_5);
			pagerRow.add(pageNumberRow);
		}
		
		pageNumberRow.removeAll();
		
		for (int i = start; pageCount > 1 && i < end; i++)
		{
			if (i == page)
				pageNumberRow.add(new ULabel("" + (i + 1), UStackStatics.FONT_NORMAL));
			else
				pageNumberRow.add(new UButton("" + (i + 1), UStackStatics.WEB_BUTTON, this, i + ""));
		}

		if (nextBtn == null)
		{
			nextBtn = new UButton("Next", UStackStatics.WEB_BUTTON, this, "Next");
			pagerRow.add(nextBtn);
		}

		// Next
		if ((page + 1) < pageCount)
			nextBtn.setVisible(true);
		else
			nextBtn.setVisible(false);
	
		resultInfoLabel.setText(count + " results in " + pageCount + " pages");
	}
	
	public void clear()
	{
		page = 0;
		prevBtn.setVisible(false);
		nextBtn.setVisible(false);
		pageNumberRow.removeAll();
		resultInfoLabel.setText("");
		grid.setVisible(false);
	}

	public int getPage()
	{
		return page;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();

		if ("Previous".equalsIgnoreCase(action))
			page--;
		else if ("Next".equalsIgnoreCase(action))
			page++;
		else
			page = Integer.valueOf(e.getActionCommand());
		
		page = Math.max(0, Math.min(page, pageCount));
		
		logger.debug("Page is now : " + page);
		
		doActions("GotoPage", e);
	}
	
	private void doActions(String actionName, ActionEvent e)
	{
		if (actions != null)
		{
			if (actionCommand == null)
				actionCommand = actionName;
			
			ActionEvent ae = new ActionEvent(e.getSource(), actionCommand);
			for (ActionListener al : actions)
				al.actionPerformed(ae);
		}
	}


}
