package com.example.myapplication

class Message(
    var message: String?,   //메시지 내용
    var sendId: String?,    //접속자 uid (보내는 사람uid)
    var receiveId: String?, //받는사람 uid
    var sendName: String?,
    var receiveName: String?,
    var time: String?) {
    constructor():this("", "", "", "","", "")
}