//盤面の状態：number<なし:0,〇:1,×:2,△:3,◇:4>
var BoardList = [
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から１番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から２番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から３番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から４番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から５番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から６番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から７番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から８番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],     //上から９番目
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]      //上から１０番目
];

//縦横斜め：boolean
var PlayerDirections = [
    [false, false, false],    //プレイヤー１
    [false, false, false],    //プレイヤー２
    [false, false, false],    //プレイヤー３
    [false, false, false]     //プレイヤー４
];

//プレイヤーの得点：number<左から、プレイヤー１, 1：プレイヤー2, 2：プレイヤー3, 3：プレイヤー4>
var PlayerScore = [0, 0, 0, 0];
//プレイヤーのHTML要素の位置：number<0：プレイヤー１, 2：プレイヤー2, 3：プレイヤー3, 1：プレイヤー4>
var PlayerPlace = [0, 2, 3, 1];

//どのプレイヤーのターンか：number<0：プレイヤー１, 1：プレイヤー2, 2：プレイヤー3, 3：プレイヤー4>
var player = 0;

var clickCell = 0;      //クリックしたマスの位置(Index)を格納：Int
var clickX = 0;         //クリックしたマスの横軸
var clickY = 0;         //クリックしたマスの縦軸
//var TemporaryScore = 0; //一時保存した点数

//周囲8方向の差：number
var aroundDifference = [
    [-1, -1], [ 1,  1], //[-1,-1]=左上, [1,1]=右下
    [-1,  0], [ 1,  0], //[-1,0]=左真ん中, [1,0]=右真ん中
    [-1,  1], [ 1, -1], //[-1,1]=左下, [1,-1]=右上
    [ 0,  1], [ 0, -1], //[0,1]=真ん中下, [0,-1]=真ん中上
];
//周囲8方向の縦横斜め：<0:縦, 1:横, 2:斜め>
var aroundVHD = [2, 2, 1, 1, 2, 2, 0, 0];
//周囲8方向の記号：number
var aroundMarks = [];

//リーチ阻止の場合は firstと secondが一致かつ自分の記号でないときのために使用
var firstStop = 0;          //最初に自分のマークと違うか判定する記号を格納
var secondStop = 0;         //次に自分のマークと違うか判定する記号を格納

//３つ並んだかの場合は firstと secondが Trueに変更されたらビンゴしたと判断するために使用
var firstLine = false;      //最初に揃ったか判定で使うフラグ
var secondLine = false;     //次に揃ったか判定で使うフラグ

var turnCnt = 1;            //ターンの(何巡目かで使用する)回数

//実行時読み込み
$(function(){
    FromIntToBoard();           //数値から盤面の文字に変換する
    ReflectScore();             //スコアを画面に反映する
    ReflectMission();           //各プレイヤーの縦横斜めの状態を表示
    $(".player_turn").text(player+1);   //どのプレイヤーのターンか表示
    
    $('table#tab tbody tr td').on("click", function(){      //マスをクリックしたとき
        FromBoardToInt();       //盤面の文字から数値に変換する
        ReflectBoardStatus();   //盤面の状態リストに今現在の盤面の情報を渡す
        clickCell = $('table#tab tbody tr td').index(this);     //クリックした位置を格納
        if (ClickData() === false) {    //クリックした情報を取得し、エラーがあれば終了
            return;
        }
        CallStopReaching();     //リーチ阻止判定呼び出し
        CallLineUpThree();      //3つ並んだか判定呼び出し
        console.log("プレイヤー" + (player+1) + "の得点：" + PlayerScore[player]);
        console.log("縦横斜め：" + PlayerDirections);
        ReflectElement();       //計算結果を画面の要素に数値を渡す
        FromIntToBoard();       //数値から盤面の文字に変換する
        ReflectScore();         //スコアを画面に反映する
        ReflectMission();       //各プレイヤーの縦横斜めの状態を表示
        TurnPlint();            //何ターンか計算して表示
        console.log("ターンの(何巡目かで使用する)回数："+turnCnt);
        if(JudgWin() || turnCnt >= 60) {     //６０マス以上埋まるまたは勝利条件を満たすとき
            showWinDisplay();
            turnCnt = 1;                //ターンの(何巡目かで使用する)回数リセット
            return;
        }
        player = (player + 1) % 4       //プレイヤーのターンを進める
        turnCnt++;                      //ターンの(何巡目かで使用する)回数
        $(".player_turn").text(player+1);   //どのプレイヤーのターンか表示
        BotAutoCheck(1);                //自動で記号を置くか確認する(最後)
    });
});

