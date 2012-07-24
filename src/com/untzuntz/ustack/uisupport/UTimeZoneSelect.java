package com.untzuntz.ustack.uisupport;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import com.untzuntz.ustack.data.Country;

public class UTimeZoneSelect extends USelectField {

	private static final long serialVersionUID = 1L;
	
	public UTimeZoneSelect()
	{
		fieldName = "Timezone";
	}
	
	public void setCountry(String name)
	{
		Country ctry = Country.getCountryByName(name);

		String filter = null;
		if (ctry != null && ctry.get("timeZoneFilter") != null)
			filter = ctry.getString("timeZoneFilter") + "/";
		
		List<String> zones = new Vector<String>();
		
		String[] zoneIds = TimeZone.getAvailableIDs();
		Arrays.sort(zoneIds);
		for (String zoneId : zoneIds)
		{
			if (filter != null)
			{
				if (zoneId.startsWith(filter))
					zones.add(zoneId);
			}
			else
				zones.add(zoneId);
		}

		setData(zones);
	}
}
