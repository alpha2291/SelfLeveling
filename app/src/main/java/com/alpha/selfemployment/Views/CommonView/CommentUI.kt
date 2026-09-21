package com.alpha.selfemployment.Views.CommonView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.GlobalSnackbar
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.CommonView.di.CommonViewModel
import com.alpha.selfemployment.Views.CommonView.domain.model.CommentResponseData
import com.alpha.selfemployment.Views.CommonView.ui.NoCommentsView
import com.alpha.selfemployment.fontFamily
import com.alpha.selfemployment.forTab
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.networkToast
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.timeAgo
import com.alpha.selfemployment.toast
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.green3A8
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


// ------------------------
// Action State Holder
// ------------------------
data class CommentActionState(
    val text: String = "",
    val mentionUserName: String? = null,
    val mentionUserId: Int = 0,
    val parentCommentId: Int = 0,
    val editingCommentId: Int = 0,
    val status: String = "1" // 1-add, 2-edit
)


// ------------------------
// Main Comment UI
// ------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentUI(
    postId: Int,
    onDismiss: () -> Unit,
    appPrefs: AppPreferences = koinInject(),
    viewModel: CommonViewModel = koinViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var actionState by remember { mutableStateOf(CommentActionState()) }

    val network by rememberNetworkStatus()

    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = primaryWhite,
        onDismissRequest = onDismiss ,
        modifier = Modifier.fillMaxWidth().padding(top = rememberNotchHeightDp().value * 3)
    )
    {
        Column(modifier = Modifier) {


            CommentHeader(modifier = Modifier.fillMaxWidth())

            CommentsContent(
                modifier = Modifier
                    .weight(1f)
                , postId = postId,
                viewModel = viewModel,
                actionState = actionState,
                listState = listState,
                onActionChange = { actionState = it }
            )

            CommentInputBar(
                state = actionState,
                onTextChange = {
                    actionState = actionState.copy(text = it)
                },
                onClearMention = {
                    actionState = CommentActionState()
                },
                onSend = {
                    if (network == NetworkStatus.Online) {
                        viewModel.addCommentsApi(
                            user_id = appPrefs.getUserId(),
                            user_post_id = postId,
                            comment = actionState.text,
                            replies_comment_id = actionState.parentCommentId,
                            status = actionState.status,
                            mentionUserName = actionState.mentionUserName ?: "",
                            parentCommentId = actionState.parentCommentId,
                            comment_id = actionState.editingCommentId,
                            mention_id = actionState.mentionUserId
                        ) {
                            actionState = CommentActionState()
                            scope.launch {
                                // ✅ If it was the first comment, reload the list
                                if (viewModel.getMainComments.value.isEmpty()) {
                                    viewModel.resetMC()
                                    viewModel.loadMainComments(
                                        user_id = appPrefs.getUserId(),
                                        user_post_id = postId
                                    )
                                }
                                listState.animateScrollToItem(0)
                            }
//                            scope.launch {
//                                listState.animateScrollToItem(0)
//                            }
                        }
                    }
                    else {
                        networkToast()
                    }
                }
            )
        }
    }
}

// ------------------------
// Header
// ------------------------
@Composable
private fun CommentHeader(modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        zText("Comments", primaryBlack, 18, 1)
    }
}

// ------------------------
// Comments Content
// ------------------------


