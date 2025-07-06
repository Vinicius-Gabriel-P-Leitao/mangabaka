package br.mangabaka.domain.usecase

import io.ebean.DB
import io.ebean.Database
import io.ebean.PagedList
import kotlin.reflect.KClass

// @formatter:off
abstract class EbeanRepository<ID, T : Any>(
    private val entityClass: KClass<T>,
    private val db: Database = DB.getDefault()
)  {
     fun findById(id: ID): T? {
        return db.find(entityClass.java, id)
    }

     fun findAll(attribute: String, size: Int, page: Int): PagedList<T> {
        val offset: Int = (page - 1) * size
        return db.find(entityClass.java).where().orderBy().asc(attribute).setFirstRow(offset).setMaxRows(size).findPagedList()
    }

     fun save(entity: T): T? {
        db.save(entity)
        return entity
    }

     fun delete(entity: T) {
        db.delete(entity)
    }

     fun deleteById(id: ID) {
        db.delete(entityClass.java, id)
    }
}