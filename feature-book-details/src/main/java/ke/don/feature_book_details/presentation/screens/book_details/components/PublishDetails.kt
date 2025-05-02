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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ke.don.feature_book_details.R
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
                text = stringResource(R.string.published_by, volumeInfo.publisher),
            )

            // Industry Identifiers
            volumeInfo.industryIdentifiers.forEach { identifier ->
                Text(
                    text = "${identifier.type}: ${identifier.identifier}",
                )
            }

            // Pages
            Text(
                text = stringResource(R.string.pages, volumeInfo.pageCount),
            )

            // Language
            Text(
                text = stringResource(R.string.language, volumeInfo.language),
            )

            // Maturity Ratings
            Text(
                text = stringResource(R.string.maturity_ratings, volumeInfo.maturityRating),
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

