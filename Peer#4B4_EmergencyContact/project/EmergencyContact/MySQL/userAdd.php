<?php
//ユーザ作成処理
include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');

function userAdd()
{
    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();
    //3.ユーザデータを挿入するSQL文を実行する。
    //フォームリクエストの内容を取得
    $userName = trim(htmlspecialchars($_POST['name'], ENT_QUOTES, 'UTF-8'));
    $pwd = trim(htmlspecialchars($_POST['pwd'], ENT_QUOTES, 'UTF-8'));
    $mail = trim(htmlspecialchars($_POST['email'], ENT_QUOTES, 'UTF-8'));
    $postId = trim(htmlspecialchars($_POST['position'], ENT_QUOTES, 'UTF-8'));

    //mysqlで使用する特殊文字をエスケープする
    $userName = mysqli_real_escape_string($conn, $userName);
    $pwd = mysqli_real_escape_string($conn, $pwd);
    $mail = mysqli_real_escape_string($conn, $mail);
    $postId = mysqli_real_escape_string($conn, $postId);

    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userName = str_replace('%', '\%', $userName);
    $pwd = str_replace('%', '\%', $pwd);
    $mail = str_replace('%', '\%', $mail);
    $postId = str_replace('%', '\%', $postId);
    //echo '<br>' . $userName . '<br>' . $pwd . '<br>' . $mail . '<br>' . $postId . '<br>';

    //SQL文の作成
    $sql = "INSERT INTO user(name, password, mail, postId) VALUES(?,?,?,?)";

    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql);

    //SQLステートメントと値をバインドする
    mysqli_stmt_bind_param($stmt, 'sssi', $userName, $pwd, $mail, $postId);

    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    var_dump($stmt);

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
        //print $result;

        // リダイレクトを実行
        header("Location: ../login.php");
        exit();
    }
}

userAdd();
