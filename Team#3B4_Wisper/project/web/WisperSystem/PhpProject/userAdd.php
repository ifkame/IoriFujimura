<?php

//ユーザ作成処理

include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');

if (!isset($_GET['userId']))  {
    //1,Inputパラメータの必須チェックを行う。
    errorcheck('006');
} else if (!isset($_GET['userName'])) {
    errorcheck('011');
} elseif (!isset($_GET['password'])) {
    errorcheck('007');
} else {

    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();
    //3.ユーザデータを挿入するSQL文を実行する。
    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));
    $userName = trim(htmlspecialchars($_GET['userName'], ENT_QUOTES, 'UTF-8'));
    $password = trim(htmlspecialchars($_GET['password'], ENT_QUOTES, 'UTF-8'));

    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);
    $userName = mysqli_real_escape_string($conn, $userName);
    $password = mysqli_real_escape_string($conn, $password);


    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userId = str_replace('%', '\%', $userId);
    $userName = str_replace('%', '\%', $userName);
    $password = str_replace('%', '\%', $password);
    //echo $userId . $userName . $password;
}
//SQL文の作成
$sql = "INSERT INTO user(userId, userName, password) VALUES(?,?,?)";

//SQLステートメントを実行する準備
$stmt = mysqli_prepare($conn, $sql);

//SQLステートメントと値をバインドする
mysqli_stmt_bind_param($stmt, 'sss', $userId, $userName, $password);


//ステートメントを実行
mysqli_stmt_execute($stmt);

//データベースの変更確認 登録できたかどうかのメッセージを変数に格納
if (!mysqli_stmt_affected_rows($stmt)) {
    //4.SQL文の実行結果を受取り、異常終了なら以下の処理を行う。
    //４－１．データベースのロールバック命令を実行する。
    mysqli_rollback($conn);
    //４－２．対象エラーメッセージをセットしてエラー終了させる。
    errorcheck('001');
} else {
    //５．データベースのコミット命令を実行する。
    mysqli_commit($conn);
    //６．返却値の連想配列に成功パラメータをセットする。
    $result = array('result' => 'success');
    //７．SQL情報をクローズさせる。
    //８．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
    //９．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);

    print $result;
}


