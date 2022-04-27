<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">

    <title>緊急連絡先掲示板/create</title>

</head>

<body>

    <h1>createページ</h1>
    <form action="create-end.php" method="POST">

        <table>
            <tr>
                <th>お名前（必須）</th>
                <td><input type="text" name="name" value="" placeholder="ECC 太郎" required></td>
            </tr>

            <tr>
                <th>役職（必須）</th>
                <td>
                    <select name="position"  required>
                        <option value="">選択してください</option>
                        <option value="1">代表取締役社長</option>
                        <option value="2">専務取締役</option>
                        <option value="3">常務取締役</option>
                        <option value="4">本部長（事業部長）</option>
                        <option value="5">部長</option>
                        <option value="6">次長</option>
                        <option value="7">課長</option>
                        <option value="8">係長</option>
                        <option value="9">主任</option>
                        <option value="10">一般社員</option>
                        <option value="11">その他</option>
                    </select>
                </td>
            </tr>

            <tr>
                <th>メールアドレス（必須）</th>
                <td>
                    <input type="text" name="email" value="" placeholder="example@ecccomp.com" required>
                </td>
            </tr>

            <tr>
                <th>パスワード（必須）</th>
                <td><input type="password" name="pwd" value="" placeholder="password" required></td>
            </tr>

            <tr>
                <td colspan="2" style="text-align: right;">
                    <button type="submit">送信する</button>
                </td>
            </tr>
        </table>

    </form>

</body>

</html>