/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
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
 */

package com.cmgapps.common.curriculumvitae.utils

import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.db.SqlPreparedStatement

class MockCursor(private vararg val list: List<Any?>) : SqlCursor {

    private var rowIndex = -1

    override fun close() {
    }

    override fun getBytes(index: Int): ByteArray? = list[rowIndex][index] as? ByteArray

    override fun getDouble(index: Int): Double? = when (val value = list[rowIndex][index]) {
        is Number -> value.toDouble()
        else -> null
    }

    override fun getLong(index: Int): Long? = when (val value = list[rowIndex][index]) {
        is Number -> value.toLong()
        else -> null
    }

    override fun getString(index: Int): String? = list[rowIndex][index] as? String

    override fun next(): Boolean {
        return list.size > ++rowIndex
    }
}

class MockSqlDriver(private val mockCursor: MockCursor) : SqlDriver {

    private var transaction: Transacter.Transaction? = null

    override fun close() {
    }

    override fun currentTransaction(): Transacter.Transaction? = transaction

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ) {
        // Do Nothing
    }

    override fun executeQuery(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ): SqlCursor {
        return mockCursor
    }

    override fun newTransaction(): Transacter.Transaction {
        val enclosing = transaction
        val transaction = Transaction(enclosing)
        this.transaction = transaction
        return transaction
    }

    private inner class Transaction(
        override val enclosingTransaction: Transacter.Transaction?
    ) : Transacter.Transaction() {
        override fun endTransaction(successful: Boolean) {
            transaction = enclosingTransaction
        }
    }
}
