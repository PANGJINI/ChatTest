package com.example.myapplication

class Comments(
    var content: String,    //댓글 내용
    var writerId: String,   //작성자 uid
    var writerName: String, //작성자 이름
    var writeTime: String
) {
    constructor(): this("", "", "", "")
}