//数値から盤面の文字に変換する
function FromIntToBoard() {

    $('table#tab tbody tr td').each(function(){
        //要素から取得した数値を対応した記号に変換
        if($(this).text() == 0){
            $(this).text("　");
        } else if($(this).text() == 1){
            $(this).text("●");
            $(this).addClass("player1");
        } else if($(this).text() == 2){
            $(this).text("✕");
            $(this).addClass("player2");
        } else if($(this).text() == 3){
            $(this).text("▲");
            $(this).addClass("player3");
        } else if($(this).text() == 4){
            $(this).text("◆");
            $(this).addClass("player4");
        }
    });
}

//盤面の文字から数値に変換する
function FromBoardToInt() {

    $('table#tab tbody tr td').each(function(){
        //要素から取得した記号を対応した数値に変換
        if($(this).text() == "　"){
            $(this).text(0);
        } else if($(this).text() == "●"){
            $(this).text(1);
        } else if($(this).text() == "✕"){
            $(this).text(2);
        } else if($(this).text() == "▲"){
            $(this).text(3);
        } else if($(this).text() == "◆"){
            $(this).text(4);
        }
    });
}

//クリックした情報を取得
function ClickData(){
    clickX = clickCell % 10;                //余りを横軸に格納
    clickY = Math.floor(clickCell / 10);    //商を縦軸に格納
    if (BoardList[clickY][clickX] != 0) {
        alert("もう他の人の記号が置かれています");
        ReflectElement();       //計算結果を画面の要素に数値を渡す
        FromIntToBoard();       //数値から盤面の文字に変換する
        return false;
    }
    BoardList[clickY][clickX] = player + 1; //自分のプレイヤーの値に変える
    //配列の始まりが 0 だから横と縦は -1 した値になっている
    //console.log("横：" + clickX  + " 縦：" + clickY);
    return true;
}

//リーチを止めたか判定呼び出し
function CallStopReaching(){
    //i<0(○○●):端に置いた場合の計算, 1(○●○):真ん中に置いた場合の計算>
    for (var i=0; i<2; i++) {
        //j<周囲8方向の情報を配列から呼び出すための数値>
        for (var j=0; j<8; j++) {
            /*
                LineUpThree(~~~);<3つ並んだか判定を行う関数>
                aroundVHD[j]<8方向それぞれの縦横斜めの情報>
                clickX<クリックした横軸の位置>
                clickY<クリックした縦軸の位置>
                aroundDifference[j][0]<周囲8方向の差_横(X座標)>
                aroundDifference[j][1]<周囲8方向の差_縦(Y座標)>
            */
            StopReaching(i, j, aroundVHD[j], clickX, clickY, aroundDifference[j][0], aroundDifference[j][1]);
            if (i === 1) {  //iが１のとき、周囲(2個の値で4回)の計算だから計算数を減らす
                j++;    //jをインクリメント
            }
        }
        console.log("周囲の記号Stop：" + aroundMarks);
    }
    aroundMarks = [];   //周囲8方向の記号のデータを削除
}

//リーチを止めたか判定
/*
    status<0(○○●):端に置いた場合の計算, 1(○●○):真ん中に置いた場合の計算>
    aroundNum<周囲8方向のどの位置か → ex:左上(0),右下(1)>
        ※左上から始まり、対角線(右下)に行き、元の所(左上)から反時計回りの順
    VHD<周囲8方向の位置に対応した縦横斜めの情報, 0:縦, 1:横, 2:斜め>
*/
function StopReaching(status, aroundNum, VHD, x, y, move_x, move_y){
    if (status == 0) {  //１のとき、端に置いた場合の計算
        for (var i=0; i<2; i++){        //2回 Loopする
            x += move_x;    //置いた位置から move_x(横の差)分ずらす
            y += move_y;    //置いた位置から move_y(縦の差)分ずらす
            if (x < 0 || x > 9 || y < 0|| y > 9) {   //縦または横が盤面の範囲外のとき
                //console.log("盤面の範囲外です");
                aroundMarks.push(0);                //置いた位置の周りの記号(数値)を取得
                return;     //処理を抜ける
            }
            if (i == 0) {               //最初の Loopのとき
                aroundMarks.push(BoardList[y][x]);  //置いた位置の周りの記号(数値)を取得
                //console.log("周囲の盤面の数値：" + BoardList[y][x]);
            } 
            //console.log("盤面の数値：" + BoardList[y][x]);
            if (BoardList[y][x] != player+1) {    //自分の記号と一致するとき
                if (i == 0) {           //最初の Loopのとき
                    firstStop = BoardList[y][x];
                } else {                //それ以外(次の Loop)のとき
                    secondStop = BoardList[y][x];
                }
            }
        }
        if (firstStop === secondStop && firstStop !== (player+1) && firstStop != 0) {    //置いたマスの隣とその先のマスが一致かつ自分の記号と一致しないとき
            //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
            if (JudgingVHD(firstStop-1, VHD) === true) {
                PlayerScore[player] += 1;   //止めたプレイヤーの点数を加算(+1)
            }
            console.log("端でビンゴStop：" + PlayerScore[player]);
        }
    } else {            //それ以外のとき、真ん中に置いた場合の計算
        for (var i=0; i<2; i++){        //2回 Loopする
            if (aroundMarks[aroundNum+i] != player+1) {    //周囲の記号が自分の記号と一致するとき
                if (i == 0) {           //最初の Loopのとき
                    firstStop = aroundMarks[aroundNum+i];
                } else {                //それ以外(次の Loop)のとき
                    secondStop = aroundMarks[aroundNum+i];
                }
            }
        }
        if (firstStop === secondStop && firstStop != (player+1) && firstStop != 0) {    //向かい合うマスが自分の記号のとき
            //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
            if (JudgingVHD(firstStop-1, VHD) === true) {
                PlayerScore[player] += 1;   //そのプレイヤーの点数を加算(+1)
            }
            console.log("真ん中でビンゴStop：" + PlayerScore[player]);
        }
    }
    //フラグをリセット
    firstStop = false;
    secondStop = false;
}

