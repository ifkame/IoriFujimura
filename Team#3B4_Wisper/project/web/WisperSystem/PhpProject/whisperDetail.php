<?php

//ユーザーささやき情報取得

include_once('./errorMsgs.php');
include_once('./mysqlConnect.php');
include_once('./mysqlClose.php');

if (!isset($_GET['userId'])) {
    //1,Inputパラメータの必須チェックを行う。
    return errorcheck('006');
} else if (!isset($_GET['whisperNo'])) {
    return errorcheck('008');
} else {
    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();
    //3.ユーザデータを挿入するSQL文を実行する。
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


    //SQLのささやき情報を取得するSQLの実行
    $sql = "SELECT w.whisperNo, w.userId, u.userName, u.iconPath, w.postDate, w.content, w.imagePath, g.goodFlg "
            . "from whisper w join user u ON w.userId = u.userId "
            . "          left join (SELECT whisperNo, userId, true AS goodFlg FROM goodInfo WHERE userId = '$userId') g "
            . "                           ON w.userId = u.userId AND w.whisperNo = g.whisperNo "
            . "WHERE w.whisperNo = '$whisperNo' ";
           

    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql);

    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //結果を転送する（件数の取得のため）
    mysqli_stmt_store_result($stmt);

    //件数の取得
    $num = mysqli_stmt_num_rows($stmt);

    //4．データのフェッチを行う。
    if ($num != 0) {
        //結果を変数に入れる
        mysqli_stmt_bind_result($stmt, $whisperNo, $userId, $userName, $iconPath, $postDate, $content, $imagePath, $goodFlg);

        //表示処理
        //print $num . "件ヒットしました<br>";
        while (mysqli_stmt_fetch($stmt)) {
            //10-1.いいねフラグがnull以外のときtrueをそれ以外はfalseをセット
            if ($goodFlg != null) {
                $goodFlg = true;
            } else {
                $goodFlg = false;
            }
        }
    }

    //6．SQL情報をクローズさせる。
    //stmtClose($stmt);

    
    //15．返却値の連想配列に成功パラメータをセットする。
    $result = array(
        'result' => 'success',
        'whisperNo' => $whisperNo,
        'userId' => $userId,
        'userName' => $userName,
        'postDate' => $postDate,
        'content' => $content,
        'goodFlg' => $goodFlg
        );
    
    //9．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);
    $whisperNo = json_encode($whisperNo);
    $userId = json_encode($userId);
    $userName = json_encode($userName);
    $postDate = json_encode($postDate);
    $content = json_encode($content);
    $goodFlg = json_encode($goodFlg);

    print $result;
    

    //8．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
}
?>
