//盤面の状態：Int<なし:0,〇:1,×:2,△:3,◇:4>
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
var Directions = [
    [false, false, false],    //プレイヤー１
    [false, false, false],    //プレイヤー２
    [false, false, false],    //プレイヤー３
    [false, false, false]     //プレイヤー４
];

//プレイヤーの得点：Int
var PlayerScore = [0, 0, 0, 0];

//どのプレイヤーのターンか：Int<0：プレイヤー１, 1：プレイヤー2, 2：プレイヤー3, 3：プレイヤー4>
var player = 0;

//実行時読み込み
$(function(){
    BoardIntChange();      //盤面数値変換処理
    $('table#tab tbody tr td').on("click", function(){      //マスのクリック時の処理
        
    });
});

//盤面数値変換
function BoardIntChange() {

    $('table#tab tbody tr td').each(function(){
        // 
        if($(this).text() == 0){
            $(this).text("　");
        } else if($(this).text() == 1){
            $(this).text("〇");
        } else if($(this).text() == 2){
            $(this).text("✕");
        } else if($(this).text() == 3){
            $(this).text("△");
        } else if($(this).text() == 4){
            $(this).text("◇");
        }
    });
}

//リーチを止めたか判定
function StopReaching(){
    
}

//三つ並んだか判定
function LineUpThree(){
    for(flag = 0;flag == 1;){
        if(player == 1){
            var align = Directions[0];  //プレイヤー1の揃ってる情報
            
            if(align[0] == true){       //縦
                flag == 1
            }else{

            }
            if(align[1] == true){      //横
                flag == 1
            }else{
                
            }
            if(align[1] == true){      //斜め
                flag == 1
            }else{
                
            }     
        }
        
    }
}

//縦・横・斜めのどれか判定(Judgment whether vertical, horizontal or diagonal)判定
function JudgingVHD(){

}