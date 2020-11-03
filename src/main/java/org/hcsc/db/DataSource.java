package org.hcsc.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class DataSource {
	private static HikariConfig config = new HikariConfig();
	private static HikariDataSource dataSource;
	
	private static final String dataBaseUrl =
								"jdbc:jtds:sqlserver://localhost/HCSCPsiquiatria;"
									+ "instance=SQLEXPRESS01;"
									+ "user=sa;password=Root1001;charset=iso_1";	
//	private static final String dataBaseUrl =
//								"jdbc:jtds:sqlserver://localhost/HCSCPsiquiatriaDevelop;"
//									+ "instance=SQLEXPRESS01;"
//									+ "user=sa;password=Root1001;charset=iso_1";	
	
	static {
		config.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
		config.setJdbcUrl(dataBaseUrl);
		config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setConnectionTestQuery("SELECT GETDATE()");
        try {
        	dataSource = new HikariDataSource( config );
        }
        catch(RuntimeException ex) {
        	Logger.getLogger(DataSource.class).error("StackTrace: ", ex);
        }
        System.out.println("CONNECTION POOL CREATED");
	}
	
	private DataSource() {
	}
	
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}
