<?php
error_reporting(E_ALL & ~E_NOTICE);
//タイムライン取得API

include_once('./errorMsgs.php');
include_once('./mysqlConnect.php');
include_once('./mysqlClose.php');

$whisperList[] = array(
    "whisperNo" => null,
    "userId" => "",
    "userName" => "",
    "iconPath" => "",
    "postDate" => "",
    "content" => "",
    "imagePath" => "",
    "goodFlg" => null
);

if (!isset($_GET['userId'])) {
    //1,Inputパラメータの必須チェックを行う。
    errorcheck('006');
} else {
    //2.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();

    //フォームリクエストの内容を取得
    $userId = trim(htmlspecialchars($_GET['userId'], ENT_QUOTES, 'UTF-8'));
    /*
      $timelineFrom = trim(htmlspecialchars($_GET['timelineFrom'], ENT_QUOTES, 'UTF-8'));
      $timelineTo = trim(htmlspecialchars($_GET['timelineTo'], ENT_QUOTES, 'UTF-8'));
     */

    //mysqlで使用する特殊文字をエスケープする
    $userId = mysqli_real_escape_string($conn, $userId);
    /*
      $timelineFrom = mysqli_real_escape_string($conn, $timelineFrom);
      $timelineTo = mysqli_real_escape_string($conn, $timelineTo);
     */

    //「%」は検索で使用するため「%」があれば「\%」に置き換える
    $userId = str_replace('%', '\%', $userId);
    /*
      $timelineFrom = str_replace('%', '\%', $timelineFrom);
      $timelineTo = str_replace('%', '\%', $timelineTo);
     */
    //echo $userId /* $timelineFrom, $timelineTo */;

    //３．ささやきリストの内容を取得するSQL文を実行する。
    //SQL文の作成
    $sql1 = "SELECT w.whisperNo, w.userId, userName, iconPath, postDate, content, imagePath, goodFlg "
        . "from follow AS f join whisper AS w ON f.followUserId = w.userId "
        . "join user u ON w.userId = u.userId "
        . "LEFT JOIN(SELECT whisperNo, userId, true AS goodFlg FROM goodInfo WHERE userId = '$userId') g "
        . "ON w.whisperNo = g.whisperNo "
        . "WHERE f.userId = '$userId' "
        . "ORDER BY whisperNo DESC";

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
        mysqli_stmt_bind_result($stmt, $whisperNo, $userId, $userName, $iconPath, $postDate, $content, $imagePath, $goodFlg);
        //４－１．イイねフラグがnull以外の場合はtrueを、それ以外の場合はfalseをセットする。
        //表示処理
        //print $num . "件ヒットしました<br>";
        while (mysqli_stmt_fetch($stmt)) {

            if ($goodFlg != "") {
                $goodFlg = TRUE;
            } else {
                $goodFlg = FALSE;
            }

            //４－２. ささやきリストの連想配列にデータを追加する。
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

    //5．SQL情報をクローズさせる。
    //stmtClose($stmt);
    //6．返却値の連想配列に成功パラメータをセットする。
    $result = array(
        'result' => 'success',
        'whisperList' => $whisperList
    );

    //8．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);

    print $result;

    //7．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqlClose($stmt, $conn);
}
