<?php include('./MySQL/userAll.php'); ?>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <title>緊急連絡先掲示板/display</title>
</head>

<body>
    <h1 style="display:inline;">displayページ</h1>
    <span>
        <h2 style="display:inline;">「ようこそ<?= $_SESSION['username'] ?>さん」</h2>
    </span>
    <button onclick="location.href='./logout.php'" style="float: right;" id="logout">ログアウト</button><br>
    <a href="input.php"><?= $_SESSION['username'] ?>さんの安否情報変更</a><br>
    <a href="javascript:history.back()">戻る</a>

    <table border="1">
        <tr style="background-color: #7fffd4;">
            <th>名前</th>
            <th>役職</th>
            <th>安否状況</th>
            <th>備考</th>
            <th>メールアドレス</th>
            <th>更新日時</th>
        </tr>
        <?php echo userTable(userAll()); ?>
    </table>
</body>

</html>