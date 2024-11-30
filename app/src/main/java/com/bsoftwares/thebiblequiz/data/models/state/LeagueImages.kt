package com.bsoftwares.thebiblequiz.data.models.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bsoftwares.thebiblequiz.R

enum class LeagueImages {
    SHIELD_CROSS,
    CHURCH,
    BE_NOT_AFRAID,
    FISH,
    THE_REDEEMER,
    BIBLE,
    FAMILY
}

@Composable
fun LeagueImages.getPainter(): Painter = painterResource(id = when(this) {
    LeagueImages.SHIELD_CROSS -> R.drawable.shield_cross_outline_icon
    LeagueImages.CHURCH -> R.drawable.church
    LeagueImages.BE_NOT_AFRAID -> R.drawable.angel_svgrepo_com
    LeagueImages.FISH -> R.drawable.fish
    LeagueImages.THE_REDEEMER -> R.drawable.jesus_brazil_svgrepo_com
    LeagueImages.BIBLE -> R.drawable.bible_24
    LeagueImages.FAMILY -> R.drawable.family_24
} )

@Composable
fun LeagueImages.getString(): String = stringResource(id = when(this) {
    LeagueImages.SHIELD_CROSS -> R.string.icon_shield_cross
    LeagueImages.CHURCH -> R.string.icon_church
    LeagueImages.BE_NOT_AFRAID -> R.string.icon_be_not_afraid
    LeagueImages.FISH -> R.string.icon_fish
    LeagueImages.THE_REDEEMER -> R.string.icon_christ_the_redeemer
    LeagueImages.BIBLE -> R.string.bible
    LeagueImages.FAMILY -> R.string.family
} )