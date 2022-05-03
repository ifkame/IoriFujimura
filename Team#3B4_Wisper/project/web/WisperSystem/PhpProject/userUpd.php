<?php

//ログイン認証
include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');



if (!isset($_GET['userId']) || $_GET['userId'] == trim("")) {
    //1,Inputパラメータの必須チェックを行う。
    return errorcheck('006');
}
if (!isset($_GET['userName']) && !isset($_GET['password']) && !isset($_GET['profile']) && !isset($_GET['icon'])) {
    if ($_GET['userName'] == trim("") || $_GET['password'] == trim("") || $_GET['profile'] == trim("") || $_GET['icon'] == trim("")) {
        //２．Inputパラメータの更新内容存在チェックを行う。
        return errorcheck('002');
    }
} else {
    //３．DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();

    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));
    $userName = trim(htmlspecialchars($_GET['userName'], ENT_QUOTES, 'UTF-8'));
    $password = trim(htmlspecialchars($_GET['password'], ENT_QUOTES, 'UTF-8'));
    $profile = trim(htmlspecialchars($_GET['profile'], ENT_QUOTES, 'UTF-8'));
    $iconPath = trim(htmlspecialchars($_GET['icon'], ENT_QUOTES, 'UTF-8'));


    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);
    $userName = mysqli_real_escape_string($conn, $userName);
    $password = mysqli_real_escape_string($conn, $password);
    $profile = mysqli_real_escape_string($conn, $profile);
    $iconPath = mysqli_real_escape_string($conn, $iconPath);


    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userId = str_replace('%', '\%', $userId);
    $userName = str_replace('%', '\%', $userName);
    $password = str_replace('%', '\%', $password);
    $profile = str_replace('%', '\%', $profile);
    $iconPath = str_replace('%', '\%', $iconPath);
    //echo $userId . $userName . $password;
    
    
}

//３．ユーザデータを更新するSQL文を実行する。
$sql = "UPDATE user SET userName=? , password=?, profile=?, iconPath=? WHERE userId =? ";

/*
 * sqlステートメントの実行準備
 * ステートメントハンドルの生成
 */
$stmt = mysqli_prepare($conn, $sql);

//ステートメントに値をバインドする
mysqli_stmt_bind_param($stmt, 'sssss', $userName, $password, $profile, $iconPath, $userId);
/* mysqli_stmt_bind_param
 * 　→プリペアードステートメントの
 * 　　パラメータマーカーに変数をバインドする
 * 'sssi'
 * 　→String型 × ３ + Integer型 × １
 * 　　対応する変数の型
 */

//sql文の実行
mysqli_stmt_execute($stmt);

if (!mysqli_error($conn)=="") { //エラーがあるとき ＝ mysqli_error($conn)に文字列が格納される
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
    
    //アイコン画像をbase64_encodeでエンコードする
    //$iconPath = base64_encode($result["icon"]);
    
    //７．SQL情報をクローズさせる。
    //８．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
    //９．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);

    print $result;
}

