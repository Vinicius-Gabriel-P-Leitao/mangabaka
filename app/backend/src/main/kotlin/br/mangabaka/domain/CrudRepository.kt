/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.domain;

import io.ebean.PagedList

interface CrudRepository<ID, T> {
    fun findById(id: ID): T?
    fun findAll(attribute: String, size: Int, page: Int): PagedList<T>
    fun save(entity: T): T
    fun delete(entity: T)
    fun deleteById(id: ID)
}