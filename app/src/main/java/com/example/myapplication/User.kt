package com.example.myapplication

data class User(
    var email: String, // 아이디
    var uId: String,   // 비밀번호
    var name: String,  // 이름
    var nick: String,   //닉네임
    var age: String,
    var gender: String,
    var area: String,
    var number: String,
    var interest: String

){
    constructor(): this("", "", "", "","","","","", "")
}