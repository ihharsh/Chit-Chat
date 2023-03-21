# Chit-Chat App
# Overview
Chit-Chat is a chat application developed in Android Studio with Kotlin programming language. The app uses Firebase services such as Firebase Auth, Firebase Realtime Database and Firebase Storage to provide a seamless and secure chatting experience. The app allows users to create an account using Firebase Auth, and maintain their account information using Firebase Realtime Database. Firebase Storage is used to store user profile images.

Every account created in Chit-Chat is visible to all the users, hence anyone can chat with anyone who has an account on Chit-Chat. The app uses Picasso library to put images and de.hdodenhof.circleimageview.CircleImageView for circular shape images.

# Features
User authentication using Firebase Auth
Real-time chat messaging using Firebase Realtime Database
Profile image storage using Firebase Storage
Circular profile images using de.hdodenhof.circleimageview.CircleImageView
Image loading using Picasso library
# Requirements
Android Studio IDE (version 4.0 or above)
Kotlin plugin for Android Studio
Firebase account
An Android device or emulator running Android 5.0 (Lollipop) or above
# Installation
Clone the project from the GitHub repository or download and extract the zip file.
Open Android Studio and select Open an existing Android Studio project.
Navigate to the cloned or extracted project directory and select the build.gradle file in the root directory.
Wait for the project to sync and build.
Create a Firebase project and add the google-services.json file to the project's app directory.
Enable Firebase services: Firebase Auth, Firebase Realtime Database, and Firebase Storage.
Run the app on an emulator or physical device.
# Usage
Open the Chit-Chat app on your device or emulator.
Create an account using your email address and a strong password.
Set your profile image by clicking on the profile picture icon and selecting an image from your device's gallery.
Start chatting with other Chit-Chat users by clicking on the Start Chatting button.
You will be randomly matched with another Chit-Chat user. Start typing your message and hit the send button.
You can end the chat by clicking on the End Chat button.
# Credits
The following libraries and resources were used in the development of Chit-Chat app:

Kotlin programming language
Firebase services: Firebase Auth, Firebase Realtime Database, and Firebase Storage
Picasso image loading library
de.hdodenhof.circleimageview.CircleImageView library for circular images
