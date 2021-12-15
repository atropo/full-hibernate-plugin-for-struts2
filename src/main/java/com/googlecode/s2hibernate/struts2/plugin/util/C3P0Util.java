package com.googlecode.s2hibernate.struts2.plugin.util;

import java.sql.SQLException;
import java.util.Set;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;

public class C3P0Util {
	
	/**
	 * Close all pooled datasources created by C3P0.
	 */
	public static void closePolledDataSources() {
		for (PooledDataSource dataSource:(Set<PooledDataSource>)C3P0Registry.allPooledDataSources()) {
			try {
				dataSource.close();
				DataSources.destroy(dataSource);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
