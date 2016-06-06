package liquibase.ext.hibernate.database.connection;

/*
 * #%L
 * Liquibase Hibernate 5 Integration
 * %%
 * Copyright (C) 2016 Liquibase.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Implements java.sql.Connection in order to pretend a hibernate configuration is a database in order to fit into the Liquibase framework.
 * Beyond standard Connection methods, this class exposes {@link #getPrefix()}, {@link #getPath()} and {@link #getProperties()} to access the setting passed in the JDBC URL.
 */
public class HibernateConnection implements Connection {
    private String prefix;
    private String url;

    private String path;
    private Properties properties;

    public HibernateConnection(String url) {
        this.url = url;

        this.prefix = url.replaceFirst(":[^:]+$", "");

        // Trim the prefix off the URL for the path
        path = url.substring(prefix.length() + 1);

        // Check if there is a parameter/query string value.
        properties = new Properties();

        int queryIndex = path.indexOf('?');
        if (queryIndex >= 0) {
            // Convert the query string into properties
            properties.putAll(readProperties(path.substring(queryIndex + 1)));
            // Remove the query string
            path = path.substring(0, queryIndex);
        }
    }

    /*
     * Creates properties to attach to this connection based on the passed query string.
     */
    protected Properties readProperties(String queryString) {
        Properties properties = new Properties();
        queryString = queryString.replaceAll("&", System.getProperty("line.separator"));
        try {
            queryString = URLDecoder.decode(queryString, "UTF-8");
            properties.load(new StringReader(queryString));
        } catch (IOException ioe) {
            throw new IllegalStateException("Failed to read properties from url", ioe);
        }

        return properties;
    }

    /*
     * Returns the entire connection URL
     */
    public String getUrl() {
        return url;
    }


    /*
     * Returns the 'protocol' of the URL. For example, "hibernate:classic" or "hibernate:ejb3"
     */
    public String getPrefix() {
        return prefix;
    }

    /*
     * The portion of the url between the path and the query string. Normally a filename or a class name.
     */
    public String getPath() {
        return path;
    }

    /*
     * The set of properties provided by the URL. Eg:
     * <p/>
     * <code>hibernate:classic:/path/to/hibernate.cfg.xml?foo=bar</code>
     * <p/>
     * This will have a property called 'foo' with a value of 'bar'.
     */
    public Properties getProperties() {
        return properties;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// JDBC METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Statement createStatement() throws SQLException {
        return null;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null;
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    public void commit() throws SQLException {

    }

    public void rollback() throws SQLException {

    }

    public void close() throws SQLException {

    }

    public boolean isClosed() throws SQLException {
        return false;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return new HibernateConnectionMetadata(url);
    }

    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    public boolean isReadOnly() throws SQLException {
        return true;
    }

    public void setCatalog(String catalog) throws SQLException {

    }

    public String getCatalog() throws SQLException {
        return "HIBERNATE";
    }

    public void setTransactionIsolation(int level) throws SQLException {

    }

    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public void clearWarnings() throws SQLException {

    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    public void setHoldability(int holdability) throws SQLException {

    }

    public int getHoldability() throws SQLException {
        return 0;
    }

    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    public void rollback(Savepoint savepoint) throws SQLException {

    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    public Clob createClob() throws SQLException {
        return null;
    }

    public Blob createBlob() throws SQLException {
        return null;
    }

    public NClob createNClob() throws SQLException {
        return null;
    }

    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    public Properties getClientInfo() throws SQLException {
        return null;
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    //@Override only in java 1.7
    public void abort(Executor arg0) throws SQLException {
    }

    //@Override only in java 1.7
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    //@Override only in java 1.7
    public String getSchema() throws SQLException {
        return "HIBERNATE";
    }

    //@Override only in java 1.7
    public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
    }

    //@Override only in java 1.7
    public void setSchema(String arg0) throws SQLException {
    }
}
