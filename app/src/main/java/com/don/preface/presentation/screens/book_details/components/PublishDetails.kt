package com.don.preface.presentation.screens.book_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.data.model.VolumeInfoDet

@Composable
fun PublishDetails(
    volumeInfo: VolumeInfoDet,
){
    OutlinedCard(
        modifier = Modifier
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
            modifier = Modifier.padding(8.dp)
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
                val processedCategories = categories.flatMap { it.split("/") }.toSet().toList()

                Text(text = "Categories:")
                Text(
                    text = processedCategories.joinToString(", "),
                    modifier = Modifier.padding(start = 16.dp) // Indent the categories
                )
            }
        }

    }
}

