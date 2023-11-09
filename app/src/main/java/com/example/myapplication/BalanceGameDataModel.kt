package com.example.myapplication

class BalanceGameDataModel(
    var bal1: String?,          //밸겜 항목1
    var bal2: String?,          //밸겜 항목2
    var voteCountList: List<Int>?,   // 투표수 배열
    var postUserId: String?,    //게시자 uid
    var postUserName: String?,  //게시자 이름
    var postUserGender: String?,
    var time: String?,
    var voteUserList: List<String>? )
{
    constructor(): this("", "", listOf(0,0),"", "","","", listOf(""))
}