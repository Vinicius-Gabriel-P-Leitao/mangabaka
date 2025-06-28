/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.domain;

import io.ebean.DB
import io.ebean.Database
import io.ebean.PagedList
import kotlin.reflect.KClass

// @formatter:off
abstract class EbeanRepository<ID, T : Any>(
    private val entityClass: KClass<T>,
    private val db: Database = DB.getDefault()
) : CrudRepository<ID, T> {

    override fun findById(id: ID): T? {
        return db.find(entityClass.java, id)
    }

    override fun findAll(attribute: String, size: Int, page: Int): PagedList<T> {
        val offset: Int = (page - 1) * size
        return db.find(entityClass.java).where().orderBy().asc(attribute).setFirstRow(offset).setMaxRows(size).findPagedList()
    }

    override fun save(entity: T): T {
        db.save(entity)
        return entity
    }

    override fun delete(entity: T) {
        db.delete(entity)
    }

    override fun deleteById(id: ID) {
        db.delete(entityClass.java, id)
    }
}