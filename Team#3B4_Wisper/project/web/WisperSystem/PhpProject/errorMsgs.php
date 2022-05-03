<?php

//エラー返却処理
function errorcheck($errCode) {
    
    //APIエラーコード一覧
    if ($errCode === '001') {
        $errMsg = 'データベース処理が異常終了しました';
    } elseif ($errCode === '002') {
        $errMsg = '変更内容がありません';
    } elseif ($errCode === '003') {
        $errMsg = 'ユーザIDが指定されていません';
    } elseif ($errCode === '004') {
        $errMsg = '対象データが見つかりませんでした';
    } elseif ($errCode === '005') {
        $errMsg = 'ささやき内容がありません';
    } elseif ($errCode === '006') {
        $errMsg = 'ユーザIDが指定されていません';
    } elseif ($errCode === '007') {
        $errMsg = 'パスワードが指定されていません';
    } elseif ($errCode === '008') {
        $errMsg = 'ささやき管理番号が指定されていません';
    } elseif ($errCode === '009') {
        $errMsg = '検索区分が指定されていません';
    } elseif ($errCode === '010') {
        $errMsg = '検索文字列が指定されていません';
    } elseif ($errCode === '011') {
        $errMsg = 'ユーザ名が指定されていません';
    } elseif ($errCode === '012') {
        $errMsg = 'フォローユーザIDが指定されていません';
    } elseif ($errCode === '013') {
        $errMsg = 'フォローフラグが指定されていません';
    } elseif ($errCode === '014') {
        $errMsg = 'いいねフラグが指定されていません';
    } elseif ($errCode === '015') {
        $errMsg = 'ログインユーザIDが指定されていません';
    } elseif ($errCode === '016') {
        $errMsg = '検索区分が不正です';
    }

    //返却値の連想配列に失敗パラメータとエラーコード、エラーメッセージをセットする。
    $result = [
        'result' =>'error',
        'errCode'=> $errCode,
        'errMsg'=> $errMsg
    ];

    //返却値の連想配列をJSONにエンコードして
    //outputパラメータを出力する。
    $jsonstr = json_encode($result,JSON_UNESCAPED_UNICODE);
    echo $jsonstr;
    
    //phpの終了命令を実行し処理を中断する
    exit;
}
?>