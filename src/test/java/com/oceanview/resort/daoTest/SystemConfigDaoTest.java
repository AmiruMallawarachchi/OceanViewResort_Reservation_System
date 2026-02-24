package com.oceanview.resort.daoTest;

import com.oceanview.resort.dao.SystemConfigDAO;
import com.oceanview.resort.exception.DatabaseException;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SystemConfigDaoTest {

    @Test
    public void get_nullOrBlankKey_returnsNullAndDoesNotHitDatabase() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        SystemConfigDAO dao = new SystemConfigDAO(dataSource);

        assertNull(dao.get(null));
        assertNull(dao.get("  "));
        verifyNoInteractions(dataSource);
    }

    @Test
    public void get_happyPath_returnsValue() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("config_value")).thenReturn("42");

        SystemConfigDAO dao = new SystemConfigDAO(dataSource);
        String value = dao.get("tax_rate_percent");

        assertEquals("42", value);
        verify(stmt).setString(1, "tax_rate_percent");
    }

    @Test
    public void get_missingTable_returnsNullInsteadOfThrowing() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenThrow(new SQLException("system_config table does not exist"));

        SystemConfigDAO dao = new SystemConfigDAO(dataSource);
        String value = dao.get("tax_rate_percent");

        assertNull(value);
    }

    @Test(expected = DatabaseException.class)
    public void get_otherSqlError_wrapsInDatabaseException() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenThrow(new SQLException("some other SQL error"));

        SystemConfigDAO dao = new SystemConfigDAO(dataSource);
        dao.get("tax_rate_percent");
    }

    @Test
    public void set_nullOrBlankKey_noDatabaseCall() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        SystemConfigDAO dao = new SystemConfigDAO(dataSource);

        dao.set(null, "x");
        dao.set("   ", "x");

        verifyNoInteractions(dataSource);
    }

    @Test
    public void set_happyPath_executesUpdate() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);

        SystemConfigDAO dao = new SystemConfigDAO(dataSource);
        dao.set("tax_rate_percent", "15.5");

        verify(stmt).setString(1, "tax_rate_percent");
        verify(stmt).setString(2, "15.5");
        verify(stmt).setString(3, "15.5");
        verify(stmt).executeUpdate();
    }

    @Test
    public void set_missingTable_ignoresError() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        doThrow(new SQLException("system_config table missing")).when(stmt).executeUpdate();

        SystemConfigDAO dao = new SystemConfigDAO(dataSource);
        dao.set("tax_rate_percent", "15.5");
        // no exception thrown
    }

    @Test(expected = DatabaseException.class)
    public void set_otherSqlError_wrapsInDatabaseException() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        doThrow(new SQLException("other error")).when(stmt).executeUpdate();

        SystemConfigDAO dao = new SystemConfigDAO(dataSource);
        dao.set("tax_rate_percent", "15.5");
    }
}

