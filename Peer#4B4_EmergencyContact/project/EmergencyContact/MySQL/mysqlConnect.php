<?php

//データベース設定
define('DB', 'sys1');  //各自のデータベース名に変えて
define('TABLE', '');
define('HOST', 'localhost');
define('ID', 'root');
define('PASS', 'ecc');  //各自のパスワードに変えて

//Mysql接続
function mysqlConnect() {
    //接続確認
    if (!$conn = mysqli_connect(HOST, ID, PASS, DB)) {
        //die('データベースに接続できません');
    } else {
        //echo "接続できました";
    }
    //クエリの文字コードを設定
    mysqli_set_charset($conn, 'utf8');
	return $conn;
}

//コミット
function commit($conn) {
	mysqli_commit($conn);
}

//ロールバック
function rollback($conn) {
	mysqli_rollback($conn);
}

//mysqlConnect();
