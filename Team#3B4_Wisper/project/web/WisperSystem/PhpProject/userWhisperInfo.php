<?php
//サーバー側のエラー非表示
//error_reporting(E_ALL & ~E_NOTICE);
//ローカル側のエラー非表示
ini_set('display_errors', 0);

//ユーザーささやき情報取得

include_once('./errorMsgs.php');
include_once('./mysqlConnect.php');
include_once('./mysqlClose.php');

if (!isset($_GET['userId'])) {
    //1,Inputパラメータの必須チェックを行う。
    return errorcheck('006');
} else if (!isset($_GET['loginUserId'])) {
    return errorcheck('015');
} else {
    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();
    //3.ユーザデータを挿入するSQL文を実行する。
    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));
    $loginUserId = trim(htmlspecialchars($_GET['loginUserId'], ENT_QUOTES, 'UTF-8'));

    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);
    $loginUserId = mysqli_real_escape_string($conn, $loginUserId);

    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $g_userId = str_replace('%', '\%', $userId);
    $loginUserId = str_replace('%', '\%', $loginUserId);
    //echo $userId . $loginUserId;

    //SQL文の作成
    $sql1 = "SELECT u.userName, u.profile, u.iconPath, ifnull(fa.cnt, 0) AS followCount, ifnull(fb.cnt, 0) AS followerCount "
        . "FROM user AS u left join followCntView AS fa ON u.userId = fa.userId "
        . "left join followerCntView AS fb ON u.userId = fb.followUserId "
        . "WHERE u.userId = '$g_userId'";

    //echo $sql1;
    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql1);

    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //結果を転送する（件数の取得のため）
    mysqli_stmt_store_result($stmt);

    //件数の取得
    $num = mysqli_stmt_num_rows($stmt);

    //4．データのフェッチを行う。データが存在しない場合、対象エラーメッセージをセットしてエラー終了させる。
    if ($num != 0) {
        //結果を変数に入れる
        mysqli_stmt_bind_result($stmt, $myUserName, $myProfile, $myIconPath, $myFollowCount, $myFollowerCount);

        //表示処理
        //print $num . "件ヒットしました<br>";
        while (mysqli_stmt_fetch($stmt)) {
            if ($num == 0) {
                errorcheck('004');
            }
        }
    }

    //5．SQL情報をクローズさせる。
    stmtClose($stmt);

    //6.フォロー中の情報
    $sql2 = "SELECT count(*) AS cnt from follow WHERE userId = '$loginUserId' AND followUserId = '$g_userId'";

    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql2);

    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //結果を転送する（件数の取得のため）
    mysqli_stmt_store_result($stmt);

    //件数の取得
    $num = mysqli_stmt_num_rows($stmt);

    //7．データのフェッチを行う。
    //データが存在しない場合、対象エラーメッセージをセットしてエラー終了させる。		
    //【エラーコード】004		
    if ($num != 0) {
        //結果を変数に入れる
        mysqli_stmt_bind_result($stmt, $cnt);


        //表示処理
        //print $num . "件ヒットしました<br>";
        while (mysqli_stmt_fetch($stmt)) {
            if ($cnt != 0) {
                $userFollowFlg = true;
            } else {
                $userFollowFlg = false;
            }
            if ($num == 0) {
                errorcheck('004');
            }
        }
    }

    //8．SQL情報をクローズさせる。
    stmtClose($stmt);

    //9.SQLのささやきリストを取得するSQLの実行
    $sql3 = "SELECT w.whisperNo, w.userId, u.userName, u.iconPath, w.postDate, w.content, w.imagePath, g.goodFlg "
        . "from whisper w join user u ON w.userId = u.userId "
        . "left join (SELECT whisperNo, userId, true AS goodFlg FROM goodInfo WHERE userId = '$loginUserId') g "
        . "ON w.userId = u.userId AND w.whisperNo = g.whisperNo "
        . "WHERE w.userId = '$g_userId' "
        . "ORDER BY w.whisperNo DESC";

    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql3);

    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //結果を転送する（件数の取得のため）
    mysqli_stmt_store_result($stmt);

    //件数の取得
    $num = mysqli_stmt_num_rows($stmt);

    $goodList = null;

    //10．データのフェッチを行う。
    if ($num != 0) {
        //結果を変数に入れる
        mysqli_stmt_bind_result($stmt, $whisperNo, $userId, $userName, $iconPath, $postDate, $content, $imagePath, $goodFlg);

        //表示処理
        //print $num . "件ヒットしました<br>";
        while (mysqli_stmt_fetch($stmt)) {
            //10-1.いいねフラグがnull以外のときtrueをそれ以外はfalseをセット
            if ($goodFlg != "") {
                $goodFlg = TRUE;
            } else {
                $goodFlg = FALSE;
            }

            $whisperNo = intval($whisperNo);

            //10-2.いいねリストの連想配列にデータを追加
            $whisperList[] = array(
                "whisperNo" => $whisperNo,
                "userId" => "${userId}",
                "userName" => "${userName}",
                "iconPath" => "${iconPath}",
                "postDate" => "${postDate}",
                "content" => "${content}",
                "imagePath" => "${imagePath}",
                "goodFlg" => $goodFlg
            );
        }
    }

    //11．SQL情報をクローズさせる。
    stmtClose($stmt);

    //12.SQLのささやきリストを取得するSQLの実行
    // $sql4 = "SELECT w.whisperNo, w.userId, userName, iconPath, postDate, content, imagePath, goodFlg "
    //     . "from goodInfo g join whisper w ON g.userId = w.userId "
    //     . "join user u ON g.userId = u.userId "
    //     . "left join (SELECT whisperNo, userId, true AS goodFlg FROM goodInfo WHERE userId = '$g_userId') wk "
    //     . "ON g.whisperNo = w.whisperNo AND w.userId = u.userId AND w.whisperNo = wk.whisperNo "
    //     . "WHERE w.userId = '$g_userId' "
    //     . "ORDER BY whisperNo DESC";
    
        $sql4 = "SELECT w.whisperNo, w.userId, userName, iconPath, "
		.              "postDate, content, imagePath, goodFlg "
        .       "FROM goodInfo g join whisper w ON g.whisperNo = w.whisperNo "
		.                       "join user u ON w.userId = u.userId "
		.                       "left join (SELECT whisperNo, userId, true AS goodFlg "
		.                                  "FROM goodInfo WHERE userId = '$loginUserId') wk "
		.                       "ON w.whisperNo = wk.whisperNo "
        .       "WHERE g.userId = '$g_userId' "
        .       "ORDER BY whisperNo DESC";

    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql4);

    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //結果を転送する（件数の取得のため）
    mysqli_stmt_store_result($stmt);

    //件数の取得
    $num = mysqli_stmt_num_rows($stmt);

    //13．データのフェッチを行う。
    if ($num != 0) {
        //結果を変数に入れる
        mysqli_stmt_bind_result($stmt, $whisperNo, $goodUserId, $goodUserName, $iconPath, $postDate, $content, $imagePath, $goodFlg);

        //表示処理
        //print $num . "件ヒットしました<br>";
        while (mysqli_stmt_fetch($stmt)) {

            //13-1.いいねフラグがnull以外のときtrueをそれ以外はfalseをセット
            if ($goodFlg != "") {
                $goodFlg = TRUE;
            } else {
                $goodFlg = FALSE;
            }
            //13-2.いいねリストの連想配列にデータを追加
            $goodList[] = array(
                "whisperNo" => $whisperNo,
                "userId" => "${goodUserId}",
                "userName" => "${goodUserName}",
                "iconPath" => "${iconPath}",
                "postDate" => "${postDate}",
                "content" => "${content}",
                "imagePath" => "${imagePath}",
                "goodFlg" => $goodFlg
            );
        }
    }

    //14．SQL情報をクローズさせる。
    //stmtClose($stmt);
    //15．返却値の連想配列に成功パラメータをセットする。
    $result = array(
        'result' => 'success',
        'userId' => $g_userId,
        'userName' => $myUserName,
        'profile' => "${myProfile}",
        'iconPath' => "${myIconPath}",
        'userFollowFlg' => $userFollowFlg,
        'followCount' => $myFollowCount,
        'followerCount' => $myFollowerCount,
        'whisperList' => $whisperList,
        'goodList' => $goodList
    );
    /* $result = array('result' => 'success');
      $userId = array('userId' => $userId);
      $userName = array('userName' => $userName);
      $profile = array('profile' => $profile);
      $userFollowFlg = array('userFollowFlg' => $userFollowFlg);
      $followCount = array('followCount' => $followCount);
      $followerCount = array('followerCount' => $followerCount);
      $whisperList = array('whisperList' => $whisperList);
      $goodList = array('goodList' => $goodList); */

    //17．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);
    /*
    $userId = json_encode($userId);
    $userName = json_encode($userName);
    $profile = json_encode($profile);
    $userFollowFlg = json_encode($userFollowFlg);
    $followCount = json_encode($followCount);
    $followerCount = json_encode($followerCount);
    $whisperList = json_encode($whisperList);
    $goodList = json_encode($goodList);
    */
    print $result;

    //16．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
}
