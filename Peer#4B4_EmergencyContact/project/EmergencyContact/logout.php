<?php
session_start();        //セッション開始
$_SESSION = array();    //セッション破棄
session_destroy();      //セッション停止

// リダイレクトを実行(headerが読み込まれる前に呼び出す)
header("Location: ./login.php"); //ログインに遷移
exit();
