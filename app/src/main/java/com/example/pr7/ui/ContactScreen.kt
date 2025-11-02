package com.example.pr7.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pr7.Contact
import com.example.pr7.ui.theme.Pr7Theme

@Composable
fun ContactColumn(contact: Contact, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Имя: ${contact.firstName}",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Отчество: ${contact.middleName}",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Фамилия: ${contact.lastName}",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Мобильный телефон: ${contact.phone}",
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Избранное",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Адрес: ${contact.address}",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ContactCard(contact: Contact, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Имя: ${contact.firstName}",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Отчество: ${contact.middleName}",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Фамилия: ${contact.lastName}",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Мобильный телефон: ${contact.phone}",
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Избранное",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Адрес: ${contact.address}",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ContactList(contacts: List<Contact>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        contacts.forEach { contact ->
            ContactCard(contact = contact)
        }
    }
}

@Preview(name = "ContactColumnPreview", showBackground = true)
@Composable
fun ContactColumnPreview() {
    Pr7Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column {
                // App Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "FirstComposeProject",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Contact Content
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    ContactColumn(
                        contact = Contact(
                            firstName = "Евгений",
                            middleName = "Андреевич",
                            lastName = "Лукашин",
                            phone = "+7 495 495 95 95",
                            address = "г. Москва, 3-я улица Строителей, д. 25, кв. 12"
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "ListPreview", showBackground = true)
@Composable
fun ListPreview() {
    Pr7Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column {
                // App Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "FirstComposeProject",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Contact List
                ContactList(
                    contacts = listOf(
                        Contact(
                            firstName = "Евгений",
                            middleName = "Андреевич",
                            lastName = "Лукашин",
                            phone = "+7 495 495 95 95",
                            address = "г. Москва, 3-я улица Строителей, д. 25, кв. 12"
                        ),
                        Contact(
                            firstName = "Василий",
                            middleName = "Егорович",
                            lastName = "Кузякин",
                            phone = "--",
                            address = "Ивановская область, дер. Крутово, д. 4"
                        ),
                        Contact(
                            firstName = "Людмила",
                            middleName = "Прокофьевна",
                            lastName = "Калугина",
                            phone = "+7 495 788 78 78",
                            address = "Москва, Большая Никитская, д. 43, кв. 290"
                        )
                    )
                )
            }
        }
    }
}
