package com.alpha.selfemployment.Views.CommonView.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alpha.selfemployment.R
import com.alpha.selfemployment.fontFamily
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.zText

@Composable
fun NoCommentsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
        , verticalArrangement = Arrangement.Center
        , horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(R.drawable.clear) , "",
            modifier = Modifier.size(180.dp)
        )

        spacer(4)

        zText(
            str(R.string.no_comments_yet)
            , primaryBlack
            , 20
            ,2
        )

        spacer(4)


        Text(
            str(R.string.be_first_to_comment_order),
            color = primaryBlack
            , fontSize = textUnit(16)
            , fontFamily = fontFamily(3)
            , textAlign = TextAlign.Center
            , modifier = Modifier.padding(horizontal = 12.dp)
        )



    }
}