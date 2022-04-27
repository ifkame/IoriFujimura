<?php
include('MySQL/userInfo.php');
$userList = userInfo();
// var_dump($userList);
// print $userList["position"];
// print $userList["status"];
?>

<script type="text/javascript">
    window.onload = function() {
        //「役職」選択リストの選択
        const position = document.getElementById('post').getElementsByClassName('selflg');
        console.log(position);
        switch (<?php echo $userList["position"]; ?>) {
            case 1:
                position[0].selected = true;
                break;
            case 2:
                position[1].selected = true;
                break;
            case 3:
                position[2].selected = true;
                break;
            case 4:
                position[3].selected = true;
                break;
            case 5:
                position[4].selected = true;
                break;
            case 6:
                position[5].selected = true;
                break;
            case 7:
                position[6].selected = true;
                break;
            case 8:
                position[7].selected = true;
                break;
            case 9:
                position[8].selected = true;
                break;
            case 10:
                position[9].selected = true;
                break;
            case 11:
                position[10].selected = true;
                break;
        }
        //「安否情報」選択リストの選択
        const status = document.getElementById('status').getElementsByClassName('selflg');
        switch (<?php echo $userList["status"]; ?>) {
            case 1:
                status[0].selected = true;
                break;
            case 2:
                status[1].selected = true;
                break;
            case 3:
                status[2].selected = true;
                break;
            default:
        }
    }
</script>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <title>緊急連絡先掲示板/input</title>
</head>

<body>
    <h1 style="display:inline;">inputページ</h1>
    <form action="MySQL/userUpd.php" method="POST">
        <table>
            <tr>
                <th>お名前（必須）</th>
                <td><input type="text" name="name" value="<?= $userList["name"] ?>" disabled></td>
            </tr>

            <tr>
                <th>役職（必須）</th>
                <td>
                    <select id="post" name="position" disabled>
                        <option value="1" class="selflg">代表取締役社長</option>
                        <option value="2" class="selflg">専務取締役</option>
                        <option value="3" class="selflg">常務取締役</option>
                        <option value="4" class="selflg">本部長（事業部長）</option>
                        <option value="5" class="selflg">部長</option>
                        <option value="6" class="selflg">次長</option>
                        <option value="7" class="selflg">課長</option>
                        <option value="8" class="selflg">係長</option>
                        <option value="9" class="selflg">主任</option>
                        <option value="10" class="selflg">一般社員</option>
                        <option value="11" class="selflg">その他</option>
                    </select>
                </td>
            </tr>

            <tr>
                <th>メールアドレス（必須）</th>
                <td>
                    <input type="text" name="email" value="<?= $userList["mail"] ?>" disabled>
                </td>
            </tr>

            <!-- <tr>
                <th>パスワード（必須）</th>
                <td><input type="text" name="pwd" value="<?= $userList["pwd"] ?>" disabled></td>
            </tr> -->

            <tr>
                <th>安否情報</th>
                <td>
                    <select id="status" name="status" required>
                        <option value="">未確認</option>
                        <option value="1" class="selflg">無事</option>
                        <option value="2" class="selflg">軽傷</option>
                        <option value="3" class="selflg">重傷</option>
                    </select>
                </td>
            </tr>

            <tr>
                <th>備考</th>
                <td>
                    <textarea name="content" rows="4" cols="50" placeholder="避難先や伝えたいことがあれば入力ください"><?php echo trim($userList["content"]); ?></textarea>
                </td>
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