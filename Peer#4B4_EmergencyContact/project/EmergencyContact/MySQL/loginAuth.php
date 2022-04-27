<?php
//ログイン情報のセッション化
session_start();
//ログイン認証
include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');

if (!isset($_POST['name']) || $_POST['name'] == trim("")) {
    //1,Inputパラメータの必須チェックを行う。
    errorcheck('006');
} else if (!isset($_POST['pwd']) || $_POST['pwd'] == trim("")) {
    errorcheck('007');
} else {
    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();
    //３．ユーザ情報を取得するSQL文を実行する。
    //フォームリクエストの内容を取得
    $userName = trim(htmlspecialchars($_POST['name'], ENT_QUOTES, 'UTF-8'));
    $pwd = trim(htmlspecialchars($_POST['pwd'], ENT_QUOTES, 'UTF-8'));


    //session_regenerate_id();    //セッションハイジャック対策
    $_SESSION['username'] = $userName;
    $_SESSION['pwd'] = $pwd;

    //mysqlで使用する特殊文字をエスケープする
    $userName = mysqli_real_escape_string($conn, $userName);
    $pwd = mysqli_real_escape_string($conn, $pwd);

    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userName = str_replace('%', '\%', $userName);
    $pwd = str_replace('%', '\%', $pwd);
    // echo $userName . $pwd;
}
//SQL文の作成
$sql = "SELECT COUNT(*) FROM user WHERE name = '$userName' AND password = '$pwd'";

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
if ($cnt != 1) {
    errorcheck('003');
} else {
    //5．返却値の連想配列に成功パラメータをセットする。
    $result = array('result' => 'success');
    //6．SQL情報をクローズさせる。
    //7．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
    //8．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);

    //print $result;

    // リダイレクトを実行
    header("Location: ../input.php");
    exit();
}
