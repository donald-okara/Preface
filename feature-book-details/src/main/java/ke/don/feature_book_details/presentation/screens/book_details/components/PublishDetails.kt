package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ke.don.shared_components.components.GenreChip
import ke.don.shared_domain.data_models.VolumeInfoDet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PublishDetails(
    modifier: Modifier = Modifier,
    volumeInfo: VolumeInfoDet,
){
    OutlinedCard(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(
                fraction = 0.9f
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.padding(8.dp)
        ) {
            // Publisher Information
            Text(
                text = "Published by: ${volumeInfo.publisher}",
            )

            // Industry Identifiers
            volumeInfo.industryIdentifiers.forEach { identifier ->
                Text(
                    text = "${identifier.type}: ${identifier.identifier}",
                )
            }

            // Pages
            Text(
                text = "Pages: ${volumeInfo.pageCount}",
            )

            // Language
            Text(
                text = "Language: ${volumeInfo.language}",
            )

            // Maturity Ratings
            Text(
                text = "Maturity Ratings: ${volumeInfo.maturityRating}",
            )

            // Categories
            volumeInfo.categories.let { categories ->
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    categories.forEachIndexed { index, category ->
                        GenreChip(
                            category
                        )
                    }
                }
            }

        }

    }
}