//3つ並んだか判定呼び出し
function CallLineUpThree(){
    //i<0(○○●):端に置いた場合の計算, 1(○●○):真ん中に置いた場合の計算>
    for (var i=0; i<2; i++) {
        //j<周囲8方向の情報を配列から呼び出すための数値>
        for (var j=0; j<8; j++) {
            /*
                LineUpThree(~~~);<3つ並んだか判定を行う関数>
                aroundVHD[j]<8方向それぞれの縦横斜めの情報>
                clickX<クリックした横軸の位置>
                clickY<クリックした縦軸の位置>
                aroundDifference[j][0]<周囲8方向の差_横(X座標)>
                aroundDifference[j][1]<周囲8方向の差_縦(Y座標)>
            */
            LineUpThree(i, j, aroundVHD[j], clickX, clickY, aroundDifference[j][0], aroundDifference[j][1]);
            if (i === 1) {  //iが１のとき、周囲(2個の値で4回)の計算だから計算数を減らす
                j++;    //jをインクリメント
            }
        }
        console.log("周囲の記号Line：" + aroundMarks);
    }
    aroundMarks = [];   //周囲8方向の記号のデータを削除
}

//三つ並んだか判定
/*
    status<0(○○●):端に置いた場合の計算, 1(○●○):真ん中に置いた場合の計算>
    aroundNum<周囲8方向のどの位置か → ex:左上(0),右下(1)>
        ※左上から始まり、対角線(右下)に行き、元の所(左上)から反時計回りの順
    VHD<周囲8方向の位置に対応した縦横斜めの情報, 0:縦, 1:横, 2:斜め>
*/
function LineUpThree(status, aroundNum, VHD, x, y, move_x, move_y){
    if (status == 0) {  //１のとき、端に置いた場合の計算
        for (var i=0; i<2; i++){        //2回 Loopする
            x += move_x;    //置いた位置から move_x(横の差)分ずらす
            y += move_y;    //置いた位置から move_y(縦の差)分ずらす
            if (x < 0 || x > 9 || y < 0|| y > 9) {   //縦または横が盤面の範囲外のとき
                //console.log("盤面の範囲外です");
                aroundMarks.push(0);                //置いた位置の周りの記号(数値)を取得
                return;     //処理を抜ける
            }
            if (i == 0) {                       //最初の Loopのとき
                aroundMarks.push(BoardList[y][x]);  //置いた位置の周りの記号(数値)を取得
                //console.log("周囲の盤面の数値：" + BoardList[y][x]);
            } 
            //console.log("盤面の数値：" + BoardList[y][x]);
            if (BoardList[y][x] == player+1) {    //自分の記号と一致するとき
                if (i == 0) {                   //最初の Loopのとき
                    firstLine = true;
                } else {                        //それ以外(次の Loop)のとき
                    secondLine = true;
                }
            }
        }
        if (firstLine === true && secondLine === true) {    //置いたマスの隣とその先のマスが自分の記号と一致するとき
            //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
            if (JudgingVHD(player, VHD) === true) {
                PlayerDirections[player][aroundVHD[aroundNum]] = true; 
                PlayerScore[player] += 3;   //そのプレイヤーの点数を加算(+3)
            }
            console.log("端でビンゴLine：" + PlayerScore[player]);
        }
    } else {            //それ以外のとき、真ん中に置いた場合の計算
        for (var i=0; i<2; i++){        //2回 Loopする
            if (aroundMarks[aroundNum+i] == player+1) {    //周囲の記号が自分の記号と一致するとき
                if (i == 0) {                   //最初の Loopのとき
                    firstLine = true;
                } else {                        //それ以外(次の Loop)のとき
                    secondLine = true;
                }
            }
        }
        if (firstLine === true && secondLine === true) {    //向かい合うマスが自分の記号のとき
            //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
            if (JudgingVHD(player, VHD) === true) {
                PlayerDirections[player][aroundVHD[aroundNum]] = true; 
                PlayerScore[player] += 3;   //そのプレイヤーの点数を加算(+3)
            }
            console.log("真ん中でビンゴLine：" + PlayerScore[player]);
        }
    }
    //フラグをリセット
    firstLine = false;
    secondLine = false;
}

