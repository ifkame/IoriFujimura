<script type="text/javascript">
    window.onload = function() {
        <?php
        //include('./MySQL/userAdd.php');

        /**
         * 
         * POSTデータへのアクセスは、filter_inputを使用するべきでしょうが、
         * $_POSTなどのスーパーグローバル変数をまずは、覚えてもらいたいので、
         * $_POSTの連想配列要素へアクセスする方法で進めています。
         * 
         */

        /**
         * デバックコードです。
         */
        // print "<pre>";
        // print_r($_POST);
        // print "</pre>";

        // htmlspechialcharsで使用する文字エンコード
        $charset = "UTF-8";

        // メールアドレスのパターン
        // 「@」があるかくらいの経験が多いです。
        $pattern = "/@/";

        // POSTデータを保存する配列
        $postData = [];
        // 出力データを保存する配列
        $viewData = [];

        //ボタン表示フラグ
        $btnFlag = true;
        /**
         * 入力チェックはissetで、各POSTデータの存在有無チェックだけでも構いません。
         * HTMLの各表示部分で、条件分岐などの処理を記載していただいても構いません。
         * その際は、if(): endifのような{}を使わない記述構文で記載するように勧めてください。
         */

        //
        // お名前
        //
        if (isset($_POST["name"])) {

            $postData["name"] = trim($_POST["name"]);

            if ($postData["name"] !== "") {
                $view["name"] = htmlspecialchars($postData["name"], ENT_QUOTES, $charset);
            } else {
                $view["name"] = "お名前が入力されていません";
                $btnFlag = false;
            }
        }

        //
        // パスワード
        //
        if (isset($_POST["pwd"])) {

            $postData["pwd"] = trim($_POST["pwd"]);

            if ($postData["pwd"] !== "") {
                $view["pwd"] = htmlspecialchars($postData["pwd"], ENT_QUOTES, $charset);
            } else {
                $view["pwd"] = "パスワードが入力されていません";
                $btnFlag = false;
            }
        }

        //
        // メールアドレス
        //
        if (isset($_POST["email"])) {

            $postData["email"] = trim($_POST["email"]);

            if ($postData["email"] !== "") {

                if (preg_match($pattern, $postData["email"])) {
                    $view["email"] = htmlspecialchars($postData["email"], ENT_QUOTES, $charset);
                } else {
                    $view["email"] = "メールアドレスが正しくありません";
                    $btnFlag = false;
                }
            } else {
                $view["email"] = "メールアドレスが入力されていません";
                $btnFlag = false;
            }
        }

        //
        // 役職の種類
        //
        if (isset($_POST["position"])) {

            $postData["position"] = (int)trim($_POST["position"]);

            switch ($postData["position"]) {
                case 1:
                    $view["position"] = "代表取締役社長";
                    break;
                case 2:
                    $view["position"] = "専務取締役";
                    break;
                case 3:
                    $view["position"] = "常務取締役";
                    break;
                case 4:
                    $view["position"] = "本部長（事業部長）";
                    break;
                case 5:
                    $view["position"] = "部長";
                    break;
                case 6:
                    $view["position"] = "次長";
                    break;
                case 7:
                    $view["position"] = "課長";
                    break;
                case 8:
                    $view["position"] = "係長";
                    break;
                case 9:
                    $view["position"] = "主任";
                    break;
                case 10:
                    $view["position"] = "一般社員";
                    break;
                case 11:
                    $view["position"] = "その他";
                    break;
                default:
                    $view["position"] = "役職が選ばれていません";
                    $btnFlag = false;
            }
        } else {
            $view["position"] = "役職が選ばれていません";
            $btnFlag = false;
        }

        if ($btnFlag) {
        ?>
            document.getElementById('back').style.display = 'none';
            document.getElementById('login').style.display = 'block';
        <?php } else { ?>
            document.getElementById('back').style.display = 'block';
            document.getElementById('login').style.display = 'none';
        <?php } ?>
    }
</script>

<!DOCTYPE html>
<html lang="ja">

<head>
    <!-- meta -->
    <meta charset="UTF-8">

    <title>緊急連絡先掲示板/create-end</title>
</head>

<body>
    <h1>create確認ページ</h1>

    <table>
        <tr>
            <th>お名前（必須）</th>
            <td><?= $view["name"] ?></td>
        </tr>
        <tr>
            <th>役職（必須）</th>
            <td><?= $view["position"] ?></td>
        </tr>
        <tr>
            <th>メールアドレス（必須）</th>
            <td><?= $view["email"] ?></td>
        </tr>

        <tr>
            <th>パスワード（必須）</th>
            <td><?= $view["pwd"] ?></td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="button" onclick="location.href='./create.php'" id="back" style="float: right;">戻る</button>
                <form action="MySQL/userAdd.php" method="POST">
                    <input type="hidden" name="name" value="<?= $view["name"] ?>">
                    <input type="hidden" name="position" value="<?= $postData["position"] ?>">
                    <input type="hidden" name="email" value="<?= $view["email"] ?>">
                    <input type="hidden" name="pwd" value="<?= $view["pwd"] ?>">
                    <button type="submit" id="login" style="float: right;">ログインへ</button>
                </form>
            </td>

        </tr>
    </table>

</body>

</html>