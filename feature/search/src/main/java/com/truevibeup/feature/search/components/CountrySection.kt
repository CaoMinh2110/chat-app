package com.truevibeup.feature.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.model.Country
import com.truevibeup.core.ui.component.SearchBar
import com.truevibeup.feature.search.R

@Composable
fun CountrySection(
    selected: String?,
    countries: List<Country>,
    onSelect: (String?) -> Unit,
    onBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, countries) {
        if (query.isBlank()) countries
        else countries.filter { it.name.orEmpty().contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        SectionHeader(title = stringResource(R.string.title_select_country), onBack = onBack)

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    query = query,
                    onQueryChange = { query = it },
                    height = 44.dp,
                    cornerRadius = 12.dp
                )
                Spacer(Modifier.height(8.dp))
            }
            item {
                RadioOption(
                    name = "All",
                    selected = selected == null,
                    onClick = { onSelect(null) }
                )
            }
            items(filtered) { country ->
                RadioOption(
                    name = country.name.orEmpty(),
                    selected = selected == country.code,
                    onClick = { country.code?.let { onSelect(it) } }
                )
            }
        }
    }
}
