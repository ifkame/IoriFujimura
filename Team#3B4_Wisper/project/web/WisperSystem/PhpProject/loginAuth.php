<?php

//ログイン認証
include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');

if (!isset($_GET['userId']) || $_GET['userId'] == trim(""))  {
    //1,Inputパラメータの必須チェックを行う。
    errorcheck('006');
} else if (!isset($_GET['password']) || $_GET['password'] == trim("")) {
    errorcheck('007');
} else {
    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();
    //３．ユーザ情報を取得するSQL文を実行する。
    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));
    $password = trim(htmlspecialchars($_GET['password'], ENT_QUOTES, 'UTF-8'));

    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);
    $password = mysqli_real_escape_string($conn, $password);


    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userId = str_replace('%', '\%', $userId);
    $password = str_replace('%', '\%', $password);
    // echo $userId . $password;
}
//SQL文の作成
$sql = "SELECT COUNT(*) FROM user WHERE userId = '$userId' AND password = '$password'";

//SQLステートメントを実行する準備
$stmt = mysqli_prepare($conn, $sql);

//ステートメントを実行
mysqli_stmt_execute($stmt);

//結果を転送する（件数の取得のため）
mysqli_stmt_store_result($stmt);

//件数の取得
$num = mysqli_stmt_num_rows($stmt);

//結果を変数に入れる
mysqli_stmt_bind_result($stmt, $cnt);
	while (mysqli_stmt_fetch($stmt)) {
}

//4.データ件数が１件以外の場合、対象エラーメッセージをセットしてエラー終了させる。
if($cnt != 1){
	errorcheck('003');
}else{
	//5．返却値の連想配列に成功パラメータをセットする。
    $result = array('result' => 'success');
    //6．SQL情報をクローズさせる。
    //7．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
    //8．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);

    print $result;
}
