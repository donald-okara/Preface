package ke.don.shared_components.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.don.shared_components.mbuku_theme.ui.theme.MbukuTheme

@Composable
fun ProfileStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview
@Composable
fun ProfileStatCardPreview(){
    MbukuTheme(
        darkTheme = true
    ) {
        ProfileStatCard(label = "Books Read", value = "24")
    }
}
@Preview
@Composable
fun ProfileStatCardLightPreview(){
    MbukuTheme(
        darkTheme = false
    ) {
        ProfileStatCard(label = "Books Read", value = "24")
    }
}
