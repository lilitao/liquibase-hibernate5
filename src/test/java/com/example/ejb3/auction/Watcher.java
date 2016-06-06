package com.example.ejb3.auction;

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

import javax.persistence.*;

@Entity
public class Watcher {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="WATCHER_SEQ")
    @TableGenerator(name="WATCHER_SEQ",table="WatcherSeqTable")
    private Integer id;

    @SuppressWarnings("unused")
    private String name;

    @ManyToOne
    private AuctionItem auctionItem;
}