@Composable
private fun CommentsContent(
    modifier: Modifier,
    postId: Int,
    viewModel: CommonViewModel,
    actionState: CommentActionState,
    onActionChange: (CommentActionState) -> Unit,
    listState : LazyListState,
    appPrefs: AppPreferences = koinInject()
) {
    val mainComments by viewModel.getMainComments.collectAsState()
    val replyComments by viewModel.getReplyComments.collectAsState()
    val isLoadingMoreMC by viewModel.isLoadingMoreMC.collectAsState()
    val isErrorMC by viewModel.isErrorMC.collectAsState()
    val expandedComments = remember { mutableStateListOf<Int>() }

    val network by rememberNetworkStatus()

    if (network == NetworkStatus.Online) {
        LaunchedEffect(postId) {
            viewModel.resetMC()
            viewModel.resetRC()
            viewModel.loadMainComments(
                user_id = appPrefs.getUserId(),
                user_post_id = postId
            )
        }
    }

    var reportBtm by remember { mutableStateOf(false) }
//    var reportData by remember { mutableStateOf<ReportData?>(null) }


    LazyColumn(
        modifier = modifier.fillMaxSize()
        , state = listState
    ) {

        when {


            network == NetworkStatus.Offline -> {
                item {
//                    NoInternet()
                }
            }

            isErrorMC -> {
                item {
//                    ApiFail(){
//                        viewModel.retryComments(
//                            user_id = appPrefs.getUserId(),
//                            user_post_id = postId
//                        )
//                    }
                }
            }

            isLoadingMoreMC && mainComments.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                        , contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
            }

            !isLoadingMoreMC &&  mainComments.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
//                            .fillMaxSize()
                        , contentAlignment = Alignment.Center
                    ) {
                        NoCommentsView()
                    }
                }
            }

            else  -> {
                items(mainComments, key = { "${it.comment_id}__-${it}" }) { comment ->
                    val isReported = comment.is_report == 1


                    // MAIN COMMENT
                    CommentItem(
                        comment = comment,
                        isReply = false,
                        onReplyClick = {
                            onActionChange(
                                actionState.copy(
                                    mentionUserName = comment.username,
                                    mentionUserId = comment.user_id,
                                    parentCommentId = comment.comment_id,
                                    status = "1"
                                )
                            )
                        },
                        onEdit = {
                            onActionChange(
                                actionState.copy(
                                    text = comment.comment,
                                    editingCommentId = comment.comment_id,
                                    status = "2"
                                )
                            )
                        },
                        onDelete = {
                            if (network == NetworkStatus.Online) {
                                viewModel.addCommentsApi(
                                    user_id = appPrefs.getUserId(),
                                    user_post_id = postId,
                                    comment = "",
                                    replies_comment_id = comment.parent_comment_id ?: 0,
                                    status = "3",
                                    comment_id = comment.comment_id,
                                    mention_id = comment.mention_id ?: 0,
                                    resultCallback = {}
                                )
                            }
                            else {
                                networkToast()
                            }
                        }
                        , onLikeClick = {
                            println("LIKEEEEEE --- ${comment.is_liked}")
                            if (network == NetworkStatus.Online) {
                                val currentComment =
                                    viewModel.getMainComments.value.firstOrNull { it.comment_id == comment.comment_id }
                                currentComment?.let { c ->
                                    viewModel.commentLikeApi(
                                        user_id = appPrefs.getUserId(),
                                        user_post_id = postId,
                                        comment_id = c.comment_id,
                                        status = if (c.is_liked == 1) "2" else "1"  // now reads latest
                                    ) { postLikeStatus ->
                                        if (postLikeStatus is ResultHandler.Success) {
                                            viewModel.setCommentLike(c.user_id, c.comment_id)
                                        }
                                    }
                                }
                            }
                            else
                            {
                                networkToast()
                            }
                        }
                        , onReport = { userId , commentId ->
//                            if (!isReported) { // ✅ check latest state
//                                reportBtm = true
//                                reportData = ReportData(
//                                    receiverId = userId,
//                                    postId = postId,
//                                    commentId = commentId,
//                                    typeOfComment = 1
//                                )
//                            } else {
//                                toast("Already Reported")
//                            }
                        }
                    )

                    // DEFAULT LAST REPLY
                    if (!expandedComments.contains(comment.comment_id)) {
                        comment.last_reply?.forEachIndexed { index, reply ->

                            CommentItem(
                                comment = reply,
                                isReply = true,
                                onReplyClick = {
                                    onActionChange(
                                        actionState.copy(
                                            mentionUserName = reply.username,
                                            mentionUserId = reply.user_id,
                                            parentCommentId = reply.parent_comment_id ?: 0,
                                            status = "1"
                                        )
                                    )
                                },
                                onEdit = {
                                    onActionChange(
                                        actionState.copy(
                                            text = reply.comment,
                                            editingCommentId = reply.comment_id,
                                            parentCommentId = reply.parent_comment_id ?: 0,
                                            status = "2"
                                        )
                                    )
                                },
                                onDelete = {
                                    if (network == NetworkStatus.Online){
                                        viewModel.deleteCommentApi(
                                            user_id = appPrefs.getUserId(),
                                            user_post_id = postId,
                                            comment_id = reply.comment_id,
                                            parent_comment_id = reply.parent_comment_id ?: 0
                                        )
                                    }
                                    else
                                    {
                                        networkToast()
                                    }
                                }
                                , onLikeClick = {
                                    if (network == NetworkStatus.Online) {
                                        viewModel.commentLikeApi(
                                            user_id = appPrefs.getUserId(),
                                            user_post_id = postId,
                                            comment_id = reply.comment_id,
                                            status = if (reply.is_liked == 1) "2" else "1"
                                        ) { postLikeStatus ->
                                            if (postLikeStatus is ResultHandler.Success) {
                                                viewModel.setCommentLastReplyLike(reply.user_id, reply.comment_id)
                                            }
                                        }
                                    }
                                    else
                                    {
                                        networkToast()
                                    }
                                }
                                , onReport = { userId , commentId ->
//                                    reportBtm = true
//                                    reportData = ReportData(
//                                        receiverId = userId,
//                                        postId = postId,
//                                        commentId = commentId
//                                        , typeOfComment = 2
//                                    )
                                }
                            )
                        }
                    }




                    // VIEW MORE (TOP)
                    comment.total_reply?.let { total ->
                        if (total > 1 && !expandedComments.contains(comment.comment_id)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                                , horizontalArrangement = Arrangement.End
                            ) {
                                HorizontalDivider(modifier = Modifier.width(96.dp))

                                Text(
                                    text = "View more ($total)",
                                    modifier = Modifier
                                        //.fillMaxWidth()
                                        .padding(start = 8.dp ,end = 8.dp)
                                        .shrinkClick {
                                            if (network == NetworkStatus.Online){
                                                expandedComments.add(comment.comment_id)
                                                viewModel.loadReplyComments(
                                                    user_id = appPrefs.getUserId(),
                                                    user_post_id = postId,
                                                    comment_id = comment.comment_id
                                                )
                                            }
                                            else
                                            {
                                                networkToast()
                                            }
                                        },
                                    color = green3A8
                                )

                            }

                        }
                    }


                    // FULL REPLIES
                    if (expandedComments.contains(comment.comment_id)) {

                        replyComments
                            .filter { it.parent_comment_id == comment.comment_id }
                            .forEach { reply ->

                                val isReported = reply.is_report == 1

                                CommentItem(
                                    comment = reply,
                                    isReply = true,
                                    onReplyClick = {
                                        onActionChange(
                                            actionState.copy(
                                                mentionUserName = reply.username,
                                                mentionUserId = reply.user_id,
                                                parentCommentId = reply.parent_comment_id ?: 0,
                                                status = "1"
                                            )
                                        )
                                    },
                                    onEdit = {
                                        onActionChange(
                                            actionState.copy(
                                                text = reply.comment,
                                                editingCommentId = reply.comment_id,
                                                parentCommentId = reply.parent_comment_id ?: 0,
                                                status = "2"
                                            )
                                        )
                                    },
                                    onDelete = {
                                        if (network == NetworkStatus.Online){
                                            viewModel.deleteCommentApi(
                                                user_id = appPrefs.getUserId(),
                                                user_post_id = postId,
                                                comment_id = reply.comment_id,
                                                parent_comment_id = reply.parent_comment_id ?: 0
                                            )
                                        }
                                        else
                                        {
                                            networkToast()
                                        }
                                    }
                                    , onLikeClick = {
                                        if (network == NetworkStatus.Online){
                                            viewModel.commentLikeApi(
                                                user_id = appPrefs.getUserId(),
                                                user_post_id = postId,
                                                comment_id = reply.comment_id,
                                                status = if (reply.is_liked == 1) "2" else "1"
                                            ) { postLikeStatus ->
                                                if (postLikeStatus is ResultHandler.Success) {
                                                    viewModel.setReplyCommentLike(reply.user_id, reply.comment_id)
                                                }
                                            }
                                        }
                                        else
                                        {
                                            networkToast()
                                        }
                                    }
                                    , onReport = { userId , commentId ->
//                                        if (!isReported) {
//                                            reportBtm = true
//                                            reportData = ReportData(
//                                                receiverId = userId,
//                                                postId = postId,
//                                                commentId = commentId,
//                                                typeOfComment = 2
//                                            )
//                                        } else {
//                                            toast("Already Reported")
//                                        }
                                    }
                                )

                            }
                    }

                    // VIEW LESS (BOTTOM)
                    comment.total_reply?.let { total ->
                        if (total > 1 && expandedComments.contains(comment.comment_id)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                                , horizontalArrangement = Arrangement.End
                            ) {
                                HorizontalDivider(modifier = Modifier.width(96.dp))

                                Text(
                                    text = "View less",
                                    modifier = Modifier
                                        //.fillMaxWidth()
                                        .padding(start = 8.dp ,end = 8.dp)
                                        .shrinkClick {
                                            expandedComments.remove(comment.comment_id)
                                        },
                                    color = green3A8
                                )
                            }
                        }
                    }

                }
            }
        }
    }

}




