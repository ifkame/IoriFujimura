<?php
//サーバー側のエラー非表示
//error_reporting(E_ALL & ~E_NOTICE);
//ローカル側のエラー非表示
ini_set('display_errors', 0);

//検索結果取得
include('./errorMsgs.php');
include('./mysqlConnect.php');
include('./mysqlClose.php');

// 配列の初期化
if ($_GET['section'] == 1) {
    $whisperList[] = array("Data" => "Nothing");
} elseif ($_GET['section'] == 2) {
    $userList[] = array("Data" => "Nothing");
}

//1,Inputパラメータの必須チェックを行う
if (!isset($_GET['section'])) {
    errorcheck('009');
} else if (!isset($_GET['string'])) {
    errorcheck('010');
} else {

    //2．検索区分の整合性チェックを行う。
    //検索区分が１または２以外の場合、対象エラーメッセージをセットしてエラー終了させる。
    if ($_GET['section'] != '1' && $_GET['section'] != '2') {
        errorcheck('016');
    }
    //4.DB接続処理を呼び出し、データベースの接続を行う。
    $conn = mysqlConnect();

    //３．検索文字列の前後に%の文字を追加する
    //タイトルの内容があって、空白でないときの処理
    if (isset($_GET['string']) && ($_GET['string'] != '')) {

        //タイトルを取得する
        $string = trim(htmlspecialchars($_GET['string'], ENT_QUOTES, 'UTF-8'));
        $section = trim(htmlspecialchars($_GET['section'], ENT_QUOTES, 'UTF-8'));

        //mysqliで使用する特殊文字をエスケープする
        $string = mysqli_real_escape_string($conn, $string);
        $section = mysqli_real_escape_string($conn, $section);

        //「%」は検索で使用するため「%」があれば「\%」に置き換える
        $string = str_replace("%", "\%", $string);
        $section = str_replace("%", "\%", $section);

        //検索条件を作成
        $psql = "LIKE '%${string}%'";
    }

    //５．検索区分が１(ユーザ検索)の場合、以下の処理を行う。
    if ($section == '1') {
        //５－１．ユーザリストを取得するSQL文を実行する。
        //SQL文の作成
        //wはささやき件数ビュー,f1はフォロー件数ビュー,f2はフォロワー件数ビュー
        $sql1 = "SELECT u.userId, u.userName, u.iconPath, IFNULL(w.cnt,0) AS whisperCount, IFNULL(f1.cnt,0) AS followCount, IFNULL(f2.cnt,0) AS followerCount "
                . "FROM user u LEFT OUTER JOIN whisperCntView w ON u.userId = w.userId "
                . "LEFT OUTER JOIN followCntView f1 ON u.userId = f1.userId "
                . "LEFT OUTER JOIN followerCntView f2 ON u.userId = f2.followUserId "
                . "WHERE u.userId $psql OR u.userName $psql";

        /*
         * sqlステートメントの実行準備
         * ステートメントハンドルの生成
         */
        $stmt = mysqli_prepare($conn, $sql1);

        //sqlステートメントの実行
        mysqli_stmt_execute($stmt);

        //結果を転送する(件数の取得のため)
        mysqli_stmt_store_result($stmt);

        //件数の取得
        $num = mysqli_stmt_num_rows($stmt);

        //５－２．データのフェッチを行い、検索結果のデータがある間以下の処理を繰り返す。
        if ($num != 0) {
            //結果を変数に入れる
            mysqli_stmt_bind_result($stmt, $userId, $userName, $iconPath, $whisperCount, $followCount, $followerCount);

            while (mysqli_stmt_fetch($stmt)) {
                //５－２－１．ユーザリストの連想配列にデータを追加する。
                $userList[] = array(
                    "userId" => "{$userId}",
                    "userName" => "{$userName}",
                    "icon" => "{$iconPath}",
                    "whisperCount" => $whisperCount,
                    "followCount" => $followCount,
                    "followerCount" => $followerCount
                );
            }
        } else {
            //$userList[] = array("Data" => "Nothing");
        }
        //５－３．SQL情報をクローズさせる。
        stmtClose($stmt);
    }

    if ($section == '2') {
        //６－１．ささやきリストを取得するSQL文を実行する。
        //SQL文の作成
        $sql2 = "SELECT g.whisperNo,u.userId,u.userName,u.iconPath,w.postDate,w.content,w.imagePath,IFNULL(g.cnt,0) AS goodCount "
                . "FROM whisper w JOIN user u ON w.userId = u.userId "
                . "LEFT OUTER JOIN goodCntView g ON w.whisperNo = g.whisperNo WHERE w.content $psql";

        /*
         * sqlステートメントの実行準備
         * ステートメントハンドルの生成
         */
        $stmt = mysqli_prepare($conn, $sql2);

        //sqlステートメントの実行
        mysqli_stmt_execute($stmt);

        //結果を転送する(件数の取得のため)
        mysqli_stmt_store_result($stmt);

        //件数の取得
        $num = mysqli_stmt_num_rows($stmt);

        //６－２．データのフェッチを行い、検索結果のデータがある間以下の処理を繰り返す。
        if ($num != 0) {
            //結果を変数に入れる
            mysqli_stmt_bind_result($stmt, $whisperNo, $userId, $userName, $iconPath, $postDate, $content, $imagePath, $goodCount);
            //６－２－１．ささやきリストの連想配列にデータを追加する。
            while (mysqli_stmt_fetch($stmt)) {
                $whisperList[] = array(
                    "whisperNo" => $whisperNo,
                    "userId" => "${userId}",
                    "userName" => "${userName}",
                    "icon" => "${iconPath}",
                    "postDate" => "${postDate}",
                    "content" => "${content}",
                    "imagePath" => "${imagePath}",
                    "goodCount" => $goodCount
                );
            }
        } else {
            $whisperList[] = array("Data" => "Nothing");
        }
        //６－３．SQL情報をクローズさせる。
        stmtClose($stmt);
    }



    //７．返却値の連想配列に成功パラメータをセットする。
    $result = array(
        'result' => 'success',
        'userList' => $userList,
        'whisperList' => $whisperList
    );

    //８．DB切断処理を呼び出し、データベースの接続を解除する。
    mysqli_close($conn);

    //９．返却値の連想配列をJSONにエンコードしてoutputパラメータを出力する。
    $result = json_encode($result);

    print $result;
}