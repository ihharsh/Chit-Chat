package com.example.chitchat.Activity

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.chitchat.ModelClass.Messages
import com.example.chitchat.ModelClass.User

class UserListComposable (context: Context){

    var context = context



    @Composable
    fun UserCard(user: User){
        Row(Modifier.padding(10.dp)
            .clickable { openChat(user) })
        {
            Image(painter = rememberAsyncImagePainter(model = user.imageUri),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape))

            Spacer(modifier = Modifier.width(10.dp))



            var isExpanded by remember { mutableStateOf(false) }

            val surfaceColor by animateColorAsState(
                if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            )

            Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                Text(text = user.name, fontSize = 15.sp,
                    color = MaterialTheme.colors.secondaryVariant)

                Spacer(modifier = Modifier.size(4.dp))
                Surface(shape = MaterialTheme.shapes.medium,
                    elevation = 10.dp, color = surfaceColor
                ){
                    Text(text = user.status, fontSize = 15.sp,
                        modifier = Modifier.padding(5.dp),
                        style = MaterialTheme.typography.body2,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1)
                }



            }

        }

    }

    @Composable
    fun UserListLazyColumn(users : ArrayList<User>) {
        LazyColumn()
        {
            items(items = users){ user->
                UserCard(user = user)
            }

        }

    }

    private fun openChat(user: User) {
        var name = user.name
        var uid = user.uid
        var image = user.imageUri
        var token = user.token

        var intent = Intent(context.applicationContext, ChatActivity::class.java)
        intent.putExtra("receiverName",name)
        intent.putExtra("receiverUid",uid)
        intent.putExtra("receiverImage",image)
        intent.putExtra("token",token)

        context.startActivity(intent)

    }






}