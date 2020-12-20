/*
 * Copyright (C)  guolin, Glance Open Source Project
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

package com.glance.guolindev.ui.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.glance.guolindev.Glance
import com.glance.guolindev.R
import com.glance.guolindev.logic.model.Column
import com.glance.guolindev.logic.model.Resource
import com.glance.guolindev.logic.repository.DatabaseRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * DataViewModel holds view data of DataActivity and provide api to specific data operations of table.
 *
 * @author guolin
 * @since 2020/9/13
 */
class DataViewModel(private val repository: DatabaseRepository) : ViewModel() {

    /**
     * The LiveData variable to observe db file list.
     */
    val columnsLiveData: LiveData<Resource<List<Column>>>
        get() = _columnsLiveData

    private val _columnsLiveData = MutableLiveData<Resource<List<Column>>>()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        _columnsLiveData.value = Resource.error(throwable.message
                ?: Glance.context.getString(R.string.glance_library_uncaught_exception_happened))
    }

    /**
     * Get the columns of a table.
     */
    fun getColumnsInTable(table: String) = viewModelScope.launch(handler) {
        val columns = repository.getColumnsInTable(table)
        _columnsLiveData.value = Resource.success(columns)
    }

    /**
     * Get the flow to load data from specific table.
     */
    fun loadDataFromTable(table: String, columns: List<Column>) =
        repository.getDataFromTableStream(table, columns).cachedIn(viewModelScope)

}