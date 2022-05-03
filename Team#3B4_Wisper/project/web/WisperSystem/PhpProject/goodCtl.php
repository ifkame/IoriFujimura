<?php

//イイネ管理処理

include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');

function toBoolean(string $str) {
    return ($str === 'true' || $str == 1);
}

$goodFlg = $_GET['goodFlg'];
//var_dump(toBoolean($goodFlg));

//1,Inputパラメータの必須チェックを行う。
if (!isset($_GET['userId'])) {
    errorcheck('006');
} else if (!isset($_GET['whisperNo'])) {
    errorcheck('008');
} else if (!isset($_GET['goodFlg'])) {
    errorcheck('014');
} else {

    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();

    //3-1.いいねデータを挿入するSQL文を実行する。
    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));
    $whisperNo = trim(htmlspecialchars($_GET['whisperNo'], ENT_QUOTES, 'UTF-8'));

    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);
    $whisperNo = mysqli_real_escape_string($conn, $whisperNo);

    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userId = str_replace('%', '\%', $userId);
    $whisperNo = str_replace('%', '\%', $whisperNo);
    //echo $userId . $whisperNo;

    //3.いいねフラグがtrue(イイね)の場合、以下の処理を行う。
    if ($goodFlg == "true" || $goodFlg == 1) {
        //SQL文の作成
        $sql = "INSERT INTO goodInfo(userId, whisperNo) values (?, ?)";

        //４．イイねフラグがFALSE(イイね外す)の場合、以下の処理を行う。
    } else {
        //４－１．イイねデータを削除するSQL文を実行する。
        $sql = "DELETE FROM goodInfo WHERE userId = ? AND whisperNo = ?";
    }
    // ↓ sql文以降は同じ処理だからまとめました
    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql);
    //SQLステートメントと値をバインドする
    mysqli_stmt_bind_param($stmt, 'si', $userId, $whisperNo);
    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //var_dump($stmt);
    //追加した行が格納されていなければ MySQL を終了
    if (mysqli_stmt_affected_rows($stmt)) {
        /* mysqli_stmt_affected_rows
         * 　→直近で実行されたステートメントで変更、
         * 　　削除あるいは追加された行の総数を返す
         */
        //echo '更新または削除完了';
    } else {
        //echo '更新または削除失敗しました';
        //データベースのロールバック
        mysqli_rollback($conn);
    }

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