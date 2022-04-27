<?php
//ログイン認証
include('errorMsgs.php');
include('mysqlConnect.php');
include('mysqlClose.php');

function userInfo()
{
    $s_name = $_SESSION['username'];
    $s_pwd = $_SESSION['pwd'];
    
    // print $s_name . $s_pwd;
    if (!isset($s_name) || $s_name == trim("")) {
        //Inputパラメータの必須チェックを行う。
        errorcheck('006');
    } else if (!isset($s_pwd) || $s_pwd == trim("")) {
        errorcheck('007');
    } else {
        //DB接続処理を呼び出し、データベースの接続を行う。
        $conn = mysqlConnect();
        //SQL文の作成
        $sql = "SELECT name, password, mail, 
                    postId, stId, content
                    FROM user
                    WHERE name = '${s_name}'
                    AND password = '${s_pwd}'";

        //SQLステートメントを実行する準備
        $stmt = mysqli_prepare($conn, $sql);

        //ステートメントを実行
        mysqli_stmt_execute($stmt);

        //結果を転送する（件数の取得のため）
        mysqli_stmt_store_result($stmt);

        //件数の取得
        $num = mysqli_stmt_num_rows($stmt);

        //結果を変数に入れる
        mysqli_stmt_bind_result($stmt, $name, $pwd, $mail, $position, $status, $content);
        while (mysqli_stmt_fetch($stmt)) {
            //ユーザーリストの連想配列にデータを追加する。
            $userList = array(
                "name" => "${name}",
                "pwd" => "${pwd}",
                "mail" => "${mail}",
                "position" => $position,
                "status" => $status,
                "content" => "${content}",
            );
        }
        //SQL情報をクローズさせる。
        //DB切断処理を呼び出し、データベースの接続を解除する。
        mysqlClose($stmt, $conn);

        //ユーザー情報
        //$userList = json_encode($userList); //返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
        //print $userList;
        return $userList;
    }
}
//userInfo();