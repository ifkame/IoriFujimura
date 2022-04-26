<?php 
const SPACE  = 0;	           /* 空点(石が置かれていない) */
const MARU = 1;	           /* 〇                       */
const BATU = 2;	           /* ×                       */
const SANKAKU = 3;	           /* △                    */
const SIKAKU = 4;	           /* □                     */
const OUT = 5;	           /* 盤外                     */

const BOARD_SIZE = 21;      /* 碁盤の大きさ             */


/* 碁盤 */
$board[BOARD_SIZE][BOARD_SIZE] = 21;

/* 碁盤の初期化 */
for ($y = 1; $y < (BOARD_SIZE-1); $y++) {
  for($x = 1; $x < (BOARD_SIZE-1); $x++ ){
    $board[$y][$x] = SPACE;
  }
}
for( $y=0; $y < BOARD_SIZE; $y++ ){
  $board[$y][0]= OUT;
  $board[$y][BOARD_SIZE-1] = OUT;
  $board[0][$y] = OUT;
  $board[BOARD_SIZE-1][$y] = OUT;
}



/* 碁盤の表示のための文字 */
$stone[] = [1, 2, 3, 4];

printf( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[ 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19]<br />" );
for ($y = 1; $y < (BOARD_SIZE-1); $y++) {
  printf( "[%2d] ", $y );
  for($x = 1; $x < (BOARD_SIZE-1); $x++ ){
    //printf( " %c", $stone[$board[$y][$x]] );
    print(ArrayChange($stone[$board[$y][$x]]));
  }
  echo "<br />";
}

function ArrayChange($ArrayInt){
  if($ArrayInt == 1){
    return "＋";
  } elseif ($ArrayInt == 2){
    return "＊";
  } elseif ($ArrayInt == 3){
    return "〇";
  } elseif ($ArrayInt == 4){
    return "？";
  }
  return "△";
}
?>