// ------------------------
// Comment Item
// ------------------------



@Composable
private fun CommentItem(
    comment: CommentResponseData,
    isReply: Boolean,
    onReplyClick: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onReport: ((Int , Int ) -> Unit)? = null,
    onLikeClick : () -> Unit
    ,appPrefs: AppPreferences = koinInject()

) {
    var expanded by remember { mutableStateOf(false) }


    val navigator = LocalNavigator.current


    ListItem(
//        modifier = Modifier.padding(start = if (isReply) 48.dp else 0.dp),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(gray48)
                , contentAlignment = Alignment.Center
            ){
                SubcomposeAsyncImage(
                    model = comment?.profile_image ?: "",
                    modifier = Modifier
                        .fillMaxSize()
                    , contentDescription = ""
                    , contentScale = ContentScale.FillBounds
                )
                {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(primaryWhite)
                                .border(1.dp , green3A8 , CircleShape)
                            , contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = comment?.username.takeIf { it?.isNotEmpty() == true }?.take(1)?.uppercase() ?: "",
                                fontSize = textUnit(14),
                                fontFamily = fontFamily(1),
                                color = primaryBlack
                            )
                        }
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
        },
        headlineContent = {
//            Text(comment.comment)
            Row(
                verticalAlignment = Alignment.CenterVertically
                , horizontalArrangement = Arrangement.Start
            ) {
                if(comment.mention_username?.isNotEmpty() == true){
                    zText("@${(comment.mention_username ?: "")}", green3A8, 16, 3)
                    spacer(2)
                }
                zText(comment.comment, primaryBlack, 16, 3)
            }
        },
        overlineContent = {
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Row {
                        Text(comment.username
                            , color = primaryBlack
                            , fontSize = textUnit(16)
                            , fontFamily = fontFamily(1)
                            , overflow = TextOverflow.Ellipsis
                        )
//                        CommonText(, primaryBlack, 16, 1, )

                        spacer(4)

                        zText(
                            timeAgo(comment.created_at),
                            primaryBlack.copy(alpha = 0.4f),
                            14,
                            1
                        )
                    }

                    Box {
                        Image(
                            painter = painterResource(R.drawable.more_vert),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(primaryBlack),
                            modifier = Modifier.shrinkClick { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }, containerColor = primaryWhite
                        )
                        {
                            if (comment.user_id == appPrefs.getUserId()) {
                                DropdownMenuItem(
                                    text = { Text("Edit"  ,color = primaryBlack)},
                                    onClick = {
                                        expanded = false
                                        onEdit?.invoke()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete", color = Color.Red) },
                                    onClick = {
                                        expanded = false
                                        onDelete?.invoke()
                                    }
                                )
                            }
                            else {

                                DropdownMenuItem(
                                    text = { Text("Report", color = Color.Red) },
                                    onClick = {
                                        println("🚨 REPORT BUTTON CLICKED")
                                        println("   comment.comment_id: ${comment.comment_id}")
                                        println("   comment.user_id: ${comment.user_id}")
                                        println("   comment.is_report: ${comment.is_report}")
                                        println("   comment.parent_comment_id: ${comment.parent_comment_id}")

                                        if (comment.is_report == 0) {
                                            expanded = false
                                            println("   ✅ is_report == 0, proceeding with report")
                                            onReport?.invoke(comment.user_id, comment.comment_id)
                                        } else {
                                            println("⛔ is_report == ${comment.is_report}, showing toast")
                                            GlobalSnackbar.show(id = R.string.already_reported)
                                            expanded = false
                                        }
                                    }
                                )
//                                DropdownMenuItem(
//                                    text = { Text("Report", color = Color.Red) },
//                                    onClick = {
//                                        println("Comment id *** parent id-- ${comment.comment_id} -- ${comment.parent_comment_id}")
//                                        if (comment.is_report == 0) {
//                                            expanded = false
//                                            println("Comment id *** parent id 2222-- ${comment.comment_id} -- ${comment.parent_comment_id}")
//                                            onReport?.invoke(comment.user_id, comment.comment_id)
//                                        }
//                                        else {
//                                            toast("Already Reported")
//                                        }
//                                    }
//                                )
                            }
                        }
                    }
                }

                spacer(2)
            }

        },
        supportingContent = {

            Column() {

                spacer(2)

                Row(verticalAlignment = Alignment.CenterVertically) {

                    val likeIcon =
                        if (comment.is_liked == 1)
                            R.drawable.like_filled
                        else
                            R.drawable.like_vd

                    Image(
                        painter = painterResource(likeIcon),
                        contentDescription = null,
                        // colorFilter = ColorFilter.tint( if (comment.is_liked == 1)  else primaryBlack.copy(.4f)),
                        modifier = Modifier
                            .size(20.dp)
                            .shrinkClick {
                                onLikeClick()
                            }
                    )



                    if (comment.like_count != 0) {

                        spacer(2)

                        zText("${comment.like_count}", green3A8, 16, 3)
                    }

                    spacer(6)

                    // !isReply &&
                    if (onReplyClick != null) {
                        Text(
                            "Reply",
                            modifier = Modifier.shrinkClick { onReplyClick() },
                            color = green3A8
                        )
                    }
                }

            }

        }
        , colors = ListItemDefaults.colors(
            containerColor = primaryWhite
        ), modifier = Modifier
            .padding(start = if (isReply) 48.dp else 0.dp)
            .fillMaxWidth()
            .shrinkClick {
                if (comment.user_id != appPrefs.getUserId()) {
                    navigator.navigate(Screen.OthersProfile(comment.user_id))
                }
            }
    )


}





@Composable
private fun CommentInputBar(
    state: CommentActionState,
    onTextChange: (String) -> Unit,
    onClearMention: () -> Unit,
    onSend: () -> Unit
) {
    Column {
        if (state.mentionUserName != null) {
            Row(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("@${state.mentionUserName}", color = green3A8)

                Image(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null,
                    modifier = Modifier.shrinkClick { onClearMention() }
                )
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = state.text,
                onValueChange = onTextChange,
                textStyle = TextStyle(
                    fontSize = textUnit(16)
                    , fontFamily = fontFamily(3)
                    , color = primaryBlack
                ),
                modifier = Modifier.weight(9f).height(56.dp),
                placeholder = { Text("Write a comment") }
            )

            Image(
                painter = painterResource(R.drawable.comment_send),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(56.dp)
                    .weight(if (forTab()) 1f else 2.5f)
                    .shrinkClick {
                        if (state.text.isNotBlank()) onSend()
                    }
            )
        }
    }
}

