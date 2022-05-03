<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title></title>
    </head>
    <body>
        <form action="index.php">
            <input type="text">
            <input type="submit">
            <input type="reset">
        </form>
        <?php
        errorcheck($_POST['aa']);

        function errorcheck($errorcode) {
            $errMsg = "";


            if ($errorcode == 001) {
                $errMsg = 'データベース処理が異常終了しました';
            } elseif ($errorcode == 002) {
                //throw new Exception('変更内容がありません');
            } elseif ($errorcode == 003) {
                //throw new Exception('ユーザIDまたはパスワードが違います');
            } elseif ($errorcode == 004) {
                //throw new Exception('対象データが見つかりませんでした');
            } elseif ($errorcode == 005) {
                //throw new Exception('ささやき内容がありません');
            } elseif ($errorcode == 006) {
                //throw new Exception('ユーザIDが指定されていません');
            } elseif ($errorcode == 007) {
                //throw new Exception('パスワードが指定されていません');
            } elseif ($errorcode == 008) {
                //throw new Exception('ささやき管理番号が指定されていません');
            } elseif ($errorcode == 009) {
                //throw new Exception('検索区分が指定されていません');
            } elseif ($errorcode == 010) {
                //throw new Exception('検索文字列が指定されていません');
            } elseif ($errorcode == 011) {
                //throw new Exception('ユーザ名が指定されていません');
            } elseif ($errorcode == 012) {
                //throw new Exception('フォロユーザIDが指定されていません');
            } elseif ($errorcode == 013) {
                //throw new Exception('フォローフラグが指定されていません');
            } elseif ($errorcode == 014) {
                //throw new Exception('イイねフラグが指定されていません');
            } elseif ($errorcode == 015) {
                //throw new Exception('ログインユーザIDが指定されていません');
            } elseif ($errorcode == 016) {
                //throw new Exception('検索区分が不正です');
            }

             return($errMsg);
        }
        ?>
    </body>
</html>
