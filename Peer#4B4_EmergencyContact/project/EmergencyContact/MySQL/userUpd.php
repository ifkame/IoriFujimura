<?php
//安否情報入力or変更
include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');

//DB接続処理を呼び出し、データベースの接続を行う。
$conn = mysqlConnect();

//フォームリクエストの内容を取得
$name = trim(htmlspecialchars($_SESSION['username'], ENT_QUOTES, 'UTF-8'));
$pwd = trim(htmlspecialchars($_SESSION['pwd'], ENT_QUOTES, 'UTF-8'));
$status = trim(htmlspecialchars($_POST['status'], ENT_QUOTES, 'UTF-8'));
$content = trim(htmlspecialchars($_POST['content'], ENT_QUOTES, 'UTF-8'));

//mysqlで使用する特殊文字をエスケープする
$name = mysqli_real_escape_string($conn, $name);
$pwd = mysqli_real_escape_string($conn, $pwd);
$status = mysqli_real_escape_string($conn, $status);
$content = mysqli_real_escape_string($conn, $content);

//「%」は検索で使用するため「%」があれば「\%」に置き換える
$name = str_replace('%', '\%', $name);
$pwd = str_replace('%', '\%', $pwd);
$status = str_replace('%', '\%', $status);
$content = str_replace('%', '\%', $content);
//echo $name.$pwd.$status.$content;

//ユーザデータを更新するSQL文を実行する。
$sql = "UPDATE user 
        SET stId=?, content=? 
        WHERE name=?
        AND password=?";

/*
 * sqlステートメントの実行準備
 * ステートメントハンドルの生成
 */
$stmt = mysqli_prepare($conn, $sql);

//ステートメントに値をバインドする
mysqli_stmt_bind_param($stmt, 'isss', $status, $content, $name, $pwd);
/* mysqli_stmt_bind_param
 * 　→プリペアードステートメントの
 * 　　パラメータマーカーに変数をバインドする
 * 'isss'
 * 　→Integer型 × １ + String型 × ３
 * 　　対応する変数の型
 */

//sql文の実行
mysqli_stmt_execute($stmt);

if (!mysqli_error($conn) == "") { //エラーがあるとき ＝ mysqli_error($conn)に文字列が格納される
    //SQL文の実行結果を受取り、異常終了なら以下の処理を行う。
    //データベースのロールバック命令を実行する。
    mysqli_rollback($conn);
    //対象エラーメッセージをセットしてエラー終了させる。
    errorcheck('001');
} else {
    //データベースのコミット命令を実行する。
    mysqli_commit($conn);
    //返却値の連想配列に成功パラメータをセットする。
    $result = array('result' => 'success');

    //SQL情報をクローズさせる。
    //DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
    //返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);

    //print $result;
    header("Location: ../display.php");
    exit();
}
