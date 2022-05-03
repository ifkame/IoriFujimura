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
} else {
    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();
    //３．フォローリストを取得するSQL文を実行する。
    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));
    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);
    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $g_userId = str_replace('%', '\%', $userId);

    //echo $userId;
    //SQL文の作成
    $sql1 = "SELECT u.userId, u.userName, u.iconPath, ifnull(wh.cnt, 0) AS whisperCount,ifnull(f1.cnt, 0) AS followCount, ifnull(f2.cnt, 0) AS followerCount "
        . "FROM follow AS f join user AS u ON f.followUserId = u.userId "
        . "left join whisperCntView AS wh ON u.userId = wh.userId "
        . "left join followCntView AS f1 ON u.userId = f1.userId "
        . "left join followerCntView AS f2 ON u.userId = f2.followUserId "
        . "WHERE f.userId = '$g_userId'";

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
        mysqli_stmt_bind_result($stmt, $userId, $userName, $iconPath, $whisperCount, $followCount, $followerCount);
        //４－１．イイねフラグがnull以外の場合はtrueを、それ以外の場合はfalseをセットする。
        //表示処理
        //print $num . "件ヒットしました<br>";
        while (mysqli_stmt_fetch($stmt)) {
            if ($goodFlg != null) {
                $goodFlg = TRUE;
            } else {
                $goodFlg = FALSE;
            }
            //4－２．フォローリストの連想配列にデータを追加する。
            $followList[] = array(
                "userId" => "${userId}",
                "userName" => "${userName}",
                "iconPath" => "${iconPath}",
                "whisperCount" => "${whisperCount}",
                "followCount" => "${followCount}",
                "followerCount" => "${followerCount}"
            );
        }
    }
    //5．SQL情報をクローズさせる。
    stmtClose($stmt);

    //６．フォロワーリストを取得するSQL文を実行する
    $sql2 = "SELECT u.userId, u.userName, u.iconPath, ifnull(wh.cnt, 0) AS whisperCount,ifnull(f1.cnt, 0) AS followCount, ifnull(f2.cnt, 0) AS followerCount "
        . "FROM follow AS f join user AS u ON f.userId = u.userId "
        . "left join whisperCntView AS wh ON u.userId = wh.userId "
        . "left join followCntView AS f1 ON u.userId = f1.userId "
        . "left join followerCntView AS f2 ON u.userId = f2.followUserId "
        . "WHERE f.followUserId = '$g_userId'";

    //SQLステートメントを実行する準備
    $stmt = mysqli_prepare($conn, $sql2);

    //ステートメントを実行
    mysqli_stmt_execute($stmt);

    //結果を転送する（件数の取得のため）
    mysqli_stmt_store_result($stmt);

    //件数の取得
    $num = mysqli_stmt_num_rows($stmt);

    //7．データのフェッチを行う。データが存在しない場合、対象エラーメッセージをセットしてエラー終了させる。
    if ($num != 0) {
        //結果を変数に入れる
        mysqli_stmt_bind_result($stmt, $userId, $userName, $iconPath, $whisperCount, $followCount, $followerCount);
        //7－１．イイねフラグがnull以外の場合はtrueを、それ以外の場合はfalseをセットする。
        //表示処理
        //print $num . "件ヒットしました<br>";
        while (mysqli_stmt_fetch($stmt)) {
            if ($goodFlg != null) {
                $goodFlg = TRUE;
            } else {
                $goodFlg = FALSE;
            }
            //7－２．フォローリストの連想配列にデータを追加する。
            $followerList[] = array(
                "userId" => "${userId}",
                "userName" => "${userName}",
                "iconPath" => "${iconPath}",
                "whisperCount" => "${whisperCount}",
                "followCount" => "${followCount}",
                "followerCount" => "${followerCount}"
            );
        }
    }

    //９．返却値の連想配列に成功パラメータをセットする。
    $result = array(
        'result' => 'success',
        'followList' => $followList,
        'followerList' => $followerList
    );

    //8．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);
    print $result;

    //８．SQL情報をクローズさせる。
    //10．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
}
