<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <title>緊急連絡先掲示板/login</title>
</head>

<body>

    <h1>loginページ</h1>
    <form action="MySQL/loginAuth.php" method="POST">

        <table>
            <tr>
                <th>お名前</th>
                <td><input type="text" name="name" value="" placeholder="ECC 太郎" required></td>
            </tr>

            <tr>
                <th>パスワード</th>
                <td>
                    <input type="password" name="pwd" value="" placeholder="password" required>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <button type="submit" style="float: right;">送信する</button>
                </td>
            </tr>
        </table>

    </form>

</body>

</html>