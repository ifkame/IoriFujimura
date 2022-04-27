<?php

//ユーザー情報の取得
function userAll()
{
    //ログイン認証
    include('errorMsgs.php');
    include('mysqlConnect.php');
    include('mysqlClose.php');

    //print $_SESSION['username'] . $_SESSION['pwd'];
    // if (!isset($_SESSION['username']) || $_SESSION['username'] == trim("")) {
    //     //Inputパラメータの必須チェックを行う。
    //     errorcheck('006');
    // } else if (!isset($_SESSION['pwd']) || $_SESSION['pwd'] == trim("")) {
    //     errorcheck('007');
    // } else {}

    //DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();
    //SQL文の作成
    $sql = "SELECT name, mail, p.position, 
    s.status, content, checkDate 
    FROM user u, post p, status s
    WHERE u.postId = p.postId
    AND u.stId = s.stId;";

    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql);

    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //結果を転送する（件数の取得のため）
    mysqli_stmt_store_result($stmt);

    //件数の取得
    $num = mysqli_stmt_num_rows($stmt);

    //結果を変数に入れる
    mysqli_stmt_bind_result($stmt, $name, $mail, $position, $status, $content, $checkDate);
    while (mysqli_stmt_fetch($stmt)) {
        //ユーザーリストの連想配列にデータを追加する。
        $userList[] = array(
            "name" => "${name}",
            "mail" => "${mail}",
            "position" => "${position}",
            "status" => "${status}",
            "content" => "${content}",
            "checkDate" => "${checkDate}"
        );
    }

    //返却値の連想配列に成功パラメータをセットする。
    $result = array('result' => 'success');
    //SQL情報をクローズさせる。
    //DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
    //返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);
    //print $result;

    //ユーザー情報
    //$userList = json_encode($userList);
    return $userList;
}

//ユーザー情報表示の処理
function userTable($userList)
{
    $table = "";
    foreach ($userList as $user) {
        $table .= "<tr>";
        $table .= "<td>" . $user['name'] . "</td>";
        $table .= "<td>" . $user['position'] . "</td>";
        if ($user['status'] == "未確認") {
            $table .= "<td style='background-color: #dcdcdc;'>";
        } else if ($user['status'] == "無事") {
            $table .= "<td style='background-color: #98fb98;'>";
        } else if ($user['status'] == "軽傷") {
            $table .= "<td style='background-color: #f0e68c;'>";
        } else if ($user['status'] == "重傷") {
            $table .= "<td style='background-color: #ffe4e1;'>";
        } else {
            $table .= "<td>";
        }
        $table .= $user['status'] . "</td>";
        $table .= "<td>" . $user['content'] . "</td>";
        $table .= "<td>" . $user['mail'] . "</td>";
        $table .= "<td>" . $user['checkDate'] . "</td>";
        $table .= "</tr>";
    }
    return $table;
}

//userAll();
