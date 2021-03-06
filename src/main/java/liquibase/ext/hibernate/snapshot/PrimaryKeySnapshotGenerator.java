package liquibase.ext.hibernate.snapshot;

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

import liquibase.exception.DatabaseException;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Column;
import liquibase.structure.core.Index;
import liquibase.structure.core.PrimaryKey;
import liquibase.structure.core.Table;

import org.hibernate.sql.Alias;

public class PrimaryKeySnapshotGenerator extends HibernateSnapshotGenerator {

    private static final int PKNAMELENGTH = 63;
    private static final String PK = "PK";

    // This is the same Alias as in org.hibernate.mapping.PersistentClass.PK_ALIAS
    private static final Alias PK_ALIAS_15 = new Alias(15, PK);
    private static final Alias NEW_PK_ALIAS = new Alias(PKNAMELENGTH, PK);

    public PrimaryKeySnapshotGenerator() {
        super(PrimaryKey.class, new Class[]{Table.class});
    }

    @Override
    protected DatabaseObject snapshotObject(DatabaseObject example, DatabaseSnapshot snapshot) throws DatabaseException, InvalidExampleException {
        return example;
    }

    @Override
    protected void addTo(DatabaseObject foundObject, DatabaseSnapshot snapshot) throws DatabaseException, InvalidExampleException {
        if (!snapshot.getSnapshotControl().shouldInclude(PrimaryKey.class)) {
            return;
        }
        if (foundObject instanceof Table) {
            Table table = (Table) foundObject;
            org.hibernate.mapping.Table hibernateTable = findHibernateTable(table, snapshot);
            if (hibernateTable == null) {
                return;
            }
            org.hibernate.mapping.PrimaryKey hibernatePrimaryKey = hibernateTable.getPrimaryKey();
            if (hibernatePrimaryKey != null) {
                PrimaryKey pk = new PrimaryKey();
                String hbnPrimaryKeyName = hibernatePrimaryKey.getName();
                /*
                 * pk name is probably truncated and maybe a duplicate in case
                 * of tables with long prefixes
                 */
                String hbnTableName = hibernateTable.getName();
                if (hbnPrimaryKeyName != null && hbnPrimaryKeyName.length() == 15 && hbnPrimaryKeyName.equals(PK_ALIAS_15.toAliasString(hbnTableName))) {
                    LOG.warning("Hibernate primary key name is probably truncated. " + hbnPrimaryKeyName);
                    String newAlias = NEW_PK_ALIAS.toAliasString(hbnTableName);
                    int newAliasLength = newAlias.length();
                    if (newAliasLength > 15) {
                        if (newAliasLength == PKNAMELENGTH) {
                            String suffix = "_" + Integer.toHexString(hbnTableName.hashCode()).toUpperCase() + "_" + PK;
                            hbnPrimaryKeyName = newAlias.substring(0, PKNAMELENGTH - suffix.length()) + suffix;
                        } else {
                            hbnPrimaryKeyName = newAlias;
                        }
                        LOG.warning("Changing hibernate primary key name to " + hbnPrimaryKeyName);
                    }
                }
                pk.setName(hbnPrimaryKeyName);
                pk.setTable(table);
                for (Object hibernateColumn : hibernatePrimaryKey.getColumns()) {
                    pk.getColumns().add(new Column(((org.hibernate.mapping.Column) hibernateColumn).getName()).setRelation(table));
                }

                LOG.info("Found primary key " + pk.getName());
                table.setPrimaryKey(pk);
                Index index = new Index();
                index.setName("IX_" + pk.getName());
                index.setTable(table);
                index.setColumns(pk.getColumns());
                index.setUnique(true);
                pk.setBackingIndex(index);
                table.getIndexes().add(index);
            }
        }
    }

}
