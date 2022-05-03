<?php
//サーバー側のエラー非表示
//error_reporting(E_ALL & ~E_NOTICE);
//ローカル側のエラー非表示
ini_set('display_errors', 0);

//フォロー管理処理

include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');

$followFlg = $_GET['followFlg'];
// print $followFlg;

//1,Inputパラメータの必須チェックを行う
if (!isset($_GET['userId'])) {
    return errorcheck('006');
} else if (!isset($_GET['followUserId'])) {
    return errorcheck('012');
} else if (!isset($_GET['followFlg'])) {
    return errorcheck('013');
} else {

    //２．DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();

    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));
    $followUserId = trim(htmlspecialchars($_GET['followUserId'], ENT_QUOTES, 'UTF-8'));

    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);
    $followUserId = mysqli_real_escape_string($conn, $followUserId);

    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userId = str_replace('%', '\%', $userId);
    $followUserId = str_replace('%', '\%', $followUserId);
    // echo $userId . $followUserId;

    //３．フォローフラグがtrue(フォローする)の場合、以下の処理を行う。
    //３－１．フォローデータを挿入するSQL文を実行する。
    if ($followFlg == "true" || $followFlg == 1) {
        //sql文の作成
        $sql = "INSERT INTO follow(userId,followUserId) values(?, ?)";
        //print 'INSERTが行われました';
        
        //４．フォローフラグがfalse(フォロー外す)の場合、以下の処理を行う。
        //４－１．フォローデータを削除するSQL文を実行する。
    } else {
        //sql文の作成
        $sql = "DELETE FROM follow WHERE userId = ? and followUserId = ?";
        //print 'DELETEが行われました';
    }

    // ↓ sql文以降は同じ処理だからまとめました
    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql);
    //SQLステートメントと値をバインドする
    mysqli_stmt_bind_param($stmt, 'ss', $userId, $followUserId);
    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //追加した行が格納されていなければ MySQL を終了
    if (mysqli_stmt_affected_rows($stmt)) {
        /* mysqli_stmt_affected_rows
         * 　→直近で実行されたステートメントで変更、
         * 　　削除あるいは追加された行の総数を返す
         */
        // echo '追加または削除完了';
    } else {
        // echo '追加または削除失敗しました';
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

