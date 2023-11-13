package com.example.metroidstore.fakedatasources

import com.example.metroidstore.datasources.DataSource

class DataSourceFake: DataSource {
    override val products = ProductDataSourceFake()
}