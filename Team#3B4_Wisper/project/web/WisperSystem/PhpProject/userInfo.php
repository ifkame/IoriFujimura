<?php

//ログイン認証
include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');



if (!isset($_GET['userId'])) {
    //1,Inputパラメータの必須チェックを行う。
    errorcheck('006');
} else {
    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();

    //3.ユーザデータを挿入するSQL文を実行する。
    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));

    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);


    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userId = str_replace('%', '\%', $userId);
    //echo $userId;
}

//SQL文の作成
$sql = "SELECT userId as user, userName, password, profile, iconPath FROM user WHERE userId = '${userId}'";

//SQLステートメントを実行する準備
$stmt = mysqli_prepare($conn, $sql);

//ステートメントを実行
mysqli_stmt_execute($stmt);

//結果を転送する（件数の取得のため）
mysqli_stmt_store_result($stmt);

//件数の取得
$num = mysqli_stmt_num_rows($stmt);

//４．データのフェッチを行う。データが存在しない場合、対象エラーメッセージをセットしてエラー終了させる。
if ($num == 0) {
    errorcheck('004');
} else {
    //結果を変数に入れる
    mysqli_stmt_bind_result($stmt, $userId, $userName, $password, $profile, $iconPath);

    while (mysqli_stmt_fetch($stmt)) {
        //5．返却値の連想配列に成功パラメータをセットする。
        $result = [
            'result' => 'success',
            'userId' => "${userId}",
            'userName' => "${userName}",
            'password' => "${password}",
            'profile' => "${profile}",
            'icon' => "${iconPath}"
        ];
    }
    
    //６．SQL情報をクローズさせる。
    //7．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
    //8．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);

    print $result;
}