//縦・横・斜めのどれか判定(Judgment whether vertical, horizontal or diagonal)判定
function JudgingVHD(check, StatusVHD){
    if(PlayerDirections[check][StatusVHD] == false){   //３つ並んでいないとき
        return true;    //そのプレイヤーの縦・横・斜めの指定した所は揃っていないと返す(True)
    }
    return false;       //そのプレイヤーの縦・横・斜めの指定した所は揃っていると返す(False)
}

//盤面の状態リストに反映する(Reflect in the board status list)
function ReflectBoardStatus(){
    ReflectProcess(1);  //反映処理(1):
}

//要素に反映する(Reflect in the element)
function ReflectElement(){
    ReflectProcess(2);  //反映処理(2):
}

//反映処理
function ReflectProcess(switchInt) {
    var vertical = 0;       //縦
    var horizontal = 0;     //横
    $('table#tab tbody tr td').each(function(){     //盤面の要素分Loop
        if (switchInt == 1) {       //1のとき、盤面の状態を変更
            /*  thisでそれぞれの要素を取得し、盤面の状態リストに反映する
                空白を 0 に置き換えて代入
            */
            BoardList[vertical][horizontal] = $(this)[0].innerText;
        } else {                    //それ以外のとき、要素の値を変更
            //計算した盤面の状態を格納し、thisでそれぞれの要素に反映する
            $(this)[0].innerText = BoardList[vertical][horizontal];
        }
        
        if (horizontal == 9) {  //横が最後のマスまでいったとき
            vertical++;     //縦をインクリメント(次の行に移動)
        }
        
        if (horizontal < 9) {   //横が最後のマスまでいってないとき
            horizontal++;   //横をインクリメント
        } else {                //それ以外
            horizontal = 0; //横をリセット
        }
    });
}

//スコア反映
function ReflectScore() {
    $(".score1").text(PlayerScore[0]);
    $(".score2").text(PlayerScore[1]);
    $(".score3").text(PlayerScore[2]);
    $(".score4").text(PlayerScore[3]);
}

//勝利判定
function JudgWin() {
    lines = PlayerDirections[player];
    if(lines[0] && lines[1] && lines[2]) {
        return true;
    }else {
        return false;
    }
}

//勝利画面表示
function showWinDisplay() {
    $(".layer").show();     //背景(半透明)を表示設定
    $(".win").show();       //Win(勝ち)を表示設定
    var winnerPlayer = PlayerScore.indexOf(Math.max(...PlayerScore));   //スコアから勝利プレイヤーのインデックスを取得
    $(".winner_player").text(winnerPlayer+1);                           //勝利プレイヤーを表示
}

//各プレイヤーの縦横斜めの状態を表示
function ReflectMission() {
    for(var i = 0; i < PlayerDirections.length; i++) {          //縦横斜めのY軸
        for(var j = 0; j < PlayerDirections[i].length; j++) {   //縦横斜めのX軸
            if(PlayerDirections[i][j]) {    //指定された縦横斜めの情報が Trueのとき
                //対応するテーブルに"〇"を表示
                $('#mission_table tr').eq(j+1).children('td').eq(i+1).text("〇");
            }
        }
    }
}

//何ターンか計算して表示
function TurnPlint(){
    $('.turn_count').text(Math.floor(turnCnt/4)+1);
}

//自動で記号を置くか確認する
function BotAutoCheck(status){
    //そのプレイヤーの自動化CheckBoxがチェックされていないとき
    if ($(".p_check").eq(PlayerPlace[player]).prop("checked") == false) {
        if (status == 0) {
            alert("自動化CheckBoxがチェックされていません");
            exit;
        }
    } else if ($(".p_check").eq(PlayerPlace[player]).prop("checked") == true) {
        if (status == 1) {
            console.log(PlayerPlace[player]);
            $('button.bot').trigger("click");
        }
    }
}