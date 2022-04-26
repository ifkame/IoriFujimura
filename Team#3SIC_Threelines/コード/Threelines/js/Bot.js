var BotSet = [];            //Botの記号または他の記号がある位置:number
var MarkSet = [];           //他の記号(数値)を格納:number

var serachValue = 0;        //置く候補のマスの位置(Index)を格納：Int
var searchX = 0;            //置く候補のX座標(横)
var searchY = 0;            //置く候補のY座標(縦)

var Bot1stCandidate = [];   //Botが１番に置く候補になる位置:number
var Bot2ndCandidate = [];   //Botが２番に置く候補になる位置:number
var eqIndex = 0;            //Botがクリックした位置(Index):number

//実行時読み込み
$(function(){
    $('button.bot').on("click", function(){       //自動(auto)ボタンクリックしたとき
        BotAutoCheck(0);            //自動で記号を置くか確認する(最初)
        
        FromIntToBoard();           //数値から盤面の文字に変換する
        ReflectScore();             //スコアを画面に反映する
        ReflectMission();           //各プレイヤーの縦横斜めの状態を表示
        $(".player_turn").text(player+1);   //どのプレイヤーのターンか表示

        console.log("---------------------------");
        //盤面を計算して記号を置き、そのマスをクリックしたことにする
        CalcBot();
        clickCell = eqIndex;     //クリックした位置を格納
        console.log("Botの押した位置："+eqIndex+", クリックした位置："+clickCell);

        //クリックした情報を取得してエラーがある(既にそのマスに記号が置かれている)とき
        if (ClickData() === false) {
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
        console.log("プレイヤー(最初)："+player);
        player = (player + 1) % 4       //プレイヤーのターンを進める
        turnCnt++;                      //ターンの(何巡目かで使用する)回数
        $(".player_turn").text(player+1);   //どのプレイヤーのターンか表示
        console.log("プレイヤー(最後)："+player);
        BotAutoCheck(1);                //自動で記号を置くか確認する(最後)
    });
});

//盤面を計算して記号を置く
function CalcBot(){
    //Botの打ち方をランダムで決める
    var BotRnd = Math.floor(Math.random() * 10) + 1;
    if (0 < BotRnd && BotRnd < 6) {     //乱数が1~5のとき
        console.log("3つ並ぶ所を探す");
        BotSearchBoard(0);      //3つ並ぶ所を探す
    } else {                            //それ以外(乱数が6~10)のとき
        console.log("リーチを止める所を探す");
        BotSearchBoard(1);      //リーチを止める所を探す
    }
    //Botが１番と２番に置く候補になる位置がないとき
    if ((typeof(Bot1stCandidate) === "undefined" || !Bot1stCandidate.length) && (typeof(Bot2ndCandidate) === "undefined" || !Bot2ndCandidate.length)) {
        BotSet = [];            //Botの記号または他の記号がある位置を削除(リセット)
        BotSearchBoard(0);      //3つ並ぶ所を探す
        //※リーチを止める所を探したときに置く候補がないときが発生するから
    }
    console.log("第１候補："+Bot1stCandidate+", 第２候補："+Bot2ndCandidate);
    //Botが１番に置く候補になる位置があるとき
    if (Bot1stCandidate.length) {
        //１番に置く候補の中からランダムに取得
        var BotRnd1st = Math.floor( Math.random() * Bot1stCandidate.length);
        eqIndex = Bot1stCandidate[BotRnd1st];   //Botがクリックした位置(Index)として取得
    //Botが２番に置く候補になる位置があるかつ１番に置く候補がないとき
    } else if (Bot2ndCandidate.length && (typeof(Bot1stCandidate) === "undefined" || !Bot1stCandidate.length)) {
        //２番に置く候補の中からランダムに取得
        var BotRnd2nd = Math.floor( Math.random() * Bot2ndCandidate.length );
        eqIndex = Bot2ndCandidate[BotRnd2nd];   //Botがクリックした位置(Index)として取得
    } else {        //それ以外=自分の記号がないとき(最初)
        SetMineMark();          //自分の記号をランダムで置く
    }
    BotSet = [];                //Botの記号または他の記号がある位置を削除(リセット)
    Bot1stCandidate = [];       //Botが１番に置く候補になる位置を削除(リセット)
    Bot2ndCandidate = [];       //Botが２番に置く候補になる位置を削除(リセット)
    //return eqIndex;             //Botがクリックした位置(Index)を返却
}

//自分の記号または他の人の記号がある場所を探す
//ActStatus<0:3つ並ぶ所を探す, 1:リーチを止める所を探す>
function BotSearchBoard(ActStatus){
    for (var i=0;i<9;i++) {         //縦(0~9)
        for (var j=0;j<9;j++) {     //横(0~9)
            if (BoardList[i][j] == player+1 && ActStatus == 0) {        //自分の記号と同じかつ3つ並ぶ所を探すとき
                BotSet.push((i*10)+j);          //自分の記号と同じ場所(数値)を格納
            //空白以外かつ自分の記号と違うかつリーチを止める所を探すとき
            } else if (BoardList[i][j] != 0 && BoardList[i][j] != player+1 && ActStatus == 1) {
                BotSet.push((i*10)+j);          //自分の記号と違う場所(数値)を格納
                MarkSet.push(BoardList[i][j]);  //他の記号(数値)を格納
            }
        }
    }
    console.log("記号の位置："+BotSet);
    BotSet.forEach(function(botsetvalue){   //Botの記号または他の記号がある位置の数だけLoop
        serachValue = botsetvalue;              //置く候補のマスの位置
        searchX = botsetvalue % 10;             //置く候補のマスの横軸の位置
        searchY = Math.floor(botsetvalue / 10); //置く候補のマスの縦軸の位置
        //console.log("置く候補の位置："+serachValue+", 横："+searchX+", 縦："+searchY);
        if (ActStatus == 0) {               //ActStatusが0のとき
            CallSearchLine();           //3つ並ぶ所を探す
        } else if (ActStatus == 1) {        //ActStatusが0のとき
            CallSearchStop();    //リーチを止める所を探す
        }
    });
}

//3つ並ぶ所を探す関数を呼び出す
function CallSearchLine(){
    //i<0(○○●):端に置いた場合の計算, 1(○●○):真ん中に置いた場合の計算>
    for (var i=0; i<2; i++) {
        //j<周囲8方向の情報を配列から呼び出すための数値>
        for (var j=0; j<8; j++) {
            /*
                SearchLineUpThree(~~~);<3つ並ぶ所を探す関数>
                aroundVHD[j]<8方向それぞれの縦横斜めの情報>
                searchX<置く候補のマスの横軸の位置>
                searchY<置く候補のマスの縦軸の位置>
                aroundDifference[j][0]<周囲8方向の差_横(X座標)>
                aroundDifference[j][1]<周囲8方向の差_縦(Y座標)>
            */
            SearchLineUpThree(i, j, aroundVHD[j], searchX, searchY, aroundDifference[j][0], aroundDifference[j][1]);
        }
    }
}

//3つ並ぶ所を探す
/*
    status<0(○○●):端に置いた場合の計算, 1(○●○):真ん中に置いた場合の計算>
    aroundNum<周囲8方向のどの位置か → ex:左上(0),右下(1)>
        ※左上から始まり、対角線(右下)に行き、元の所(左上)から反時計回りの順
    VHD<周囲8方向の位置に対応した縦横斜めの情報, 0:縦, 1:横, 2:斜め>
*/
function SearchLineUpThree(status, aroundNum, VHD, x, y, move_x, move_y){
    if (status == 0) {  //１のとき、端に置いた場合の計算
        for (var i=0; i<2; i++){        //2回 Loopする
            x += move_x;    //置いた位置から move_x(横の差)分ずらす
            y += move_y;    //置いた位置から move_y(縦の差)分ずらす
            if (x < 0 || x > 9 || y < 0 || y > 9) {      //縦または横が盤面の範囲外のとき
                return;                             //処理を抜ける
            }
            if (i == 0 && BoardList[y][x] == 0) {   //最初のLoopかつ置く場所が空白のとき
                //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
                if (JudgingVHD(player, VHD) === true) {
                    Bot2ndCandidate.push((y*10)+x);         //２番目の候補として場所を追加
                }
            }
            if (BoardList[y][x] == player+1) {      //自分の記号と一致するとき
                if (i == 0) {                       //最初のLoopのとき
                    firstLine = true;                   //その方向の隣のマスが自分の記号と同じ
                }
            }
            if (firstLine == true && BoardList[y][x] == 0) {     //3つ並ぶ所かつ次のLoopかつ置く場所が空白のとき
                //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
                if (JudgingVHD(player, VHD) === true) {
                    Bot1stCandidate.push((y*10)+x);     //１番目の候補として場所を追加
                }
            }
        }
    } else {            //それ以外のとき、真ん中に置いた場合の計算
        for (var i=0; i<2; i++){        //2回 Loopする
            x += move_x;    //置いた位置から move_x(横の差)分ずらす
            y += move_y;    //置いた位置から move_y(縦の差)分ずらす
            if (x < 0 || x > 9 || y < 0 || y > 9) {     //縦または横が盤面の範囲外のとき
                return;             //処理を抜ける
            }
            if (BoardList[y][x] == 0 && i == 0) {       //周囲が空白かつ最初のLoopのとき
                firstLine = true;   //その方向の隣のマスが自分の記号と同じ
            }
            if (firstLine == true && i == 1) {          //3つ並ぶ所かつ次のLoopのとき
                if (BoardList[y][x] == player+1) {      //周囲のその先が自分の記号と一致するとき
                    //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
                    if (JudgingVHD(player, VHD) === true) {
                        x -= move_x;    //置いた位置から move_x(横の差)分戻す
                        y -= move_y;    //置いた位置から move_y(縦の差)分戻す
                        if (BoardList[y][x] == 0) {     //置く場所が空白のとき
                            Bot1stCandidate.push((y*10)+x);     //１番目の候補として場所を追加
                        }
                    }
                }
                
            }
        }
    }
    //フラグをリセット
    firstLine = false;
    secondLine = false;
}

//リーチを止める所を探す関数を呼び出す
function CallSearchStop(){
    //j<周囲8方向の情報を配列から呼び出すための数値>
    for (var j=0; j<8; j++) {
        /*
            SearchLineUpThree(~~~);<3つ並ぶ所を探す関数>
            aroundVHD[j]<8方向それぞれの縦横斜めの情報>
            searchX<置く候補のマスの横軸の位置>
            searchY<置く候補のマスの縦軸の位置>
            aroundDifference[j][0]<周囲8方向の差_横(X座標)>
            aroundDifference[j][1]<周囲8方向の差_縦(Y座標)>
        */
        SearchStopReaching(aroundVHD[j], searchX, searchY, aroundDifference[j][0], aroundDifference[j][1]);
    }
}

//リーチを止める所を探す
/*
    VHD<周囲8方向の位置に対応した縦横斜めの情報, 0:縦, 1:横, 2:斜め>
*/
function SearchStopReaching(VHD, x, y, move_x, move_y){
    var OtherMark = BoardList[y][x];
    for (var i=0; i<2; i++){        //2回 Loopする
        x += move_x;    //置いた位置から move_x(横の差)分ずらす
        y += move_y;    //置いた位置から move_y(縦の差)分ずらす
        if (x < 0 || x > 9 || y < 0 || y > 9) {   //縦または横が盤面の範囲外のとき
            return;     //処理を抜ける
        }
        if (i == 0) {   //最初の Loopのとき
            firstStop = BoardList[y][x];    //その方向の周囲の記号(数値)を格納
        } else {        //それ以外(次の Loop)のとき
            secondStop = BoardList[y][x];   //その方向の周囲の先の記号(数値)を格納
        }
    }
    //置いたマスの隣が今回の記号と一致かつその先のマスが空白のとき
    if (firstStop == OtherMark && secondStop == 0) {
        //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
        if (JudgingVHD(OtherMark-1, VHD) === true) {
            Bot1stCandidate.push((y*10)+x);     //１番目の候補として場所を追加
        }
    }
    //置いたマスの隣が空白かつその先のマスが今回の記号と一致するとき
    if (firstStop == 0 && secondStop == OtherMark) {
        //そのプレイヤーの縦・横・斜めの指定した所は揃っていないとき(True)
        if (JudgingVHD(OtherMark-1, VHD) === true) {
            x -= move_x;    //置いた位置から move_x(横の差)分戻す
            y -= move_y;    //置いた位置から move_y(縦の差)分戻す
            Bot1stCandidate.push((y*10)+x);     //１番目の候補として場所を追加
        }
    }
    //フラグをリセット
    firstStop = 0;
    secondStop = 0;
}

//記号がない所に自分の記号を置く
function SetMineMark(){
    eqIndex = Math.floor(Math.random() * 100);    //盤面からランダムで位置を取得
    var x = eqIndex % 10;               //縦
    var y = Math.floor(eqIndex / 10);   //横
    console.log("縦：" + y + ", 横：" + x);
    if (BoardList[y][x] == 0) {     //記号が置かれていないとき
        return;     //処理を抜ける
    }
    SetMineMark();      //記号が既に置かれているので再実行
}